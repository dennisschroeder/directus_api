package com.directus.auth.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class NoProjectKeyException(message: String) : AbstractBaseException(message) {
    override val errorCode: ErrorCode = ErrorCode.BAD_REQUEST
}