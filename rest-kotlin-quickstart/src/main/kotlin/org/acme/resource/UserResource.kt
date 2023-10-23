package org.acme.resource

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.acme.models.*
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation



@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
class UserService {
    @Inject
    lateinit var keycloakService: KeycloakService

    @GET
    @Path("/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getUserByEmail(@PathParam("email") searchEmailDto: SearchEmailDto): UserRepresentation? {
        return keycloakService.getUserByEmail(searchEmailDto)
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    fun getUserToken(loginDto: LoginDto): Response{
        val accessToken = keycloakService.getUserToken(loginDto.userName,loginDto.password)
        return Response.ok(accessToken).build()
    }

    @POST
    fun createUser(user: User): Response {
        val createdUser = keycloakService.createUser(user)
        return Response.ok(createdUser).build()
    }

    @POST
    @Path("/reset-password")
    @Produces(MediaType.APPLICATION_JSON)
    fun resetPassword(resetPasswordDto: ResetPasswordDto):Response{
        val passwordReset = keycloakService.resetPassword(resetPasswordDto)
        return Response.ok(passwordReset).build()
    }
}
