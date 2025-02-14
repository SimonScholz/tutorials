package dev.simonscholz

import com.mongodb.client.MongoClient
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider

class ShedLockConfig {
    @Produces
    @Singleton
    fun lockProvider(mongo: MongoClient): LockProvider = MongoLockProvider(mongo.getDatabase("shedlock"))
}
