package endpoints.auth.exception

import com.directus.AbstractBaseException
import com.directus.domain.model.ErrorCode

class InvalidTokenException(message: String = "") : AbstractBaseException(message) {
   override val errorCode = ErrorCode.INVALID_TOKEN
}
