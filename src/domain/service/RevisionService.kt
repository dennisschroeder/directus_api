package com.directus.domain.service

import com.directus.domain.model.Revision
import com.directus.repository.RevisionRepository
import org.jetbrains.exposed.sql.SizedIterable

object RevisionService {
    fun getRevisions(): SizedIterable<Revision> = RevisionRepository.getAll()
    fun getRevision(id: Int): Revision? = RevisionRepository.getById(id)
    fun deleteRevision(id: Int) = RevisionRepository.remove(id)
}
