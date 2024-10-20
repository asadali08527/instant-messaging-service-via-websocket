package co.vivo.chatservice.controller;

import co.vivo.chatservice.converter.UserConverter;
import co.vivo.chatservice.dto.UserRequestDto;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.UserService;
import co.vivo.chatservice.wrapper.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser( UserRequestDto userRequestDto) {
        UserEntity userEntity = userService.registerUser(userRequestDto);
        // Map the UserEntity to the User wrapper class
        User user = UserConverter.convert(userEntity);
        // Return the response
        return Response.ok(Map.of("user", user)).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = authService.loginUser(userRequestDto.getEmail()!=null && !userRequestDto.getEmail().isEmpty()?userRequestDto.getEmail():userRequestDto.getMobile(), userRequestDto.getPassword());
        if (userEntity != null) {
            String token = authService.generateToken(userEntity);
            User user = UserConverter.convert(userEntity);
            return Response.ok(Map.of("user", user, "token", token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
