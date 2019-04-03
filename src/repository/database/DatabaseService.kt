package com.directus.repository.database

import com.directus.projectKey
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.ApplicationCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// @ToDo: Refactor to a more Kotlinesque style
object DatabaseService {

    val connections = hashMapOf<String, Database>()

    fun initMysql(dataSource: HikariDataSource) = Database.connect(dataSource)

    fun <T : Table> createTables(vararg tables: T) {
        connections.forEach { transaction(it.value) { SchemaUtils.create(*tables) } }
    }

    fun buildSqlConfig(
        host: String,
        port: Int,
        dbName: String,
        username: String,
        password: String
    ): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://$host:$port/$dbName"
        config.username = username
        config.password = password
        config.maximumPoolSize = 10
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

    fun <Query> transaction(projectKey: String, query: () -> Query) =
        transaction(connections[projectKey]) {
            addLogger(StdOutSqlLogger)
            query()
        }

    suspend fun <Query> asyncTransaction(projectKey: String, query: () -> Query) =
        withContext(Dispatchers.Default) {
            transaction(connections[projectKey]) {
                addLogger(StdOutSqlLogger)
                query()
            }
        }
}

val ApplicationCall.dbConnection get() = DatabaseService.connections[projectKey]