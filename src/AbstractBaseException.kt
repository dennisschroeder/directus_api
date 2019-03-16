package com.directus

import com.directus.domain.model.ErrorCode

abstract class AbstractBaseException(message: String) : RuntimeException(message) {
    abstract val errorCode: ErrorCode
}