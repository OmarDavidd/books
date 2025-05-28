package davo.demo_libros.controllers;

import davo.demo_libros.Dto.EstadoDTO;
import davo.demo_libros.Dto.PrestamoDTO;
import davo.demo_libros.Models.EstadoPrestamo;
import davo.demo_libros.services.EstadoService;
import davo.demo_libros.services.PrestamosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<EstadoDTO>> getAllEstados() {
        List<EstadoDTO> estados = estadoService.getAllEstados();
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }
}
