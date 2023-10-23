package org.acme.models

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation


@ApplicationScoped
class KeycloakService {
    @Inject
    private lateinit var keycloak: Keycloak

    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    lateinit var serverUrl: String

    @ConfigProperty(name = "quarkus.keycloak.admin-client.client-id")
    lateinit var clientId: String

    @ConfigProperty(name = "quarkus.keycloak.admin-client.client-secret")
    lateinit var clientSecret: String

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    lateinit var realm: String

    val grantType: String = "password"

//    private val keycloak: Keycloak = Keycloak.getInstance(
//        "http://localhost:8081/auth",
//        realm,
//        "admin",
//        "admin",
//        "quarkus-be",
//        "JPkVBp6nSi6VMVuxpaTURWQeZLf2xDDb"
//    )


    fun createUser(user: User): UserRepresentation? {
        val userRepresentation = UserRepresentation()
        userRepresentation.isEnabled = true
        userRepresentation.username = user.userName
        userRepresentation.email = user.email
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName


        val response = keycloak.realm(realm).users().create(userRepresentation)

        val createdUser = keycloak.realm(realm).users().searchByEmail(user.email, true).first()

        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = user.password

        val userId = response.location.path.substringAfterLast("/")
        keycloak.realm(realm).users().get(userId).resetPassword(credentialRepresentation)

        return createdUser
    }


    fun getUserByEmail(searchEmailDto: SearchEmailDto): UserRepresentation? {
        return keycloak.realm(realm).users().searchByEmail(searchEmailDto.email, true).first()
    }


    fun getUserToken(userName: String, password: String): AccessTokenResponse {
        val keycloak =
            KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .username(userName)
                .password(password)
                .build()

        return keycloak.tokenManager().grantToken()

    }

    fun resetPassword(resetPasswordDto: ResetPasswordDto): UserRepresentation? {
        val existingUser = keycloak.realm(realm).users().searchByEmail(resetPasswordDto.email, true).firstOrNull()

        if (existingUser != null) {
            val credentialRepresentation = CredentialRepresentation()
            credentialRepresentation.type = CredentialRepresentation.PASSWORD
            credentialRepresentation.value = resetPasswordDto.newPassword

            keycloak.realm(realm).users().get(existingUser.id).resetPassword(credentialRepresentation)
        }
        return existingUser
    }
}



