package co.vivo.chatservice.controller;


import co.vivo.chatservice.dto.GroupDto;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.ChatGroupRepository;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatGroupService;
import co.vivo.chatservice.wrapper.Group;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/groups")
public class ChatGroupController {

    @Inject
    private ChatGroupRepository chatGroupRepository;

    @Inject
    private AuthService authService; // To verify user tokens

    @Inject
    private ChatGroupService chatGroupService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(GroupDto chatGroup, @HeaderParam("Authorization") String token) {
        UserEntity adminUser = authService.verifyToken(token);
        if (adminUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        chatGroupService.createChatGroup(chatGroup,adminUser);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups( @HeaderParam("Authorization") String token) {
        UserEntity groupUser = authService.verifyToken(token);
        List<Group> groups = chatGroupService.getAllGroups(groupUser);
        return Response.ok(groups).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId, @HeaderParam("Authorization") String token) {
        UserEntity groupUser = authService.verifyToken(token);
        if (groupUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        Group group =chatGroupService.getGroupByGroupId(groupId, groupUser);
        if(group!=null)
            return Response.ok(group).build();
        else
            return Response.ok("Either Request Group doesn't exist or You do not have permission").build();
    }
}
