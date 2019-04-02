package com.directus.repository

import com.directus.domain.model.Revision
import com.directus.domain.model.Revisions
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.deleteWhere
import repository.RepositoryInterface

object RevisionRepository : RepositoryInterface<Revision> {
    override fun getById(id: Int): Revision? = Revision[id]
    override fun getAll(): SizedIterable<Revision> = Revision.all()
    override fun remove(id: Int): Int = Revisions.deleteWhere { Revisions.id eq id }
}