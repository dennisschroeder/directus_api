package com.directus.repository

import com.directus.domain.model.User
import com.directus.domain.model.Users

import repository.RepositoryInterface

object UserRepository : RepositoryInterface<User> {
    override fun getById(id: Int) = User.findById(id)
    override fun getAll() = User.all()
    fun findByEmail(email: String) = User.find { Users.email eq email }.singleOrNull()
}