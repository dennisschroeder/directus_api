package com.directus.repository

import domain.model.User
import domain.model.Users
import repository.RepositoryInterface

object UserRepository : RepositoryInterface<User> {
    override fun getById(id: Int) = User.findById(id)
    override fun getAll() = User.all()
    fun findByEmail(email: String) = User.find { Users.email eq email }.singleOrNull()
}