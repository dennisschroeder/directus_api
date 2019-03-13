package com.directus.domain.service

import com.directus.repository.UserRepository
import domain.model.User

object UserService {

    object Async {
        suspend fun getUser(id : Int) = UserRepository.getById(id)
        suspend fun getUsers() = UserRepository.getAll()
        suspend fun createBuilder() = UserRepository.asyncQuery { User }
    }

    fun createBuilder() = User
}