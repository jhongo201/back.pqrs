package com.claude.springboot.app.security.service;



import com.claude.springboot.app.dto.UsuarioResponseDTO;
import com.claude.springboot.app.security.dto.ActualizarUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioDTO;
import com.claude.springboot.app.security.dto.RegistroUsuarioLdapDTO;
import com.claude.springboot.app.security.dto.UsuarioDTO;
import com.claude.springboot.app.security.dto.UsuarioInfoCompletaDTO;
import com.claude.springboot.app.security.entities.Usuario;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface UsuarioService {
   // List<Usuario> listarTodos();
   List<Map<String, Object>> listarTodos();
    Usuario obtenerPorId(Long id);
    Usuario crear(UsuarioDTO usuarioDTO);
    Usuario actualizar(Long id, UsuarioDTO usuarioDTO);
    void eliminar(Long id);
    Map<String, Object> convertirUsuarioAMap(Usuario usuario);
    Usuario registrarUsuario(RegistroUsuarioDTO registroDTO);
    void activarCuenta(String tokenOCodigo);

    UsuarioInfoCompletaDTO  obtenerInformacionCompleta(Long id);
    // Nuevo método para LDAP
    Usuario registrarUsuarioLdap(RegistroUsuarioLdapDTO dto);
    Usuario actualizarUsuarioLdap(ActualizarUsuarioLdapDTO dto);
    void eliminarUsuarioLdap(Long id);
    List<UsuarioResponseDTO> obtenerUsuariosPorArea(Long areaId);
    Map<String, Object> getDashboardStats();
    

}
