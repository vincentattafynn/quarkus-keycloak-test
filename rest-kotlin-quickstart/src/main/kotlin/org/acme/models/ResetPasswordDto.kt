package org.acme.models

data class ResetPasswordDto (val email: String, val newPassword: String)