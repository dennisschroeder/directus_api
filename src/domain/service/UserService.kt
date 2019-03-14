package com.directus.domain.service
import com.directus.repository.UserRepository
import com.directus.repository.UserRepository.asyncQuery
import domain.model.User

object UserService {

    suspend fun getUser(id : Int) = UserRepository.getById(id)
    suspend fun getUsers() = UserRepository.getAll()
    suspend fun getUserByEmail(email: String) = UserRepository.findByEmail(email)

    suspend fun createUser(fields: User.() -> Unit) = asyncQuery {
        User.new(init = fields)
    }
}