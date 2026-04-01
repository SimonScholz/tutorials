#!/usr/bin/env kotlin

@file:DependsOn("io.swagger.parser.v3:swagger-parser:2.1.39")

import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import java.io.File

// ================= CONFIG =================

val inputFile = "./raw-fft-api.yaml"
val outputFile = "./stripped-fft-api.yaml"

// Only keep these paths (empty = keep all)
val pathsToKeep =
    setOf(
        "/api/inboundprocesses/{inboundProcessId}",
        "/api/inboundprocesses/{inboundProcessId}/purchaseorder",
    )

// ===========================================

println("Parsing OpenAPI file: $inputFile")

val options =
    ParseOptions().apply {
        isResolve = true
        isResolveFully = false
        isFlatten = true
    }

val result: SwaggerParseResult = OpenAPIV3Parser().readLocation(inputFile, null, options)

if (result.messages.isNotEmpty()) {
    println("Parser messages:")
    result.messages.forEach { println(" - $it") }
}

var openAPI: OpenAPI = result.openAPI ?: error("Failed to parse OpenAPI spec")

println("Applying OpenAPI Generator normalization...")

// ================= PATH FILTERING =================
if (pathsToKeep.isNotEmpty()) {
    println("Filtering paths...")

    val newPaths = Paths()

    openAPI.paths
        ?.filterKeys { it in pathsToKeep }
        ?.forEach { (path, item) ->
            newPaths.addPathItem(path, item)
        }

    openAPI.paths = newPaths
}

// ================= COLLECT USED SCHEMAS =================

val usedSchemas = mutableSetOf<String>()

fun collectSchema(schema: Schema<*>?) {
    if (schema == null) return

    // Handle $ref
    schema.`$ref`?.let {
        val name = it.substringAfterLast("/")
        if (usedSchemas.add(name)) {
            val refSchema = openAPI.components?.schemas?.get(name)
            collectSchema(refSchema)
        }
        return
    }

    // Properties
    schema.properties?.values?.forEach {
        collectSchema(it as Schema<*>)
    }

    // Array items
    schema.items?.let {
        collectSchema(it)
    }

    // Composed schemas
    schema.allOf?.forEach { collectSchema(it as Schema<*>) }
    schema.oneOf?.forEach { collectSchema(it as Schema<*>) }
    schema.anyOf?.forEach { collectSchema(it as Schema<*>) }

    when (val additional = schema.additionalProperties) {
        is Schema<*> -> collectSchema(additional)
    }

    // Not required but safe: map value schemas
    schema.not?.let { collectSchema(it) }

    schema.discriminator?.mapping?.values?.forEach { ref ->
        val name = ref.substringAfterLast("/")
        if (usedSchemas.add(name)) {
            collectSchema(openAPI.components?.schemas?.get(name))
        }
    }
}

// Traverse paths
openAPI.paths?.values?.forEach { pathItem ->
    pathItem.readOperations().forEach { op ->
        op.requestBody?.content?.values?.forEach {
            collectSchema(it.schema)
        }
        op.responses?.values?.forEach { response ->
            response.content?.values?.forEach {
                collectSchema(it.schema)
            }
        }
        op.parameters?.forEach { param ->
            collectSchema((param)?.schema)
        }
    }
}

println("Used schemas: ${usedSchemas.size}")

// ================= REMOVE UNUSED SCHEMAS =================

val schemas = openAPI.components?.schemas ?: emptyMap()

val pruned = schemas.filterKeys { it in usedSchemas }

openAPI.components.schemas = pruned.toMutableMap()

println("Pruned schemas from ${schemas.size} to ${pruned.size}")

// Remove redundant components due to allOf flattening and anyOf simplification
openAPI.components?.schemas?.values?.forEach { schema ->

    // Remove duplicate discriminator property
    schema.allOf?.forEach { part ->
        if (part is Schema<*>) {
            val props = part.properties
            if (props != null && props.containsKey("action")) {
                props.remove("action")
            }
        }
    }

    // Remove useless anyOf (only required constraints)
    val anyOf = schema.anyOf
    if (anyOf != null) {
        val onlyRequiredConstraints =
            anyOf.all { sub ->
                sub is Schema<*> &&
                    sub.required != null &&
                    sub.properties == null
            }

        if (onlyRequiredConstraints) {
            schema.anyOf = null
        }
    }
}

// ================= WRITE OUTPUT =================

val yaml: String = Yaml.pretty(openAPI)
File(outputFile).writeText(yaml)

println("Cleaned OpenAPI written to $outputFile")
println("Done.")
