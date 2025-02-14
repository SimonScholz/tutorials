package dev.simonscholz

import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider
import javax.sql.DataSource

class ShedLockConfig {
    @Produces
    @Singleton
    fun lockProvider(dataSource: DataSource): LockProvider = JdbcLockProvider(dataSource)
}
