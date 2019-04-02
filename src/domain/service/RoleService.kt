package com.directus.domain.service

import com.directus.domain.model.Role
import com.directus.repository.RoleRepository
import org.jetbrains.exposed.sql.SizedIterable

object RoleService {
    fun getRoles(): SizedIterable<Role> = RoleRepository.getAll()
    fun getRole(id: Int): Role? = RoleRepository.getById(id)
    fun deleteRole(id: Int) = RoleRepository.remove(id)

    fun createRole(fields: Role.() -> Unit) =  Role.new(init = fields)
}
