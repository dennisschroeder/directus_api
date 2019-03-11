package domain.model

data class SuccessResponse<Data,Meta>(val  data: Data? = null ,val meta: Meta? = null)
data class ErrorResponse(val code: Int, val message: String)