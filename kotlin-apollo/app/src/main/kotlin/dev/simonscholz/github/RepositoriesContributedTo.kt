package dev.simonscholz.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

fun fetchAll(
    apolloClient: ApolloClient,
    after: Optional<String?> = Optional.absent(),
): Flow<RepositoriesContributedToQuery.RepositoriesContributedTo> =
    flow {
        var nextCursor: Optional<String?> = after
        var hasNextPage = true
        while (hasNextPage) {
            val response =
                apolloClient
                    .query(RepositoriesContributedToQuery(count = 10, cursor = nextCursor))
                    .execute()
            check(response.errors.isNullOrEmpty() || response.exception == null) {
                "GraphQL errors: ${response.errors}, Exception: ${response.exception}"
            }

            response.exception?.let { throw it }

            val repositoriesContributedTo =
                requireNotNull(response.data?.viewer?.repositoriesContributedTo) {
                    "Missing repositoriesContributedTo in response"
                }

            emit(repositoriesContributedTo)

            val pageInfo = repositoriesContributedTo.pageInfo
            hasNextPage = pageInfo.hasNextPage == true
            nextCursor = if (hasNextPage) Optional.presentIfNotNull(pageInfo.endCursor) else Optional.absent()
        }
    }

suspend fun main() {
    val env = dotenv()
    val token = env["GITHUB_PAT"]

    val apolloClient =
        ApolloClient
            .Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpHeader("Authorization", "Bearer $token")
            .build()

    val allData = fetchAll(apolloClient)

    allData.toList().forEach { queries ->
        queries.nodes?.filterNotNull()?.forEach {
            println("${it.nameWithOwner} ${it.stargazerCount} ${it.primaryLanguage}")
        }
    }
}
