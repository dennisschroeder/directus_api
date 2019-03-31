package com.directus.endpoints.auth.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class ExpiredTokenException(message: String) : AbstractBaseException(message) {
   override val errorCode = ErrorCode.EXPIRED_TOKEN
}
