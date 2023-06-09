package no.hiof.webframework.springBoot;
import no.hiof.webframework.springBoot.model.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
class ChatEventListener {


    @Autowired
    private SimpMessageSendingOperations template;

    @EventListener
    public void chatSocketListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Object username;
        if (accessor.getSessionAttributes() != null) {

            username = accessor.getSessionAttributes().get("username");

            ChatService chatMessage = new ChatService();
            chatMessage.setType(ChatService.MessageType.DISCONNECT);
            chatMessage.setSender((String) username);

            template.convertAndSend("/subject/public", chatMessage);
        }
    }
}
