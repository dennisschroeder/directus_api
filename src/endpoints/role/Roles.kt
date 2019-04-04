package com.directus.endpoints.role

import com.directus.UserIds
import com.directus.domain.model.RoleReceiver
import com.directus.domain.service.RoleService
import com.directus.endpoints.exception.BadRequestException
import com.directus.repository.database.asyncTransaction
import com.directus.repository.database.transaction
import com.directus.successResponse
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.launch

@KtorExperimentalLocationsAPI
fun Route.roles() {
    route("/roles") {
        post {

            val payload = runCatching { call.receive<Any>() }

            payload.onSuccess { roles ->
                call.asyncTransaction {
                    if (roles is ArrayList<*>) {
                        roles.forEach { role ->
                            val roleReceiver =
                                Gson().fromJson<RoleReceiver>(role.toString(), RoleReceiver::class.java)

                            RoleService.createRoleFromPayload(roleReceiver)
                        }

                        launch {
                            call.response.status(HttpStatusCode.OK)
                        }
                        return@asyncTransaction
                    }

                    if (roles is LinkedTreeMap<*, *>) {
                        val roleReceiver =
                            Gson().fromJson<RoleReceiver>(roles.toString(), RoleReceiver::class.java)
                        RoleService.createRoleFromPayload(roleReceiver)

                        launch {
                            call.response.status(HttpStatusCode.OK)
                        }
                        return@asyncTransaction
                    }
                }
            }

            payload.onFailure {
                throw BadRequestException("Missing or wrong payload!")
            }
        }

        get {
            call.transaction {
                val roles = RoleService.getRoles().toList()

                launch {
                    call.successResponse(HttpStatusCode.OK, roles)
                }
            }
        }

        get<UserIds> { user ->
            val listOfIds = user.ids.split(",")

            call.transaction {
                val roles = listOfIds.mapNotNull { id ->
                    RoleService.getRole(id.toInt())
                }

                launch {

                    if (1 == roles.size) {
                        call.successResponse(HttpStatusCode.OK, roles.first())
                        return@launch
                    }

                    call.successResponse(HttpStatusCode.OK, roles)
                }
            }
        }
    }
}