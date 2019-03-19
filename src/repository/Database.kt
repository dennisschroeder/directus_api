package com.directus.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// @ToDo: Refactor to a more Kotlinesque style
object Database {

    fun initH2() {
        Database.connect(h2Config())
    }

    fun initMysql() {
        Database.connect(mySqlConfig())
    }

    fun createTables(vararg tables: IntIdTable) = transaction { SchemaUtils.create(*tables) }

    private fun h2Config(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }


    private fun mySqlConfig(): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl="jdbc:mysql://localhost:32770/directus"
        config.username = "root"
        config.password=""
        config.maximumPoolSize=10
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.addDataSourceProperty("useServerPrepStmts", "true")
        config.addDataSourceProperty("useLocalSessionState", "true")
        config.addDataSourceProperty("rewriteBatchedStatements", "true")
        config.addDataSourceProperty("cacheResultSetMetadata", "true")
        config.addDataSourceProperty("cacheServerConfiguration", "true")
        config.addDataSourceProperty("elideSetAutoCommits", "true")
        config.addDataSourceProperty("maintainTimeStats", "true")
        config.validate()
        return HikariDataSource(config)
    }

}