package repository

import org.jetbrains.exposed.sql.SizedIterable

interface RepositoryInterface<Entity> {
    fun getById(id: Int): Entity?
    fun getAll(): SizedIterable<Entity>

}