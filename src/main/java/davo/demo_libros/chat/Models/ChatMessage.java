package davo.demo_libros.chat.Models;

import jakarta.persistence.*; // O javax.persistence.* si usas Spring Boot 2.x
import lombok.Data; // Para @Data
import lombok.NoArgsConstructor; // Para constructor sin argumentos
import lombok.AllArgsConstructor; // Opcional, para constructor con todos los argumentos

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes") // Asegúrate de que este sea el nombre de tu tabla
@Data // Genera getters, setters, equals, hashCode, toString
@NoArgsConstructor // Genera el constructor sin argumentos (necesario para JPA)
@AllArgsConstructor // Opcional: Genera un constructor con todos los campos
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usamos nombres de campos en inglés para que coincidan con Flutter
    // y si la columna en DB es diferente, usamos @Column(name="...")
    @Column(name = "remitente_id")
    private Long senderId;

    @Column(name = "destinatario_id")
    private Long receiverId;

    @Column(name = "contenido")
    private String content;

    @Column(name = "fecha_envio")
    private LocalDateTime sentAt; // Usar LocalDateTime


    // Puedes añadir un constructor para inicializar valores por defecto si lo necesitas,
    // o manejarlo en el servicio. Con Lombok y @NoArgsConstructor, JPA lo creará.
    // Los valores por defecto (sentAt y isRead) los puedes manejar en el servicio al guardar.
}