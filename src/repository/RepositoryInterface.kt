package repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

interface RepositoryInterface<T, M> {
    val table: T

    suspend fun <M> dbQuery(
        block: () -> M): M =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

    fun mapToModel(row: ResultRow): M
}