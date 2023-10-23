package org.acme.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)


