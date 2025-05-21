package com.claude.springboot.app.security.controllers;

import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PublicEndpoint;
import com.claude.springboot.app.security.dto.ActualizarUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.PersonaDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.RolResponseDTO;
import com.claude.springboot.app.security.dto.UsuarioDTO;
import com.claude.springboot.app.security.dto.UsuarioInfoCompletaDTO;
import com.claude.springboot.app.security.dto.UsuarioLdapResponseDTO;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.TokenActivacion;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.RolRepository;
import com.claude.springboot.app.security.repositories.TokenActivacionRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;
import com.claude.springboot.app.security.service.EmailService;
import com.claude.springboot.app.security.service.UsuarioService;

import jakarta.validation.Valid;

import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.security.annotations.PermitirEliminar;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TokenActivacionRepository tokenActivacionRepository;
    private final EmailService emailService;
    public final RolRepository rolRepository;

    @GetMapping
    @PermitirLectura
    public ResponseEntity<?> listarUsuarios() {
        try {
            log.info("Listando usuarios");
            List<Map<String, Object>> usuarios = usuarioService.listarTodos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error al listar usuarios", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener la lista de usuarios");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping()
    @PermitirEscritura
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioService.crear(usuarioDTO);
            return ResponseEntity.ok(usuarioService.convertirUsuarioAMap(usuario));
        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al crear el usuario");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            Map<String, Object> response = usuarioService.convertirUsuarioAMap(usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al obtener el usuario");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioService.actualizar(id, usuarioDTO);
            Map<String, Object> response = usuarioService.convertirUsuarioAMap(usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al actualizar usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al actualizar el usuario");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error al actualizar usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al actualizar el usuario");
            response.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al eliminar usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al eliminar el usuario");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error al eliminar usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al eliminar el usuario");
            response.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/registro")
    //@PermitirEscritura
    @PublicEndpoint
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroUsuarioDTO registroDTO) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(registroDTO);
            Map<String, Object> response = usuarioService.convertirUsuarioAMap(usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al registrar usuario", e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al registrar usuario");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/activar/{tokenOCodigo}")
    @PublicEndpoint
    public ResponseEntity<?> activarCuenta(@PathVariable String tokenOCodigo) {
        try {
            usuarioService.activarCuenta(tokenOCodigo);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cuenta activada correctamente");
            response.put("estado", "ACTIVACION_EXITOSA");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            // Limpiar el mensaje de error
            String mensaje = e.getMessage().replace("Error al activar la cuenta: ", "");
            response.put("error", mensaje);
            response.put("estado", "ERROR_ACTIVACION");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reenviar-activacion")
    public ResponseEntity<?> reenviarActivacion(@RequestParam String username) {
        try {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            log.debug("El estado es: " + usuario.isEstado());
            if (usuario.isEstado()) {
                throw new RuntimeException(
                        "La cuenta ya está activada " + username + " El estado es: " + usuario.isEstado());
            }

            // Desactivar tokens anteriores
            List<TokenActivacion> tokensAnteriores = tokenActivacionRepository
                    .findAllByUsuarioAndEstadoTrue(usuario);
            tokensAnteriores.forEach(token -> {
                token.setEstado(false);
                tokenActivacionRepository.save(token);
            });

            // Crear nuevo token
            String token = UUID.randomUUID().toString();
            String codigo = String.format("%06d", new Random().nextInt(999999));

            TokenActivacion nuevoToken = new TokenActivacion();
            nuevoToken.setToken(token);
            nuevoToken.setCodigoActivacion(codigo);
            nuevoToken.setUsuario(usuario);
            nuevoToken.setFechaExpiracion(LocalDateTime.now().plusDays(1));
            nuevoToken.setEstado(true);
            tokenActivacionRepository.save(nuevoToken);

            // Reenviar email
            emailService.enviarEmailActivacion(
                    usuario.getPersona().getEmail(),
                    token,
                    codigo,
                    usuario.getPersona().getNombres());

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Se reenvio correo de activación reenviado a " + usuario.getPersona().getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}/info-completa")
    @PublicEndpoint
    public ResponseEntity<UsuarioInfoCompletaDTO> obtenerInformacionCompleta(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerInformacionCompleta(id));
    }

    @PostMapping("/ldap")
    @PermitirEscritura
    public ResponseEntity<?> registrarUsuarioLdap(@Valid @RequestBody RegistroUsuarioLdapDTO dto) {
        try {
            String fullUsername = dto.getUsername();

            // Verificar si ya existe
            if (usuarioRepository.existsByUsername(fullUsername)) {
                throw new RuntimeException("El usuario ya está registrado en el sistema");
            }

            // Obtener el rol
            Rol rol = rolRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setUsername(fullUsername);
            usuario.setEstado(dto.getEstado());
            usuario.setRol(rol);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setPassword(""); // Usuario LDAP, no necesita contraseña local

            usuario = usuarioRepository.save(usuario);

            // Crear DTO de respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario LDAP registrado exitosamente");
            response.put("usuario", convertToResponseDTO(usuario));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al registrar usuario LDAP: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al registrar usuario LDAP");
            response.put("mensaje", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    private UsuarioLdapResponseDTO convertToResponseDTO(Usuario usuario) {
        UsuarioLdapResponseDTO dto = new UsuarioLdapResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUsername(usuario.getUsername());
        dto.setEstado(usuario.isEstado());
        dto.setFechaCreacion(usuario.getFechaCreacion());

        RolResponseDTO rolDTO = new RolResponseDTO();
        rolDTO.setIdRol(usuario.getRol().getIdRol());
        rolDTO.setNombre(usuario.getRol().getNombre());
        dto.setRol(rolDTO);

        return dto;
    }

    @PutMapping("/ldap/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarUsuarioLdap(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioLdapDTO dto) {
        try {
            // Validar que el ID en la URL coincida con el del DTO
            if (!id.equals(dto.getIdUsuario())) {
                throw new IllegalArgumentException(
                        "El ID en la URL no coincide con el ID en el cuerpo de la solicitud");
            }

            // Buscar el usuario
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que sea un usuario LDAP
            if (!usuario.getUsername().endsWith("@mintrabajo.loc")) {
                throw new RuntimeException("El usuario no es un usuario LDAP");
            }

            // Obtener el rol
            Rol rol = rolRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Actualizar el usuario
            usuario.setRol(rol);
            usuario.setEstado(dto.getEstado());

            usuario = usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario LDAP actualizado exitosamente");
            response.put("usuario", convertToResponseDTO(usuario));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al actualizar usuario LDAP: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al actualizar usuario LDAP");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/ldap/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarUsuarioLdap(@PathVariable Long id) {
        try {
            // Buscar el usuario
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que sea un usuario LDAP
            if (!usuario.getUsername().endsWith("@mintrabajo.loc")) {
                throw new RuntimeException("El usuario no es un usuario LDAP");
            }

            // Eliminar el usuario
            usuarioRepository.delete(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario LDAP eliminado exitosamente");
            response.put("username", usuario.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al eliminar usuario LDAP: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error al eliminar usuario LDAP");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // para debuggin
    @GetMapping("/permisos-actuales")
    @PublicEndpoint
    public ResponseEntity<?> obtenerPermisosActuales() {
        log.info("Obteniendo permisos actuales");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        try {
            // Información básica de autenticación
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());

            // Información de Spring Security
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                Map<String, Object> userDetailsInfo = new HashMap<>();
                userDetailsInfo.put("enabled", userDetails.isEnabled());
                userDetailsInfo.put("accountNonExpired", userDetails.isAccountNonExpired());
                userDetailsInfo.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
                userDetailsInfo.put("accountNonLocked", userDetails.isAccountNonLocked());
                response.put("userDetails", userDetailsInfo);
            }

            // Información de la base de datos
            usuarioRepository.findByUsername(auth.getName())
                    .ifPresent(usuario -> {
                        try {
                            // Información del rol
                            if (usuario.getRol() != null) {
                                Map<String, Object> rolInfo = new HashMap<>();
                                rolInfo.put("id", usuario.getRol().getIdRol());
                                rolInfo.put("nombre", usuario.getRol().getNombre());
                                response.put("rol", rolInfo);
                            }

                            // Información de la persona
                            if (usuario.getPersona() != null) {
                                Map<String, Object> personaInfo = new HashMap<>();
                                personaInfo.put("id", usuario.getPersona().getIdPersona());
                                personaInfo.put("nombres", usuario.getPersona().getNombres());
                                personaInfo.put("apellidos", usuario.getPersona().getApellidos());
                                personaInfo.put("email", usuario.getPersona().getEmail());
                                response.put("persona", personaInfo);
                            }

                            // Información adicional del usuario
                            Map<String, Object> usuarioInfo = new HashMap<>();
                            usuarioInfo.put("id", usuario.getIdUsuario());
                            usuarioInfo.put("estado", usuario.isEstado());
                            usuarioInfo.put("ultimoLogin", usuario.getUltimoLogin());
                            response.put("usuario", usuarioInfo);

                        } catch (Exception e) {
                            log.error("Error al procesar información del usuario: {}", e.getMessage());
                        }
                    });

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener permisos actuales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al obtener permisos",
                            "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", auth.isAuthenticated());
        response.put("principal", auth.getPrincipal().getClass().getName());
        response.put("authorities", auth.getAuthorities());
        response.put("details", auth.getDetails());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-token/{tokenOCodigo}")
    public ResponseEntity<?> verificarToken(@PathVariable String tokenOCodigo) {
        try {
            TokenActivacion token = tokenActivacionRepository.findByToken(tokenOCodigo)
                    .orElseGet(() -> tokenActivacionRepository.findByCodigoActivacion(tokenOCodigo)
                            .orElseThrow(() -> new RuntimeException("Token o código de activación inválido")));

            Map<String, Object> response = new HashMap<>();
            response.put("activo", token.isEstado());
            response.put("fechaExpiracion", token.getFechaExpiracion());
            response.put("expirado", LocalDateTime.now().isAfter(token.getFechaExpiracion()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/area/{areaId}")
    @PermitirLectura
    public ResponseEntity<?> obtenerUsuariosPorArea(@PathVariable Long areaId) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerUsuariosPorArea(areaId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/current")
@PermitirLectura
public ResponseEntity<UsuarioInfoCompletaDTO> getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    UsuarioInfoCompletaDTO usuarioInfo = new UsuarioInfoCompletaDTO();
    // Usuario
    usuarioInfo.setIdUsuario(usuario.getIdUsuario());
    usuarioInfo.setUsername(usuario.getUsername());
    usuarioInfo.setEstado(usuario.isEstado());
    usuarioInfo.setFechaCreacion(usuario.getFechaCreacion());
    usuarioInfo.setRol(usuario.getRol().getNombre());

    // Persona
    if (usuario.getPersona() != null) {
        usuarioInfo.setIdPersona(usuario.getPersona().getIdPersona());
        usuarioInfo.setNombres(usuario.getPersona().getNombres());
        usuarioInfo.setApellidos(usuario.getPersona().getApellidos());
        usuarioInfo.setTipoDocumento(usuario.getPersona().getTipoDocumento());
        usuarioInfo.setNumeroDocumento(usuario.getPersona().getNumeroDocumento());
        usuarioInfo.setEmail(usuario.getPersona().getEmail());
        usuarioInfo.setTelefono(usuario.getPersona().getTelefono());
        usuarioInfo.setEstadoPersona(usuario.getPersona().isEstado());
        usuarioInfo.setFechaCreacionPersona(usuario.getPersona().getFechaCreacion());

        // Área
        if (usuario.getPersona().getArea() != null) {
            usuarioInfo.setIdArea(usuario.getPersona().getArea().getIdArea());
            usuarioInfo.setNombreArea(usuario.getPersona().getArea().getNombre());
            usuarioInfo.setEstadoArea(usuario.getPersona().getArea().isEstado());

            // Dirección
            if (usuario.getPersona().getArea().getDireccion() != null) {
                usuarioInfo.setIdDireccion(usuario.getPersona().getArea().getDireccion().getIdDireccion());
                usuarioInfo.setNombreDireccion(usuario.getPersona().getArea().getDireccion().getNombre());
                usuarioInfo.setEstadoDireccion(usuario.getPersona().getArea().getDireccion().isEstado());

                // Territorial
                if (usuario.getPersona().getArea().getDireccion().getTerritorial() != null) {
                    usuarioInfo.setIdTerritorial(usuario.getPersona().getArea().getDireccion().getTerritorial().getIdTerritorial());
                    usuarioInfo.setNombreTerritorial(usuario.getPersona().getArea().getDireccion().getTerritorial().getNombre());
                    usuarioInfo.setEstadoTerritorial(usuario.getPersona().getArea().getDireccion().getTerritorial().isEstado());
                }
            }
        }

        // Empresa
        if (usuario.getPersona().getEmpresa() != null) {
            usuarioInfo.setIdEmpresa(usuario.getPersona().getEmpresa().getIdEmpresa());
            usuarioInfo.setNombreEmpresa(usuario.getPersona().getEmpresa().getNombre());
            usuarioInfo.setEstadoEmpresa(usuario.getPersona().getEmpresa().isEstado());
        }
    }
    
    return ResponseEntity.ok(usuarioInfo);
}

}
