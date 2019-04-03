package com.directus.domain.service

import com.directus.domain.model.Role
import com.directus.domain.model.RoleReceiver
import com.directus.endpoints.exception.BadRequestException
import com.directus.repository.RoleRepository
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*

object RoleService {
    fun getRoles(): SizedIterable<Role> = RoleRepository.getAll()
    fun getRole(id: Int): Role? = RoleRepository.getById(id)
    fun deleteRole(id: Int) = RoleRepository.remove(id)

    fun createRole(fields: Role.() -> Unit) =  Role.new(init = fields)

    fun createRoleFromPayload(payload: RoleReceiver) = createRole {
        name = payload.name ?: throw BadRequestException("Name is required")
        description = payload.description
        ipWhitelist = payload.ipWhitelist
        navBlacklist = payload.navBlacklist
        externalId = UUID.randomUUID().toString()
    }
}
