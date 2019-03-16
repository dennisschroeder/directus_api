package com.directus.endpoint.auth.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class InvalidCredentialsException(message: String) : AbstractBaseException(message) {
   override val errorCode = ErrorCode.INVALID_CREDENTIALS
}
