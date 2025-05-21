# Documentación del Endpoint de Opciones de Menú

## Descripción General

Este documento describe la implementación de un nuevo endpoint en el sistema PQRS que permite obtener dinámicamente las opciones de menú disponibles para un usuario autenticado, basándose en sus permisos asignados a través de su rol.

**Endpoint:** `GET /api/menu/opciones`

## Propósito

El propósito de este endpoint es permitir que la aplicación frontend construya dinámicamente el menú de navegación basado en los permisos del usuario autenticado. Esto mejora:

1. **Seguridad**: Solo se muestran las opciones a las que el usuario tiene acceso.
2. **Experiencia de usuario**: La interfaz se adapta a los permisos del usuario.
3. **Mantenibilidad**: Los cambios en permisos se reflejan automáticamente en el menú sin necesidad de modificar el frontend.

## Componentes Implementados

### 1. Controlador: `MenuController`

```java
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    
    @GetMapping("/opciones")
    public ResponseEntity<MenuResponseDTO> getOpcionesMenu() {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Obtener las opciones de menú para el usuario
        MenuResponseDTO menuOptions = menuService.getMenuOptionsForUser(username);
        
        return ResponseEntity.ok(menuOptions);
    }
}
```

Este controlador expone el endpoint `/api/menu/opciones` que:
- Obtiene el usuario autenticado del contexto de seguridad de Spring
- Llama al servicio `MenuService` para obtener las opciones de menú
- Devuelve la respuesta en formato JSON

### 2. Objetos de Transferencia de Datos (DTOs)

#### `MenuResponseDTO`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO {
    private List<ModuloMenuDTO> modulos;
}
```

#### `ModuloMenuDTO`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloMenuDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private List<RutaMenuDTO> rutas;
}
```

#### `RutaMenuDTO`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaMenuDTO {
    private Long id;
    private String ruta;
    private String descripcion;
    private boolean puedeLeer;
    private boolean puedeEscribir;
    private boolean puedeActualizar;
    private boolean puedeEliminar;
}
```

Estos DTOs definen la estructura de la respuesta JSON que se enviará al frontend, organizando jerárquicamente los módulos y sus rutas asociadas con los permisos correspondientes.

### 3. Servicio: `MenuService` y `MenuServiceImpl`

#### Interfaz `MenuService`

```java
public interface MenuService {
    /**
     * Obtiene las opciones de menú para un usuario específico basado en sus permisos
     * 
     * @param username Nombre de usuario
     * @return DTO con la estructura jerárquica de módulos y rutas permitidas
     */
    MenuResponseDTO getMenuOptionsForUser(String username);
}
```

#### Implementación `MenuServiceImpl`

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final UsuarioRepository usuarioRepository;
    private final PermisoRolRepository permisoRolRepository;

    @Override
    @Transactional(readOnly = true)
    public MenuResponseDTO getMenuOptionsForUser(String username) {
        log.info("Obteniendo opciones de menú para el usuario: {}", username);
        
        // Obtener el usuario y su rol
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Rol rol = usuario.getRol();
        if (rol == null) {
            log.warn("El usuario {} no tiene un rol asignado", username);
            return new MenuResponseDTO(new ArrayList<>());
        }
        
        // Obtener todos los permisos del rol donde puede_leer es true
        List<PermisoRol> permisos = permisoRolRepository.findByRolAndPuedeLeerTrue(rol);
        
        // Agrupar las rutas por módulo
        Map<Modulo, List<Ruta>> modulosConRutas = new HashMap<>();
        
        for (PermisoRol permiso : permisos) {
            Ruta ruta = permiso.getRuta();
            if (ruta != null && ruta.isEstado()) {  // Solo incluir rutas activas
                Modulo modulo = ruta.getModulo();
                if (modulo != null && modulo.isEstado()) {  // Solo incluir módulos activos
                    if (!modulosConRutas.containsKey(modulo)) {
                        modulosConRutas.put(modulo, new ArrayList<>());
                    }
                    modulosConRutas.get(modulo).add(ruta);
                }
            }
        }
        
        // Convertir a DTOs
        List<ModuloMenuDTO> modulosDTO = modulosConRutas.entrySet().stream()
                .map(entry -> {
                    Modulo modulo = entry.getKey();
                    List<Ruta> rutas = entry.getValue();
                    
                    // Convertir rutas a DTOs
                    List<RutaMenuDTO> rutasDTO = rutas.stream()
                            .map(ruta -> {
                                // Buscar el permiso específico para esta ruta
                                PermisoRol permiso = permisos.stream()
                                        .filter(p -> p.getRuta().getIdRuta().equals(ruta.getIdRuta()))
                                        .findFirst()
                                        .orElse(null);
                                
                                return RutaMenuDTO.builder()
                                        .id(ruta.getIdRuta())
                                        .ruta(ruta.getRuta())
                                        .descripcion(ruta.getDescripcion())
                                        .puedeLeer(permiso != null && permiso.isPuedeLeer())
                                        .puedeEscribir(permiso != null && permiso.isPuedeEscribir())
                                        .puedeActualizar(permiso != null && permiso.isPuedeActualizar())
                                        .puedeEliminar(permiso != null && permiso.isPuedeEliminar())
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    return ModuloMenuDTO.builder()
                            .id(modulo.getIdModulo())
                            .nombre(modulo.getNombre())
                            .descripcion(modulo.getDescripcion())
                            .rutas(rutasDTO)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new MenuResponseDTO(modulosDTO);
    }
}
```

Este servicio implementa la lógica principal para obtener las opciones de menú:

