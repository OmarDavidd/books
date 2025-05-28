package davo.demo_libros.Repository;

import davo.demo_libros.Models.EstadoPrestamo;
import davo.demo_libros.Models.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoPrestamo, Long>{
}


