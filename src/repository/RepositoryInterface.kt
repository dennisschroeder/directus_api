package repository

import com.directus.model.AbstractTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

interface RepositoryInterface<T, M> {
    val table: AbstractTable

    suspend fun getById(id: Int): M?
    suspend fun getAll(): Collection<M>

    suspend fun <Q> dbQuery(
        block: () -> Q
    ): Q =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}