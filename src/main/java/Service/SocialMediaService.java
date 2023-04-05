package Service;

import DAO.SocialMediaDAO;
import Model.*;
import java.util.*;
public class SocialMediaService {
    SocialMediaDAO socialMediaDAO;

    // No-args constructor for SocialMediaDAO
    public SocialMediaService() {
        socialMediaDAO = new SocialMediaDAO();
    }

    public Account addUser(Account account) {
        // Is the username field empty?
        if (account.getUsername().isBlank()) {
            return null;
        }
        // Is the password less than 4 chars?
        if (account.getPassword().length() < 4) {
            return null;
        }
        return socialMediaDAO.insertUser(account);
    }

    public Account verifyLogin(Account account) {
        return socialMediaDAO.verifyUsernameAndPassword(account);
    }

    public Message addMessage(Message msg) {
        // Is the message empty?
        if (msg.getMessage_text().isBlank()) {
            return null;
        }
        // Is the message greater than 254 chars?
        if (msg.getMessage_text().length() > 254) {
            return null;
        }
        // Is the user in the database?
        if (!socialMediaDAO.verifyAccountWithID(msg.getPosted_by())) {
            return null;
        }
        // Returns null if empty
        return socialMediaDAO.insertMessage(msg);
    }

    public List<Message> getAllMessages() {
        return socialMediaDAO.getAllMessages();
    }

    public Message getMessage(int id) {
        return socialMediaDAO.getMessage(id);
    }

    public Message deleteMessage(int id) {
        return socialMediaDAO.deleteMessage(id);
    }

    public Message updateMessage(int message_id, Message message) {
        // Does the message already exist?
        if (socialMediaDAO.getMessage(message_id) == null) {
            return null;
        }

        // Is the message empty?
        if (message.getMessage_text().isBlank()) {
            return null;
        }

        // Is the message greater than 254 chars?
        if (message.getMessage_text().length() > 254) {
            return null;
        }
        socialMediaDAO.updateMessage(message_id,message);
        return socialMediaDAO.getMessage(message_id);
    }

    public List<Message> getAllUserMessages(int account_id) {
        if (socialMediaDAO.verifyAccountWithID(account_id)) {
            return socialMediaDAO.getAllUserMessages(account_id);
        }
        // If there are no messages, then we simply return an empty list
        return new ArrayList<>();
    }
}
