package com.directus.endpoint.auth.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class UserNotFoundException(message: String) : AbstractBaseException(message) {
    override val errorCode: ErrorCode = ErrorCode.USER_NOT_FOUND
}