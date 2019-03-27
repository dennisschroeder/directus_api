package com.directus.config.exception
import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class ApiConfigurationNotFoundException(message: String) : AbstractBaseException(message) {
    override val errorCode: ErrorCode = ErrorCode.API_PROJECT_CONFIGURATION_NOT_FOUND
}