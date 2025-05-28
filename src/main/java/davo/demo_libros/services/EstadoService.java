package davo.demo_libros.services;

import davo.demo_libros.Dto.EstadoDTO;
import davo.demo_libros.Dto.LibroDTO;
import davo.demo_libros.Models.EstadoPrestamo;
import davo.demo_libros.Repository.EstadoRepository;
import davo.demo_libros.Repository.PrestamoRepository;
import lombok.Locked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<EstadoDTO> getAllEstados(){
        return estadoRepository.findAll()
                .stream()
                .map(this::convertEstadoDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstadoPrestamo getEstadoById(Long id) {
        return estadoRepository.findById(id).get();
    }


    @Transactional(readOnly = true)
    public EstadoDTO convertEstadoDto(EstadoPrestamo estadoPrestamo){
        EstadoDTO dto = new EstadoDTO();
        dto.setId(estadoPrestamo.getId());
        dto.setNombre(estadoPrestamo.getNombre());
        dto.setDescripcion(estadoPrestamo.getDescripcion());

        return dto;
    }


}
