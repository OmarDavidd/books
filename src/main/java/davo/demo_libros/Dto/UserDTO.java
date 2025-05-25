package davo.demo_libros.Dto;

import davo.demo_libros.Models.Libro;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String fechaRegistro;
    private Double valoracion;
    private Boolean activo;
    //private List<Libro> idLibros;
}
