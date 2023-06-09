package no.hiof.webframework.application.chat;
import no.hiof.webframework.application.enums.ChatMethod;
import no.hiof.webframework.interfaces.ChatStrategy;

import java.util.List;

/**
 * A chat room that allows users to send and receive messages using a specified
 * chat strategy.
 */
public class ChatRoom {
    private ChatStrategy chatStrategy;
    /**

     Creates a ChatRoom object based on the given chat method.
     @param chatMethod the type of chat method, either PRIVATE or GROUP
     @throws IllegalArgumentException if the chatMethod is null
     */
    public ChatRoom(ChatMethod chatMethod) throws IllegalArgumentException {
        if (chatMethod == null)
            throw new IllegalArgumentException("Chat method cannot be null.");

        else if (chatMethod == ChatMethod.PRIVATE)
            this.chatStrategy = new PrivateChat();

        else if (chatMethod == ChatMethod.GROUP)
            this.chatStrategy = new GroupChat();
    }

    /**
     * Sends a message from the specified sender to the chat room using the
     * chat strategy.
     *
     * @param sender the user sending the message
     * @param message the message to send
     */
    public void sendMessage(ChatUser sender, String message) {
        chatStrategy.sendMessage(sender, message);
    }
    /**
     * Receives a message from the specified sender in the chat room using the
     * chat strategy.
     *
     * @param sender the user sending the message
     * @param message the message received
     */
    public void receiveMessage(ChatUser sender, String message) {
        chatStrategy.receiveMessage(sender, message);
    }
    /**
     * Returns the chat history for the chat room using the chat strategy.
     *
     * @return a list of strings representing the chat history
     */
    public List<String> getChatHistory() {
        return chatStrategy.getChatHistory();
    }

    /**
     * Adds a chat user to the chat strategy.
     *
     * @param user the chat user to add
     */


    public void addUser(ChatUser user) {
        chatStrategy.addUser(user);
    }
}
