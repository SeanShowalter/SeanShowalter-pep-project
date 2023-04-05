package Controller;

import Model.*;
import Service.SocialMediaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    SocialMediaService socialMediaService;
    public SocialMediaController() {
        socialMediaService = new SocialMediaService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Create endpoint for registering new accounts
        app.post("/register", this::postRegistration);

        // Create endpoint for login
        app.post("/login", this::postLogin);

        // Create endpoint for posting messages
        app.post("/messages", this::postMessage);

        // Create endpoint for retrieving all messages
        app.get("/messages", this::getAllMessagesHandler);

        // Create endpoint for retrieving a particular message by its id
        app.get("/messages/{message_id}", this::getMessageByIDHandler);

        // Create endpoint for deleting a particular message by its id
        app.delete("/messages/{message_id}", this::deleteMessageByIDHandler);

        // Create endpoint for updating a particular message by its id
        app.patch("/messages/{message_id}", this::patchMessageByIDHandler);

        // Create endpoint for retrieving all messages from a particular user
        app.get("accounts/{account_id}/messages", this::getAllUserMessagesHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void postRegistration(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account;
        try {
            account = mapper.readValue(context.body(), Account.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Account addedUser = socialMediaService.addUser(account);
        if (addedUser == null) {
            context.status(400);
        } else {
            context.json(mapper.writeValueAsString(addedUser));
        }
    }

    private void postLogin(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account;
        try {
            account = mapper.readValue(context.body(), Account.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Account user = socialMediaService.verifyLogin(account);
        if (user == null) {
            context.status(401);
        } else {
            context.json(mapper.writeValueAsString(user));
        }
    }

    private void postMessage(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message msg;
        try {
            msg = mapper.readValue(context.body(), Message.class);
            //msg.setTime_posted_epoch(System.currentTimeMillis());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Message messageReturn = socialMediaService.addMessage(msg);
        if (messageReturn == null) {
            context.status(400);
        } else {
            context.json(mapper.writeValueAsString(messageReturn));
        }
    }

    private void getAllMessagesHandler(Context context){
        context.json(socialMediaService.getAllMessages());
    }

    private void getMessageByIDHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message msg = socialMediaService.getMessage(message_id);
        if (msg == null) {
            context.status(200);
        } else {
            context.json(mapper.writeValueAsString(msg));
        }
    }

    private void deleteMessageByIDHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message msg = socialMediaService.deleteMessage(message_id);
        if (msg == null) {
            context.status(200);
        } else {
            context.json(mapper.writeValueAsString(msg));
        }
    }

    private void patchMessageByIDHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message updatedMessage = socialMediaService.updateMessage(message_id, message);
        if(updatedMessage == null){
            context.status(400);
        }else{
            context.json(mapper.writeValueAsString(updatedMessage));
        }

    }

    private void getAllUserMessagesHandler(Context context) {
        context.json(socialMediaService.getAllUserMessages(Integer.parseInt(context.pathParam("account_id"))));
    }
}