package davo.demo_libros.chat.Repository;

import davo.demo_libros.chat.Models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :usuario1 AND m.receiverId = :usuario2) OR " + // Usar senderId y receiverId
            "(m.senderId = :usuario2 AND m.receiverId = :usuario1) " +
            "ORDER BY m.sentAt ASC") // Usar sentAt para el orden
    List<ChatMessage> findConversacion(@Param("usuario1") Long usuario1Id,
                                       @Param("usuario2") Long usuario2Id);
}