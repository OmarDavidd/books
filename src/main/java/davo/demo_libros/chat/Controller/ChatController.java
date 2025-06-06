package davo.demo_libros.chat.Controller;

import davo.demo_libros.chat.Models.ChatMessage; // Tu modelo ChatMessage de Java
import davo.demo_libros.chat.Service.ChatService; // Tu servicio de chat
import org.springframework.messaging.handler.annotation.MessageMapping; // Para WebSockets
import org.springframework.messaging.handler.annotation.Payload; // Para WebSockets
import org.springframework.messaging.simp.SimpMessagingTemplate; // Para enviar mensajes STOMP a clientes
import org.springframework.web.bind.annotation.GetMapping; // Para REST
import org.springframework.web.bind.annotation.RequestMapping; // Para REST
import org.springframework.web.bind.annotation.RequestParam; // Para REST
import org.springframework.web.bind.annotation.RestController; // Usar si solo es REST, o @Controller si lo combinas.
import org.springframework.http.ResponseEntity; // Para devolver respuestas HTTP en REST

import java.time.ZoneOffset; // Para convertir LocalDateTime a timestamp de milisegundos
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Usamos @RestController para este ejemplo porque combina REST y WebSockets.
// @Controller también funcionaría, pero @RestController es más idiomático para APIs JSON.
@RestController
@RequestMapping("/api/chat") // Prefijo para todos los endpoints REST en esta clase
public class ChatController {

    private final ChatService chatService; // Inyecta tu servicio para interactuar con la DB
    private final SimpMessagingTemplate messagingTemplate; // Para enviar mensajes a través del broker STOMP

    // Constructor para inyección de dependencias
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // --- ENDPOINT REST: Obtener historial de conversación ---
    // Este método es llamado por ChatApiService en Flutter.
    @GetMapping("/conversacion")
    public ResponseEntity<List<Map<String, Object>>> getConversacion(
            @RequestParam("usuario1Id") Long usuario1Id, // Asegúrate de que Flutter envíe Long o String que se pueda parsear a Long
            @RequestParam("usuario2Id") Long usuario2Id) { // Lo mismo aquí

        System.out.println("Petición REST para historial de conversación: U1=" + usuario1Id + ", U2=" + usuario2Id);

        // 1. Obtener los mensajes del servicio (que los saca de la base de datos)
        List<ChatMessage> dbMessages = chatService.getConversation(usuario1Id, usuario2Id);

        // 2. Convertir los ChatMessage de Java a una estructura Map<String, Object> que Flutter espera
        // Esto es CRUCIAL para que ChatMessage.fromJson de Flutter pueda parsear correctamente.
        List<Map<String, Object>> messagesForFlutter = dbMessages.stream().map(msg -> {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("id", msg.getId() != null ? msg.getId().toString() : null); // ID como String
            jsonMap.put("senderId", msg.getSenderId() != null ? msg.getSenderId().toString() : null); // senderId como String
            jsonMap.put("receiverId", msg.getReceiverId() != null ? msg.getReceiverId().toString() : null); // receiverId como String
            jsonMap.put("content", msg.getContent());
            // Convertir LocalDateTime a milisegundos desde la época Unix (para Flutter)
            jsonMap.put("timestamp", msg.getSentAt() != null ? msg.getSentAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null);

            return jsonMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(messagesForFlutter); // Devolver 200 OK con la lista de mensajes
    }


    // --- ENDPOINT WEBSOCKET: Procesar y retransmitir mensajes en tiempo real ---
    // Este método es llamado cuando Flutter envía un mensaje a '/app/chat.send'.
    @MessageMapping("/chat.send")
    public void processMessage(@Payload ChatMessage message) {
        System.out.println("Mensaje WebSocket recibido de " + message.getSenderId() + ": " + message.getContent());

        ChatMessage savedMessage = chatService.saveMessage(message);

           Map<String, Object> messageForFlutter = new HashMap<>();
        messageForFlutter.put("id", savedMessage.getId() != null ? savedMessage.getId().toString() : null);
        messageForFlutter.put("senderId", savedMessage.getSenderId() != null ? savedMessage.getSenderId().toString() : null);
        messageForFlutter.put("receiverId", savedMessage.getReceiverId() != null ? savedMessage.getReceiverId().toString() : null);
        messageForFlutter.put("content", savedMessage.getContent());
        messageForFlutter.put("timestamp", savedMessage.getSentAt() != null ? savedMessage.getSentAt().toInstant(ZoneOffset.UTC).toEpochMilli() : null);

        messagingTemplate.convertAndSend("/topic/public", messageForFlutter);
        System.out.println("Mensaje retransmitido a /topic/public: " + messageForFlutter);

    }
}