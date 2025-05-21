package com.claude.springboot.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.dto.ActualizarResponsableDTO;
import com.claude.springboot.app.dto.AreaResponseDTO;
import com.claude.springboot.app.dto.TemasPqrsDTO;
import com.claude.springboot.app.dto.TemasPqrsResponseDTO;
import com.claude.springboot.app.dto.UsuarioResponsableDTO;
import com.claude.springboot.app.dto.VincularResponsableDTO;
import com.claude.springboot.app.entities.TemasPqrs;
import com.claude.springboot.app.entities.TemasPqrsResponsable;
import com.claude.springboot.app.repositories.TemasPqrsRepository;
import com.claude.springboot.app.repositories.TemasPqrsResponsableRepository;
import com.claude.springboot.app.security.entities.Area;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.AreaRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemasPqrsServiceImpl implements TemasPqrsService {

    private final TemasPqrsRepository temasPqrsRepository;
    private final TemasPqrsResponsableRepository responsableRepository;
    private final AreaRepository areaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public TemasPqrsResponseDTO crear(TemasPqrsDTO dto) {
        // Validar área
        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));

        // Validar nombre único por área
        if (temasPqrsRepository.existsByNombreAndAreaIdArea(dto.getNombre(), dto.getIdArea())) {
            throw new RuntimeException("Ya existe un tema con ese nombre en el área especificada");
        }

        TemasPqrs tema = new TemasPqrs();
        tema.setArea(area);
        tema.setNombre(dto.getNombre());
        tema.setDescripcion(dto.getDescripcion());
        tema.setEstado(dto.getEstado() != null ? dto.getEstado() : true);

        tema = temasPqrsRepository.save(tema);

        return convertToResponseDTO(tema);
    }

    @Override
    @Transactional
    public TemasPqrsResponseDTO actualizar(Long id, TemasPqrsDTO dto) {
        TemasPqrs tema = temasPqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        Area area = areaRepository.findById(dto.getIdArea())
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));

        // Validar nombre único si se está cambiando
        if (!tema.getNombre().equals(dto.getNombre()) &&
                temasPqrsRepository.existsByNombreAndAreaIdArea(dto.getNombre(), dto.getIdArea())) {
            throw new RuntimeException("Ya existe un tema con ese nombre en el área especificada");
        }

        tema.setArea(area);
        tema.setNombre(dto.getNombre());
        tema.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            tema.setEstado(dto.getEstado());
        }

        tema = temasPqrsRepository.save(tema);

        return convertToResponseDTO(tema);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        TemasPqrs tema = temasPqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        temasPqrsRepository.delete(tema);
    }

    @Override
    @Transactional(readOnly = true)
    public TemasPqrsResponseDTO obtenerPorId(Long id) {
        TemasPqrs tema = temasPqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        return convertToResponseDTO(tema);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemasPqrsResponseDTO> listarTodos() {
        return temasPqrsRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TemasPqrsResponseDTO vincularResponsable(Long idTema, VincularResponsableDTO dto) {
        TemasPqrs tema = temasPqrsRepository.findById(idTema)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el nuevo usuario no sea ya responsable activo
        if (responsableRepository.existsByTemaAndUsuarioAndEstadoTrue(tema, usuario)) {
            throw new RuntimeException("El nuevo usuario ya es responsable activo de este tema");
        }

        TemasPqrsResponsable responsable = new TemasPqrsResponsable();
        responsable.setTema(tema);
        responsable.setUsuario(usuario);
        responsableRepository.save(responsable);

        return convertToResponseDTO(tema);
    }

    @Override
    @Transactional
    public TemasPqrsResponseDTO desvincularResponsable(Long idTema, Long idUsuario) {
        TemasPqrs tema = temasPqrsRepository.findById(idTema)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TemasPqrsResponsable responsable = responsableRepository.findByTemaAndUsuario(tema, usuario)
                .orElseThrow(() -> new RuntimeException("El usuario no es responsable de este tema"));

        responsable.setEstado(false);
        responsableRepository.save(responsable);

        return convertToResponseDTO(tema);
    }

    private TemasPqrsResponseDTO convertToResponseDTO(TemasPqrs tema) {
        TemasPqrsResponseDTO dto = new TemasPqrsResponseDTO();
        dto.setIdTema(tema.getIdTema());
        dto.setNombre(tema.getNombre());
        dto.setDescripcion(tema.getDescripcion());
        dto.setEstado(tema.isEstado());
        dto.setFechaCreacion(tema.getFechaCreacion());

        AreaResponseDTO areaDTO = new AreaResponseDTO();
        areaDTO.setIdArea(tema.getArea().getIdArea());
        areaDTO.setNombre(tema.getArea().getNombre());
        dto.setArea(areaDTO);

        // Obtener responsables activos
        List<UsuarioResponsableDTO> responsables = responsableRepository.findByTemaAndEstadoTrue(tema)
                .stream()
                .map(resp -> {
                    Usuario u = resp.getUsuario();
                    return new UsuarioResponsableDTO(
                            u.getIdUsuario(),
                            u.getUsername(),
                            u.getPersona() != null ? u.getPersona().getNombreCompleto() : null);
                })
                .collect(Collectors.toList());

        dto.setResponsables(responsables);

        return dto;
    }

    @Override
    @Transactional
    public TemasPqrsResponseDTO actualizarResponsable(Long idTema, ActualizarResponsableDTO dto) {
        TemasPqrs tema = temasPqrsRepository.findById(idTema)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        // Validar usuario anterior
        Usuario usuarioAnterior = usuarioRepository.findById(dto.getIdUsuarioAnterior())
                .orElseThrow(() -> new RuntimeException("Usuario anterior no encontrado"));

        // Validar usuario nuevo
        Usuario usuarioNuevo = usuarioRepository.findById(dto.getIdUsuarioNuevo())
                .orElseThrow(() -> new RuntimeException("Usuario nuevo no encontrado"));

        // Buscar la asignación actual activa
        TemasPqrsResponsable responsableActual = responsableRepository
                .findByTemaAndUsuarioAndEstadoTrue(tema, usuarioAnterior)
                .orElseThrow(() -> new RuntimeException("El usuario anterior no es responsable activo de este tema"));

        // Verificar que el nuevo usuario no sea ya responsable activo
        if (responsableRepository.existsByTemaAndUsuarioAndEstadoTrue(tema, usuarioNuevo)) {
            throw new RuntimeException("El nuevo usuario ya es responsable activo de este tema");
        }

        // Desactivar la asignación actual
        responsableActual.setEstado(false);
        responsableRepository.save(responsableActual);

        // Crear nueva asignación
        TemasPqrsResponsable nuevoResponsable = new TemasPqrsResponsable();
        nuevoResponsable.setTema(tema);
        nuevoResponsable.setUsuario(usuarioNuevo);
        responsableRepository.save(nuevoResponsable);

        return convertToResponseDTO(tema);
    }

}
