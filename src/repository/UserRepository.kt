package com.directus.repository

import domain.model.User
import domain.model.Users
import repository.RepositoryInterface

object UserRepository : RepositoryInterface<User> {
    suspend fun getById(id: Int) = asyncQuery {
        User.findById(id)
    }

    suspend fun getAll() = asyncQuery {
        User.all()
    }

    suspend fun findByEmail(email: String) = asyncQuery {
        User.find { Users.email eq email }.singleOrNull()
    }

    suspend fun findByFieldName(fieldName: String) = asyncQuery {
        User.all().toList()
    }
}