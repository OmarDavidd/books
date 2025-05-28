package davo.demo_libros.services;

import davo.demo_libros.Dto.GeneroDTO;
import davo.demo_libros.Dto.LibroDTO;
import davo.demo_libros.Dto.UserDTO;
import davo.demo_libros.Models.Genero;
import davo.demo_libros.Models.ImagenLibro;
import davo.demo_libros.Models.Libro;
import davo.demo_libros.Models.Usuario;
import davo.demo_libros.Repository.GeneroRepository;
import davo.demo_libros.Repository.LibroRepository;

import java.util.*;
import java.util.stream.Collectors;

import davo.demo_libros.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public LibroDTO convertToDTO(Libro book) {
        if (book == null) {
            return null;
        }

        LibroDTO dto = new LibroDTO();
        dto.setId(book.getId());
        dto.setTitulo(book.getTitulo());
        dto.setAutor(book.getAutor());
        dto.setAnioPublicacion(book.getAnioPublicacion());
        dto.setIsbn(book.getIsbn());
        dto.setEditorial(book.getEditorial());
        dto.setEstado(book.getEstadoFisico());
        /*
        if (book.getGeneros() != null) {
            dto.setGeneros(
                    book.getGeneros().stream()
                            .map(this::convertGeneroToDTO)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setGeneros(Collections.emptyList());
        }*/

        if (book.getGeneros() != null && !book.getGeneros().isEmpty()) {
            dto.setGenerosIds(
                    book.getGeneros().stream()
                            .map(Genero::getId)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setGenerosIds(Collections.emptyList());
        }

        if (book.getImagenes() != null && !book.getImagenes().isEmpty()) {
            dto.setUrlsImagenes(
                    book.getImagenes().stream()
                            .map(ImagenLibro::getUrlImagen)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setUrlsImagenes(Collections.emptyList());
        }

        Optional<UserDTO> userOpt = usuarioService.findById(book.getUsuario().getId());

        dto.setIdUsuario(
                userOpt.map(user -> user.getId().toString())  // Convertimos Long a String
                        .orElse("Usuario no disponible")
        );
        dto.setNombreUsuario(
                userOpt.map(UserDTO::getNombreCompleto)
                        .orElse("Usuario no disponible")
        );
        return dto;
    }

    private GeneroDTO convertGeneroToDTO(Genero genero) {
        GeneroDTO generoDTO = new GeneroDTO();
        generoDTO.setId(genero.getId());
        generoDTO.setNombre(genero.getNombre());
        generoDTO.setDescripcion(genero.getDescripcion());
        return generoDTO;
    }

    @Transactional(readOnly = true)
    public List<LibroDTO> obtenerTodosLosLibrosDTO() {
        return libroRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LibroDTO obtenerLibroPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }
        return libroRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + id));
    }

    @Transactional
    public LibroDTO addLibro(Libro libro, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        libro.setUsuario(usuario);

        if (libro.getDisponibleIntercambio() == null) {
            libro.setDisponibleIntercambio(true);
        }
        System.out.println("libro"+libro);
        System.out.println("libro"+libro.toString());


        if (libro.getGenerosIdsEntrada() != null && !libro.getGenerosIdsEntrada().isEmpty()) {
            Set<Genero> generosEntidades = libro.getGenerosIdsEntrada().stream()
                    .map(id -> generoRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Género no encontrado con ID: " + id)))
                    .collect(Collectors.toSet());
            libro.setGeneros(generosEntidades);
        } else {
            libro.setGeneros(Collections.emptySet());
        }

        List<ImagenLibro> imagenesLibro = new ArrayList<>();
        if (libro.getUrlsImagenesEntrada() != null && !libro.getUrlsImagenesEntrada().isEmpty()) {
            for (String url : libro.getUrlsImagenesEntrada()) {
                ImagenLibro imagen = new ImagenLibro();
                imagen.setUrlImagen(url);
                imagen.setLibro(libro); // ¡CRUCIAL! Establecer la referencia al libro padre
                imagenesLibro.add(imagen);
            }
        }
        libro.setImagenes(imagenesLibro);

        Libro savedLibro = libroRepository.save(libro);

        return convertToDTO(savedLibro);
    }

    @Transactional(readOnly = true)
    public List<LibroDTO> obtenerLibrosPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID de usuario no puede ser nulo");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        List<Libro> librosUsuario = libroRepository.findByUsuarioId(usuarioId);

        return librosUsuario.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LibroDTO updateLibro(Long id, Libro libroActualizado) {
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser nulo");
        }

        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + id));

        libroExistente.setTitulo(libroActualizado.getTitulo());
        libroExistente.setAutor(libroActualizado.getAutor());
        libroExistente.setAnioPublicacion(libroActualizado.getAnioPublicacion());
        libroExistente.setIsbn(libroActualizado.getIsbn());
        libroExistente.setEditorial(libroActualizado.getEditorial());
        libroExistente.setEstadoFisico(libroActualizado.getEstadoFisico());
        libroExistente.setDisponibleIntercambio(libroActualizado.getDisponibleIntercambio());

        System.out.println("libroActualizado.getGenerosIdsEntrada()"+libroActualizado.getGenerosIdsEntrada());
        System.out.println("libroActualizado.getGenerosIdsEntrada()"+libroActualizado.getGeneros());

        if (libroActualizado.getGenerosIdsEntrada() != null && !libroActualizado.getGenerosIdsEntrada().isEmpty()) {
            Set<Genero> generosEntidades = libroActualizado.getGenerosIdsEntrada().stream()
                    .map(generoId -> generoRepository.findById(generoId)
                            .orElseThrow(() -> new RuntimeException("Género no encontrado con ID: " + generoId)))
                    .collect(Collectors.toSet());
            libroExistente.setGeneros(generosEntidades);
        } else {
            libroExistente.setGeneros(Collections.emptySet());
        }

        if (libroExistente.getImagenes() != null) {
            libroExistente.getImagenes().clear();
        } else {
            libroExistente.setImagenes(new ArrayList<>());
        }

        if (libroActualizado.getUrlsImagenesEntrada() != null && !libroActualizado.getUrlsImagenesEntrada().isEmpty()) {
            for (String url : libroActualizado.getUrlsImagenesEntrada()) {
                ImagenLibro imagen = new ImagenLibro();
                imagen.setUrlImagen(url);
                imagen.setLibro(libroExistente); // Establecer la referencia al libro padre
                libroExistente.getImagenes().add(imagen);
            }
        }

        Libro savedLibro = libroRepository.save(libroExistente);

        return convertToDTO(savedLibro);
    }

    @Transactional
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new RuntimeException("Libro no encontrado con ID: " + id);
        }
        libroRepository.deleteById(id);
    }
}
