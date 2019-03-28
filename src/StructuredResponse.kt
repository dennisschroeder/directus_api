package com.directus

import com.directus.domain.model.ErrorResponse
import domain.model.SuccessResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import javax.lang.model.type.NullType


suspend fun ApplicationCall.errorResponse(exception: AbstractBaseException) {
    respond(
        exception.errorCode.httpCode,
        ErrorResponse(
            code = exception.errorCode.internalCode,
            message = exception.message ?: exception.errorCode.httpCode.description
        )
    )
}

suspend fun <Data>ApplicationCall.successResponse(httpCode: HttpStatusCode, data: Data) {
    respond(
        httpCode,
        SuccessResponse<Data, NullType>(data)
    )
}