package com.claude.springboot.app.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.dto.UsuarioResponseDTO;
import com.claude.springboot.app.repositories.PqrsRepository;
import com.claude.springboot.app.security.dto.ActualizarUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.PersonaDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.UsuarioDTO;
import com.claude.springboot.app.security.dto.UsuarioInfoCompletaDTO;
import com.claude.springboot.app.security.entities.Area;
import com.claude.springboot.app.security.entities.Direccion;
import com.claude.springboot.app.security.entities.Empresa;
import com.claude.springboot.app.security.entities.Persona;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Territorial;
import com.claude.springboot.app.security.entities.TokenActivacion;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.exception.UserRegistrationException;
import com.claude.springboot.app.security.exception.UsernameExistsException;
import com.claude.springboot.app.security.exception.UsuarioNotFoundException;
import com.claude.springboot.app.security.repositories.AreaRepository;
import com.claude.springboot.app.security.repositories.EmpresaRepository;
import com.claude.springboot.app.security.repositories.PersonaRepository;
import com.claude.springboot.app.security.repositories.RolRepository;
import com.claude.springboot.app.security.repositories.TokenActivacionRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AreaRepository areaRepository;
    private final EmpresaRepository empresaRepository;
    private final TokenActivacionRepository tokenActivacionRepository;
    private final EmailService emailService;
    private final PqrsRepository pqrsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarTodos() {
        try {
            log.debug("Iniciando listado de usuarios");
            List<Usuario> usuarios = usuarioRepository.findAllWithRelations();

            return usuarios.stream()
                    .map(this::convertirUsuarioAMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar usuarios", e);
            throw new RuntimeException("Error al obtener la lista de usuarios", e);
        }
    }

    public Map<String, Object> convertirUsuarioAMap(Usuario usuario) {
        try {
            Map<String, Object> usuarioMap = new HashMap<>();

            // Información básica del usuario
            usuarioMap.put("id", usuario.getIdUsuario());
            usuarioMap.put("username", usuario.getUsername());
            usuarioMap.put("estado", usuario.isEstado());
            usuarioMap.put("fechaCreacion", usuario.getFechaCreacion());
            usuarioMap.put("ultimoLogin", usuario.getUltimoLogin());

            // Información del rol
            if (usuario.getRol() != null) {
                Map<String, Object> rolMap = new HashMap<>();
                rolMap.put("id", usuario.getRol().getIdRol());
                rolMap.put("nombre", usuario.getRol().getNombre());
                usuarioMap.put("rol", rolMap);
            }

            // Información de la persona
            if (usuario.getPersona() != null) {
                Map<String, Object> personaMap = new HashMap<>();
                personaMap.put("id", usuario.getPersona().getIdPersona());
                personaMap.put("nombres", usuario.getPersona().getNombres());
                personaMap.put("apellidos", usuario.getPersona().getApellidos());
                personaMap.put("email", usuario.getPersona().getEmail());
                personaMap.put("telefono", usuario.getPersona().getTelefono());
                personaMap.put("tipoDocumento", usuario.getPersona().getTipoDocumento());
                personaMap.put("numeroDocumento", usuario.getPersona().getNumeroDocumento());
                usuarioMap.put("persona", personaMap);
            }

            return usuarioMap;
        } catch (Exception e) {
            log.error("Error al convertir usuario a map: {}", usuario.getUsername(), e);
            throw new RuntimeException("Error al procesar datos del usuario", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        try {
            return usuarioRepository.findByIdWithRelations(id)
                    .orElseThrow(() -> new UsuarioNotFoundException(id));
        } catch (UsuarioNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener usuario con ID: {}", id, e);
            throw new RuntimeException("Error al obtener el usuario", e);
        }
    }

    @Override
@Transactional
public Usuario crear(UsuarioDTO usuarioDTO) {
    try {
        // Verificar si la persona existe
        Persona persona = personaRepository.findById(usuarioDTO.getIdPersona())
                .orElseThrow(() -> new UserRegistrationException(
                    "PERSON_NOT_FOUND",
                    "No se encontró la persona con ID: " + usuarioDTO.getIdPersona()
                ));
        
        // Realizar validaciones
        validateExistingUser(persona, usuarioDTO.getUsername());

        // Verificar si el rol existe
        Rol rol = rolRepository.findById(usuarioDTO.getIdRol())
                .orElseThrow(() -> new UserRegistrationException(
                    "ROLE_NOT_FOUND",
                    "No se encontró el rol con ID: " + usuarioDTO.getIdRol()
                ));
        
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setPersona(persona);
        usuario.setRol(rol);
        usuario.setEstado(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);
        
        // Si llegamos aquí, todo salió bien
        log.info("Usuario creado exitosamente: {}", usuario.getUsername());
        return usuario;
        
    } catch (UserRegistrationException e) {
        // Propagar directamente las excepciones de validación
        log.error("Error de validación al crear usuario: {}", e.getMessage());
        throw e;
    } catch (Exception e) {
        log.error("Error al crear usuario: {}", e.getMessage());
        throw new UserRegistrationException(
            "USER_CREATION_ERROR",
            "Error al crear usuario: " + e.getMessage()
        );
    }
}

    @Override
    @Transactional
    public Usuario actualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = obtenerPorId(id);

        // Si se está cambiando el username, verificar que no exista
        if (!usuario.getUsername().equals(usuarioDTO.getUsername()) &&
                usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Obtener persona y rol si se están actualizando
        if (usuarioDTO.getIdPersona() != null
                && !usuario.getPersona().getIdPersona().equals(usuarioDTO.getIdPersona())) {
            Persona persona = personaRepository.findById(usuarioDTO.getIdPersona())
                    .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
            usuario.setPersona(persona);
        }

        if (usuarioDTO.getIdRol() != null && !usuario.getRol().getIdRol().equals(usuarioDTO.getIdRol())) {
            Rol rol = rolRepository.findById(usuarioDTO.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        usuario.setUsername(usuarioDTO.getUsername());
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        usuario.setEstado(usuarioDTO.isEstado());

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(RegistroUsuarioDTO registroDTO) {
        try {
            
            /*
             * // Verificar y obtener el rol
             * Rol rol = rolRepository.findById(registroDTO.getIdRol())
             * .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
             * 
             * // Verificar si la persona ya tiene un usuario con ese rol
             * List<Usuario> usuariosExistentes = usuarioRepository.findAll();
             * boolean rolYaAsignado = usuariosExistentes.stream()
             * .anyMatch(u -> u.getPersona() != null &&
             * u.getRol() != null &&
             * registroDTO.getEmail().equals(u.getPersona().getEmail()) &&
             * u.getRol().getIdRol().equals(registroDTO.getIdRol()));
             * 
             * if (rolYaAsignado) {
             * throw new
             * RuntimeException("La persona ya tiene un usuario con el rol especificado");
             * }
             */


            // Validar datos únicos
            validateNewUser(registroDTO);

            // Verificar y obtener el área
            Area area = areaRepository.findById(registroDTO.getIdArea())
                    .orElseThrow(() -> new RuntimeException("Área no encontrada"));

            // Validar que el área esté activa
            if (!area.isEstado()) {
                log.info("El Área: {} tiene el estado: {}", area.getNombre(), area.isEstado());
                throw new RuntimeException("El área seleccionada no está activa");
            }

            // Validar que el área pertenece a una dirección activa
            if (area.getDireccion() == null || !area.getDireccion().isEstado()) {
                log.info("La Direccion: " + area.getDireccion().getNombre() + " tiene el estado: "
                        + area.getDireccion().isEstado(), area);
                throw new RuntimeException("El área no pertenece a una dirección válida");
            }

            // Validar que la dirección pertenece a una territorial activa
            if (area.getDireccion().getTerritorial() == null ||
                    !area.getDireccion().getTerritorial().isEstado()) {
                log.info("La Territorial: " + area.getDireccion().getTerritorial().getNombre() + " tiene el estado: "
                        + area.getDireccion().getTerritorial().isEstado(), area);
                throw new RuntimeException("La dirección no pertenece a una territorial válida");
            }

            // Validar que la territorial pertenece a una empresa activa
            if (area.getDireccion().getTerritorial().getEmpresa() == null ||
                    !area.getDireccion().getTerritorial().getEmpresa().isEstado()) {
                log.info(
                        "La Empresa: " + area.getDireccion().getTerritorial().getEmpresa().getNombre()
                                + " tiene el estado: " + area.getDireccion().getTerritorial().getEmpresa().isEstado(),
                        area);
                throw new RuntimeException("La territorial no pertenece a una empresa válida");
            }

            // Verificar y obtener la empresa
            Empresa empresa = empresaRepository.findById(registroDTO.getIdEmpresa())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

            // Verificar y obtener el rol
            Rol rol = rolRepository.findById(registroDTO.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Crear la persona
            Persona persona = new Persona();
            persona.setNombres(registroDTO.getNombres());
            persona.setApellidos(registroDTO.getApellidos());
            persona.setTipoDocumento(registroDTO.getTipoDocumento());
            persona.setNumeroDocumento(registroDTO.getNumeroDocumento());
            persona.setEmail(registroDTO.getEmail());
            persona.setTelefono(registroDTO.getTelefono());
            persona.setArea(area);
            persona.setEmpresa(empresa);
            persona.setEstado(true);

            persona = personaRepository.save(persona);

            // Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setUsername(registroDTO.getUsername());
            usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
            usuario.setPersona(persona);
            usuario.setRol(rol);
            usuario.setEstado(false);
            usuario.setFechaCreacion(LocalDateTime.now());

            // Primero guardamos el usuario
            usuario = usuarioRepository.save(usuario); // Importante: guardar y obtener el usuario con ID

            // Generar token y código de activación
            String token = UUID.randomUUID().toString();
            String codigo = generarCodigoActivacion();

            TokenActivacion tokenActivacion = new TokenActivacion();
            tokenActivacion.setToken(token);
            tokenActivacion.setCodigoActivacion(codigo);
            tokenActivacion.setUsuario(usuario); // Ahora usuario tiene un ID
            // tokenActivacion.setFechaExpiracion(LocalDateTime.now().plusDays(1));
            tokenActivacion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
            // tokenActivacion.setEstado(true);
            tokenActivacion.setEstado(false);
            tokenActivacionRepository.save(tokenActivacion);

            // Enviar email de activación
            emailService.enviarEmailActivacion(
                    usuario.getPersona().getEmail(),
                    token,
                    codigo,
                    usuario.getPersona().getNombres());

            return usuario;
        } catch (UserRegistrationException e) {
            // Propagar la excepción directamente sin envolverla
            throw e;
        } catch (Exception e) {
            log.error("Error al registrar usuario: {}", e.getMessage());
            throw new UserRegistrationException(
            "REGISTRATION_ERROR",
            "Error al registrar usuario: " + e.getMessage()
        );
        }
    }

    private String generarCodigoActivacion() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    @Transactional
    public void activarCuenta(String tokenOCodigo) {
        try {
            // Buscar primero por token, si no encuentra, buscar por código
            TokenActivacion token = tokenActivacionRepository.findByToken(tokenOCodigo)
                    .orElseGet(() -> tokenActivacionRepository.findByCodigoActivacion(tokenOCodigo)
                            .orElseThrow(() -> new RuntimeException("Token o código de activación inválido")));

            log.info("Token encontrado - Estado: {}, Fecha Expiración: {}, Fecha Actual: {}",
                    token.isEstado(), token.getFechaExpiracion(), LocalDateTime.now());

            // Obtener el usuario asociado al token
            Usuario usuario = token.getUsuario();
            if (usuario == null) {
                throw new RuntimeException("No se encontró el usuario asociado al token");
            }

            // Validar si ya está activado
            if (usuario.isEstado()) {
                throw new RuntimeException("La cuenta ya se encuentra activada");
            }

            // Validar token
            if (!token.isEstado()) {
                throw new RuntimeException("El token ya ha sido utilizado. Por favor solicite uno nuevo");
            }

            // La validación de expiración debe ser independiente del estado
            if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
                token.setEstado(false);
                tokenActivacionRepository.save(token);
                throw new RuntimeException("El token ha expirado. Por favor solicite uno nuevo");
            }

            // Si llegamos aquí, el token es válido y podemos activar la cuenta
            usuario.setEstado(true);
            usuarioRepository.save(usuario);

            // Marcar el token como usado
            token.setEstado(false);
            tokenActivacionRepository.save(token);

            log.info("Cuenta activada exitosamente para el usuario: {}", usuario.getUsername());

        } catch (Exception e) {
            log.error("Error al activar cuenta: {}", e.getMessage());
            throw new RuntimeException("Error al activar la cuenta: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UsuarioInfoCompletaDTO obtenerInformacionCompleta(Long id) {
        Usuario usuario = usuarioRepository.findByIdWithFullInfo(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioInfoCompletaDTO dto = new UsuarioInfoCompletaDTO();
        
        // Mapear información del usuario
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUsername(usuario.getUsername());
        dto.setEstado(usuario.isEstado());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);

        // Mapear información de la persona
        Persona persona = usuario.getPersona();
        if (persona != null) {
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombres(persona.getNombres());
            dto.setApellidos(persona.getApellidos());
            dto.setTipoDocumento(persona.getTipoDocumento());
            dto.setNumeroDocumento(persona.getNumeroDocumento());
            dto.setEmail(persona.getEmail());
            dto.setTelefono(persona.getTelefono());
            dto.setEstadoPersona(persona.isEstado());
            dto.setFechaCreacionPersona(persona.getFechaCreacion());

            // Mapear información del área
            Area area = persona.getArea();
            if (area != null) {
                dto.setIdArea(area.getIdArea());
                dto.setNombreArea(area.getNombre());
                dto.setEstadoArea(area.isEstado());

                // Mapear información de la dirección
                Direccion direccion = area.getDireccion();
                if (direccion != null) {
                    dto.setIdDireccion(direccion.getIdDireccion());
                    dto.setNombreDireccion(direccion.getNombre());
                    dto.setEstadoDireccion(direccion.isEstado());

                    // Mapear información de la territorial
                    Territorial territorial = direccion.getTerritorial();
                    if (territorial != null) {
                        dto.setIdTerritorial(territorial.getIdTerritorial());
                        dto.setNombreTerritorial(territorial.getNombre());
                        dto.setEstadoTerritorial(territorial.isEstado());

                        // Mapear información de la empresa desde territorial
                        Empresa empresaTerritorial = territorial.getEmpresa();
                        if (empresaTerritorial != null) {
                            dto.setIdEmpresa(empresaTerritorial.getIdEmpresa());
                            dto.setNombreEmpresa(empresaTerritorial.getNombre());
                            dto.setEstadoEmpresa(empresaTerritorial.isEstado());
                        }
                    }
                }
            }

            // Si no se encontró la empresa a través de la jerarquía territorial,
            // usar la empresa directamente asociada a la persona
            if (dto.getIdEmpresa() == null && persona.getEmpresa() != null) {
                dto.setIdEmpresa(persona.getEmpresa().getIdEmpresa());
                dto.setNombreEmpresa(persona.getEmpresa().getNombre());
                dto.setEstadoEmpresa(persona.getEmpresa().isEstado());
            }
        }

        return dto;
    }

    @Transactional
    public Usuario registrarUsuarioLdap(RegistroUsuarioLdapDTO dto) {
        // Verificar si el usuario existe en LDAP
        String username = dto.getUsername(); // Ahora recibimos solo el nombre de usuario sin dominio
    log.debug("Registrando usuario LDAP: {}", username);
        

        // Verificar si ya existe en la base de datos local
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya está registrado en el sistema");
        }

        // Obtener el rol
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear el usuario local
        Usuario usuario = new Usuario();
        usuario.setUsername(username); // Guardamos sin el dominio
        usuario.setEstado(dto.getEstado());
        usuario.setRol(rol);
        usuario.setFechaCreacion(LocalDateTime.now());
        // Establecer una contraseña temporal o hash
        usuario.setPassword(UUID.randomUUID().toString());

        return usuarioRepository.save(usuario);
    }


    @Transactional
    public Usuario actualizarUsuarioLdap(ActualizarUsuarioLdapDTO dto) {
        log.debug("Actualizando usuario LDAP con ID: {}", dto.getIdUsuario());
        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que sea un usuario de LDAP (puedes agregar una bandera en tu entidad para esto)
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new RuntimeException("El usuario no es un usuario de LDAP");
        }

        // Obtener el rol
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Actualizar el usuario
        usuario.setRol(rol);
        usuario.setEstado(dto.getEstado());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuarioLdap(Long id) {
        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que sea un usuario de LDAP
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new RuntimeException("El usuario no es un usuario de LDAP");
        }

        usuarioRepository.delete(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> obtenerUsuariosPorArea(Long areaId) {
        return usuarioRepository.findAllByAreaIdWithFullInfo(areaId).stream()
            .map(usuario -> new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getPersona() != null ? usuario.getPersona().getNombreCompleto() : usuario.getUsername(),
                usuario.getPersona() != null ? usuario.getPersona().getEmail() : null
            ))
            .collect(Collectors.toList());
    }

    private void validateExistingUser(Persona persona, String username) {
        // Validar username
        if (usuarioRepository.existsByUsername(username)) {
            throw new UserRegistrationException(
                "USERNAME_EXISTS",
                "Este nombre de usuario ya está en uso"
            );
        }
    
        // Validar si la persona ya tiene usuario
        if (usuarioRepository.existsByPersona(persona)) {
            throw new UserRegistrationException(
                "USER_ALREADY_EXISTS",
                "La persona ya tiene un usuario asignado"
            );
        }
    
        // Validar PQRS activos
        List<String> estadosFinalizados = Arrays.asList("RESUELTO", "CERRADO");
        if (pqrsRepository.hasActivePqrs(
                persona.getNumeroDocumento(),
                persona.getArea().getIdArea(),
                estadosFinalizados)) {
            
            throw new UserRegistrationException(
                "ACTIVE_PQRS_EXISTS",
                String.format("Ya existe una PQRS activa para este número de documento en el área %s",
                    persona.getArea().getNombre())
            );
        }
    }

    private void validateNewUser(RegistroUsuarioDTO registroDTO) {
        // Validar documento
        if (personaRepository.existsByNumeroDocumento(registroDTO.getNumeroDocumento())) {
            throw new UserRegistrationException(
                "DOCUMENT_EXISTS",
                "Este número de documento ya está registrado"
            );
        }

        // Validar email
        if (personaRepository.existsByEmail(registroDTO.getEmail())) {
            throw new UserRegistrationException(
                "EMAIL_EXISTS",
                "Este correo electrónico ya está registrado"
            );
        }

        // Validar username
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new UserRegistrationException(
                "USERNAME_EXISTS",
                "Este nombre de usuario ya está en uso"
            );
        }

        // Nueva validación para PQRS activo
        // Nueva validación para PQRS activo usando el área
        List<String> estadosFinalizados = Arrays.asList("RESUELTO", "CERRADO");
        if (pqrsRepository.existsByNumeroDocumentoSolicitanteAndTemaAreaIdAreaAndEstadoPqrsNotIn(
                registroDTO.getNumeroDocumento(),
                registroDTO.getIdArea(),
                estadosFinalizados)) {
            throw new UserRegistrationException(
                "ACTIVE_PQRS_EXISTS",
                "Ya existe una PQRS activa para este número de documento en el área seleccionada"
            );
        }

    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        stats.put("usuariosHoy", usuarioRepository.countByDateAfter(now.with(LocalTime.MIN)));
        stats.put("usuariosSemana", usuarioRepository.countByDateAfter(now.minusDays(7)));
        stats.put("usuariosMes", usuarioRepository.countByDateAfter(now.withDayOfMonth(1)));

        List<Object[]> estadosCount = usuarioRepository.countByEstado();
        Map<String, Long> porEstado = new HashMap<>();
        for (Object[] result : estadosCount) {
            porEstado.put(((Boolean)result[0]) ? "Activo" : "Inactivo", (Long)result[1]);
        }
        stats.put("porEstado", porEstado);

        return stats;
    }

}
