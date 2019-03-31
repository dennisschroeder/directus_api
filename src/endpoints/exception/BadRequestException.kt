package com.directus.endpoints.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class BadRequestException(message: String) : AbstractBaseException(message) {
   override val errorCode = ErrorCode.BAD_REQUEST
}
