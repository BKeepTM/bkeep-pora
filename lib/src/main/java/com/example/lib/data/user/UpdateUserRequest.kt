package com.example.lib.data.user

data class UpdateUserRequest(
    val username: String? = null,
    val mail: String? = null,
    val password: String? = null
)
