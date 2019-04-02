package com.directus.repository

import com.directus.domain.model.Role
import com.directus.domain.model.Roles
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.deleteWhere
import repository.RepositoryInterface

object RoleRepository : RepositoryInterface<Role> {
    override fun getById(id: Int): Role? = Role[id]
    override fun getAll(): SizedIterable<Role> = Role.all()
    override fun remove(id: Int): Int = Roles.deleteWhere { Roles.id eq id }
}