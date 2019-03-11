package repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

interface RepositoryInterface<Model> {

    suspend fun <Q> asyncQuery(
        block: () -> Q
    ): Q =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}