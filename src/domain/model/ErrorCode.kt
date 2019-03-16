package com.directus.domain.model

import io.ktor.http.HttpStatusCode

enum class ErrorCode(val internalCode: Int, val httpCode: HttpStatusCode) {

    // General error codes
    INTERNAL_ERROR(0, HttpStatusCode.InternalServerError),
    NOT_FOUND(1, HttpStatusCode.NotFound),
    BAD_REQUEST(2, HttpStatusCode.NotFound),
    UNAUTHORIZED(3, HttpStatusCode.Unauthorized),
    INVALID_REQUEST(4, HttpStatusCode.BadRequest),
    ENDPOINT_NOT_FOUND(5, HttpStatusCode.NotFound),
    METHOD_NOT_ALLOWED(6, HttpStatusCode.MethodNotAllowed),
    TOO_MANY_REQUESTS(7, HttpStatusCode.TooManyRequests),
    API_PROJECT_CONFIGURATION_NOT_FOUND(8, HttpStatusCode.NotFound),
    FAILED_GENERATING_SQL_QUERY(9, HttpStatusCode.InternalServerError),
    FORBIDDEN(10, HttpStatusCode.Forbidden),
    FAILED_TO_CONNECT_TO_DATABASE(11, HttpStatusCode.Forbidden),
    UNPROCESSABLE_ENTITY(12, HttpStatusCode.UnprocessableEntity),
    INVALID_OR_EMPTY_PAYLOAD(13, HttpStatusCode.BadRequest),
    DEFAULT_PROJECT_NOT_CONFIGURED_PROPERLY(14, HttpStatusCode.ServiceUnavailable),
    BATCH_UPLOAD_NOT_ALLOWED(15, HttpStatusCode.BadRequest),
    INVALID_FILESYSTEM_PATH(16, HttpStatusCode.InternalServerError),
    INVALID_CONFIGURATION_PATH(17, HttpStatusCode.UnprocessableEntity),
    PROJECT_NAME_ALREADY_EXISTS(18, HttpStatusCode.Conflict),
    UNAUTHORIZED_LOCATION_ACCESS(19, HttpStatusCode.Unauthorized),
    INSTALLATION_INVALID_DATABASE_INFORMATION(20, HttpStatusCode.BadRequest),


    // Authentication error codes
    INVALID_CREDENTIALS(100, HttpStatusCode.NotFound),
    INVALID_TOKEN(101, HttpStatusCode.Unauthorized),
    EXPIRED_TOKEN(102, HttpStatusCode.Unauthorized),
    INACTIVE_USER(103, HttpStatusCode.Unauthorized),
    INVALID_RESET_PASSWORD_TOKEN(104, HttpStatusCode.Unauthorized),
    EXPIRED_RESET_PASSWORD_TOKEN(105, HttpStatusCode.Unauthorized),
    USER_NOT_FOUND(106, HttpStatusCode.NotFound),
    USER_WITH_PROVIDED_EMAIL_NOT_FOUND(107, HttpStatusCode.NotFound),
    USER_NOT_AUTHENTICATED(108, HttpStatusCode.Unauthorized),

}