1. **Obtención del usuario y su rol**: Busca el usuario por su nombre de usuario y obtiene su rol asociado.
2. **Consulta de permisos**: Obtiene todos los permisos del rol donde `puede_leer` es verdadero.
3. **Agrupación por módulo**: Organiza las rutas por módulo para crear una estructura jerárquica.
4. **Filtrado de elementos inactivos**: Solo incluye módulos y rutas que estén activos.
5. **Mapeo a DTOs**: Convierte las entidades a DTOs para la respuesta.

### 4. Repositorio: Método Adicional en `PermisoRolRepository`

```java
/**
 * Encuentra todos los permisos de un rol donde puede_leer es true
 * @param rol El rol para el que se buscan los permisos
 * @return Lista de permisos donde puede_leer es true
 */
List<PermisoRol> findByRolAndPuedeLeerTrue(Rol rol);
```

Este método se agregó al repositorio existente para permitir la consulta de permisos de un rol específico donde el campo `puede_leer` es verdadero.

## Flujo de Ejecución

1. El cliente (frontend) hace una petición GET a `/api/menu/opciones`.
2. Spring Security valida que el usuario esté autenticado.
3. `MenuController` obtiene el nombre de usuario del contexto de seguridad.
4. `MenuServiceImpl` busca el usuario y su rol en la base de datos.
5. `MenuServiceImpl` consulta los permisos del rol donde `puede_leer` es verdadero.
6. `MenuServiceImpl` agrupa las rutas por módulo y construye la estructura jerárquica.
7. `MenuServiceImpl` convierte las entidades a DTOs para la respuesta.
8. `MenuController` devuelve la respuesta en formato JSON.

## Estructura de la Respuesta JSON

La respuesta del endpoint tendrá la siguiente estructura:

```json
{
  "modulos": [
    {
      "id": 1,
      "nombre": "Administración",
      "descripcion": "Módulo de administración del sistema",
      "rutas": [
        {
          "id": 1,
          "ruta": "/api/usuarios",
          "descripcion": "Gestión de usuarios",
          "puedeLeer": true,
          "puedeEscribir": true,
          "puedeActualizar": true,
          "puedeEliminar": false
        },
        {
          "id": 2,
          "ruta": "/api/roles",
          "descripcion": "Gestión de roles",
          "puedeLeer": true,
          "puedeEscribir": false,
          "puedeActualizar": false,
          "puedeEliminar": false
        }
      ]
    },
    {
      "id": 2,
      "nombre": "PQRS",
      "descripcion": "Módulo de gestión de PQRS",
      "rutas": [
        {
          "id": 3,
          "ruta": "/api/pqrs",
          "descripcion": "Gestión de PQRS",
          "puedeLeer": true,
          "puedeEscribir": true,
          "puedeActualizar": true,
          "puedeEliminar": false
        }
      ]
    }
  ]
}
```

## Uso en el Frontend

El frontend puede utilizar esta respuesta para construir dinámicamente el menú de navegación:

```javascript
// Ejemplo de código JavaScript para construir el menú
fetch('/api/menu/opciones')
  .then(response => response.json())
  .then(data => {
    const menu = document.getElementById('main-menu');
    
    data.modulos.forEach(modulo => {
      // Crear elemento de menú para el módulo
      const moduloItem = document.createElement('li');
      moduloItem.className = 'menu-module';
      moduloItem.textContent = modulo.nombre;
      
      // Crear submenú para las rutas
      const subMenu = document.createElement('ul');
      
      modulo.rutas.forEach(ruta => {
        // Solo mostrar rutas que el usuario puede leer
        if (ruta.puedeLeer) {
          const rutaItem = document.createElement('li');
          const rutaLink = document.createElement('a');
          rutaLink.href = ruta.ruta;
          rutaLink.textContent = ruta.descripcion;
          
          // Opcional: Agregar clases o atributos según otros permisos
          if (ruta.puedeEscribir) rutaLink.dataset.canWrite = 'true';
          if (ruta.puedeActualizar) rutaLink.dataset.canUpdate = 'true';
          if (ruta.puedeEliminar) rutaLink.dataset.canDelete = 'true';
          
          rutaItem.appendChild(rutaLink);
          subMenu.appendChild(rutaItem);
        }
      });
      
      // Solo agregar módulos que tengan al menos una ruta
      if (subMenu.children.length > 0) {
        moduloItem.appendChild(subMenu);
        menu.appendChild(moduloItem);
      }
    });
  })
  .catch(error => console.error('Error al cargar el menú:', error));
```

## Ventajas de esta Implementación

1. **Seguridad mejorada**: El menú se construye dinámicamente basado en los permisos reales del usuario.
2. **Mantenibilidad**: Los cambios en permisos se reflejan automáticamente en el menú.
3. **Experiencia de usuario coherente**: Los usuarios solo ven las opciones a las que tienen acceso.
4. **Separación de responsabilidades**: El backend proporciona la estructura y los permisos, el frontend se encarga solo de la presentación.
5. **Rendimiento**: Se realiza una sola consulta para obtener toda la estructura del menú.

## Consideraciones de Seguridad

- Este endpoint solo debe ser accesible para usuarios autenticados.
- La información de permisos se obtiene del servidor, no se almacena en el cliente.
- Los permisos se validan tanto en el frontend (para mostrar/ocultar opciones) como en el backend (para permitir/denegar acciones).
- Se filtran los módulos y rutas inactivos para evitar mostrar opciones no disponibles.

## Posibles Mejoras Futuras

1. **Caché**: Implementar caché para mejorar el rendimiento en aplicaciones con muchos usuarios.
2. **Ordenación**: Agregar capacidad para ordenar módulos y rutas según algún criterio.
3. **Iconos y metadatos**: Extender el modelo para incluir iconos u otros metadatos útiles para el frontend.
4. **Permisos anidados**: Soportar estructuras de menú más complejas con varios niveles de anidamiento.
