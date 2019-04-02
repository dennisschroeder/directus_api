package com.directus.repository

import com.directus.domain.model.User
import com.directus.domain.model.UserStatus
import com.directus.domain.model.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

import repository.RepositoryInterface

object UserRepository : RepositoryInterface<User> {
    override fun getById(id: Int) = User.findById(id)
    override fun getAll() = User.all()
    override fun remove(id: Int) = Users.deleteWhere { Users.id eq id }

    fun findByEmail(email: String) = User.find { Users.email eq email }.singleOrNull()
    fun findActiveByEmail(email: String) = User.find {
        (Users.email eq email) and (Users.status eq "active")
    }.singleOrNull()

    fun getActiveById(id: Int) = User.find {
        (Users.id eq id) and (Users.status eq UserStatus.ACTIVE.value)
    }.singleOrNull()
}