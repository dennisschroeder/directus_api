package com.directus.domain.service
import com.directus.repository.UserRepository
import domain.model.User

object UserService {

    fun getUser(id : Int) = UserRepository.getById(id)
    fun getUsers() = UserRepository.getAll()
    fun getUserByEmail(email: String) = UserRepository.findByEmail(email)

    fun createUser(fields: User.() -> Unit) =  User.new(init = fields)
}