package davo.demo_libros.services;

import davo.demo_libros.Dto.EstadoDTO;
import davo.demo_libros.Dto.PrestamoDTO;
import davo.demo_libros.Models.EstadoPrestamo;
import davo.demo_libros.Models.Prestamo;
import davo.demo_libros.Repository.EstadoRepository;
import davo.demo_libros.Repository.PrestamoRepository;
import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrestamosService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private EstadoService estadoService;

    public PrestamoDTO convertToDTO(Prestamo prestamo){
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setNombreSolicitante(prestamo.getSolicitante().getNombre());
        dto.setNombrePropietario(prestamo.getPropietario().getNombre());
        dto.setNombreLibro(prestamo.getLibro().getTitulo());
        dto.setIdLibro(Math.toIntExact(prestamo.getLibro().getId()));
        dto.setFechaInicio(
                Optional.ofNullable(prestamo.getFechaInicio())
                        .map(Object::toString)
                        .orElse("")
        );

        dto.setFechaDevolucionEsperada(
                Optional.ofNullable(prestamo.getFechaDevolucionEsperada())
                        .map(Object::toString)
                        .orElse("")
        );
        dto.setDuracion(prestamo.getDuracion());
        dto.setLugar(prestamo.getLugar());
        dto.setEstado(prestamo.getEstado().getNombre());
        dto.setMensaje(prestamo.getMensaje());

        return dto;
    }

    @Transactional
    public PrestamoDTO addPrestamo(Prestamo prestamo) {
        Prestamo savedPrestamo = prestamoRepository.save(prestamo);
        return convertToDTO(savedPrestamo);
    }

    @Transactional
    public PrestamoDTO updateEstado(Prestamo prestamo) {
        Prestamo updatedPrestamo = prestamoRepository.save(prestamo);
        return convertToDTO(updatedPrestamo);
    }

    //get all prestamos
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getAllPrestamos() {
        return prestamoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrestamoDTO> getPrestamosByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo.");
        }

        List<Prestamo> allPrestamos = prestamoRepository.findAll();

        List<Prestamo> filteredPrestamos = allPrestamos.stream()
                .filter(prestamo -> {
                    Long solicitanteId = (prestamo.getSolicitante() != null) ? prestamo.getSolicitante().getId() : null;
                    Long propietarioId = (prestamo.getPropietario() != null) ? prestamo.getPropietario().getId() : null;

                    return userId.equals(solicitanteId) || userId.equals(propietarioId);
                })
                .collect(Collectors.toList());

        // 3. Convert the filtered list to DTOs
        return filteredPrestamos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrestamoDTO cambiarEstado(Long idPrestamo, Long nuevoEstadoId) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con ID: " + idPrestamo));

        EstadoPrestamo newEstado = estadoService.getEstadoById(nuevoEstadoId);
        prestamo.setEstado(newEstado);

        Prestamo updatedPrestamo = prestamoRepository.save(prestamo);
        return convertToDTO(updatedPrestamo);
    }

    @Transactional
    public PrestamoDTO cambiarFecha(Long idPrestamo, LocalDateTime date) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con ID: " + idPrestamo));

        prestamo.setFechaInicio(date);

        Prestamo updatedPrestamo = prestamoRepository.save(prestamo);
        return convertToDTO(updatedPrestamo);
    }


    @Transactional
    public void eliminarPrestamo(Long id) {
        System.out.println(id);
        prestamoRepository.deleteById(id);
    }
}
