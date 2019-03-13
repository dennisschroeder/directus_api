package com.directus.repository

import domain.model.User
import repository.RepositoryInterface

object UserRepository: RepositoryInterface<User> {
    suspend fun getById(id: Int) = asyncQuery {
        User.findById(id)
    }

    suspend fun getAll() = asyncQuery {
        User.all()
    }

}