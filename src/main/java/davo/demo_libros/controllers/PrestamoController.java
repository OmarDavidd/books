package davo.demo_libros.controllers;

import davo.demo_libros.Dto.PrestamoDTO;
import davo.demo_libros.Models.Prestamo;
import davo.demo_libros.services.LibroService;
import davo.demo_libros.services.PrestamosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamosService prestamosService;

    @PostMapping
    public ResponseEntity<PrestamoDTO> createPrestamo(@RequestBody Prestamo prestamo) {
        PrestamoDTO savedPrestamo = prestamosService.addPrestamo(prestamo);
        return new ResponseEntity<>(savedPrestamo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PrestamoDTO>> getAllPrestamos() {
        List<PrestamoDTO> prestamos = prestamosService.getAllPrestamos();
        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<List<PrestamoDTO>> getPrestamosByUserId(@PathVariable Long id) {
        List<PrestamoDTO> prestamos = prestamosService.getPrestamosByUserId(id);
        if (prestamos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<PrestamoDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Long nuevoEstadoId
    ) {
        PrestamoDTO updatedPrestamo = prestamosService.cambiarEstado(id, nuevoEstadoId);
        return ResponseEntity.ok(updatedPrestamo);
    }

    @PutMapping("/fecha/{id}")
    public ResponseEntity<PrestamoDTO> cambiarFecha(
            @PathVariable Long id,
            @RequestBody LocalDateTime date
    ) {
        PrestamoDTO updatedPrestamo = prestamosService.cambiarFecha(id, date);
        return ResponseEntity.ok(updatedPrestamo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable Long id) {
        try {
            prestamosService.eliminarPrestamo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 Not Found
        }
    }

}
