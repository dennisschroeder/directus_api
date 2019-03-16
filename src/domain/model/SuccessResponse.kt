package domain.model

data class SuccessResponse<Data,Meta>(val  data: Data? = null ,val meta: Meta? = null)