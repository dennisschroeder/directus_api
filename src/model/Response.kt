package com.directus.model

data class SuccessResponse<R,T>(val  data: R? = null ,val meta: T? = null)
data class ErrorResponse(val code: Int, val message: String)