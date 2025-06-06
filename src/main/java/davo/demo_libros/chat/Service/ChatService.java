package davo.demo_libros.chat.Service;

import davo.demo_libros.chat.Models.ChatMessage;
import davo.demo_libros.chat.Repository.ChatRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository; // Renombré para mayor claridad

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        // Asegurarse de que la fecha de envío y el estado de lectura se establezcan al guardar
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        return chatRepository.save(message);
    }

    public List<ChatMessage> getConversation(Long usuario1Id, Long usuario2Id) {
        return chatRepository.findConversacion(usuario1Id, usuario2Id);
    }
}