# Implementación de Tipo Documento

## Descripción
Este módulo implementa la gestión completa de tipos de documentos de identidad en el sistema PQRS, incluyendo la creación de la tabla en base de datos, entidades JPA, servicios, controladores y endpoints REST.

## Estructura de Archivos Creados

### 1. Base de Datos
- **`scripts/crear_tabla_tipo_documento.sql`**: Script para crear la tabla y datos iniciales
- **`scripts/registrar_rutas_tipos_documento.sql`**: Script para registrar rutas públicas

### 2. Entidades y DTOs
- **`entities/TipoDocumento.java`**: Entidad JPA con validaciones y auditoría
- **`dto/TipoDocumentoDTO.java`**: DTO para transferencia de datos
- **`mappers/TipoDocumentoMapper.java`**: Mapper para conversiones entre entidad y DTO

### 3. Capa de Datos
- **`repositories/TipoDocumentoRepository.java`**: Repositorio con consultas personalizadas

### 4. Capa de Servicio
- **`services/TipoDocumentoService.java`**: Interfaz del servicio
- **`services/TipoDocumentoServiceImpl.java`**: Implementación del servicio con lógica de negocio

### 5. Capa de Controlador
- **`controllers/TipoDocumentoController.java`**: Controlador REST con endpoints completos

## Estructura de la Tabla

```sql
CREATE TABLE tipo_documento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);
```

## Datos Iniciales

El sistema incluye los siguientes tipos de documento por defecto:

| Código | Nombre | Descripción |
|--------|--------|-------------|
| CC | Cédula de Ciudadanía | Documento de identidad para ciudadanos colombianos |
| TI | Tarjeta de Identidad | Documento de identidad para menores de edad |
| RC | Registro Civil | Documento de identidad para menores de 7 años |
| CE | Cédula de Extranjería | Documento de identidad para extranjeros residentes |
| PA | Pasaporte | Documento de identidad internacional |
| NIT | Número de Identificación Tributaria | Documento de identidad para personas jurídicas |
| PEP | Permiso Especial de Permanencia | Documento para migrantes venezolanos |

## Endpoints REST

### Endpoints Públicos (sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tipos-documento` | Obtener todos los tipos de documento |
| GET | `/api/tipos-documento/activos` | Obtener tipos de documento activos |
| GET | `/api/tipos-documento/{id}` | Obtener tipo de documento por ID |
| GET | `/api/tipos-documento/codigo/{codigo}` | Obtener tipo de documento por código |
| GET | `/api/tipos-documento/buscar?termino={termino}` | Buscar tipos de documento |
| GET | `/api/tipos-documento/buscar/activos?termino={termino}` | Buscar tipos de documento activos |
| GET | `/api/tipos-documento/estadisticas` | Obtener estadísticas |
| GET | `/api/tipos-documento/existe/codigo/{codigo}` | Verificar existencia por código |
| GET | `/api/tipos-documento/existe/nombre?nombre={nombre}` | Verificar existencia por nombre |

### Endpoints Privados (requieren autenticación)

| Método | Endpoint | Descripción | Permiso |
|--------|----------|-------------|---------|
| POST | `/api/tipos-documento` | Crear nuevo tipo de documento | Escritura |
| PUT | `/api/tipos-documento/{id}` | Actualizar tipo de documento | Actualizar |
| PATCH | `/api/tipos-documento/{id}/activar` | Activar tipo de documento | Actualizar |
| PATCH | `/api/tipos-documento/{id}/desactivar` | Desactivar tipo de documento | Actualizar |
| DELETE | `/api/tipos-documento/{id}` | Eliminar tipo de documento | Actualizar |

## Características Implementadas

### 1. Validaciones
- Código único (máximo 10 caracteres)
- Nombre único (máximo 100 caracteres)
- Descripción opcional (máximo 255 caracteres)
- Estado obligatorio (por defecto true)

### 2. Auditoría
- Fecha de creación automática
- Fecha de actualización automática
- Métodos `@PrePersist` y `@PreUpdate`

### 3. Búsquedas
- Por ID, código, nombre
- Búsqueda por término (código o nombre)
- Filtros por estado (activo/inactivo)
- Búsquedas case-insensitive

### 4. Operaciones CRUD Completas
- Crear, leer, actualizar, eliminar
- Activar/desactivar (soft delete)
- Validaciones de unicidad
- Manejo de errores

### 5. Estadísticas
- Conteo total, activos, inactivos
- Endpoint de estadísticas agregadas

## Seguridad

### Rutas Públicas
Las rutas de consulta (GET) están configuradas como públicas para permitir acceso sin autenticación, facilitando la integración con formularios públicos.

### Rutas Privadas
Las rutas de modificación (POST, PUT, PATCH, DELETE) requieren autenticación y permisos específicos:
- `@PermitirEscritura` para creación
- `@PermitirActualizar` para modificación y eliminación

## Instalación y Configuración

### 1. Ejecutar Scripts de Base de Datos
```bash
# Crear tabla y datos iniciales
mysql -u usuario -p pqrs_db < scripts/crear_tabla_tipo_documento.sql

# Registrar rutas públicas
mysql -u usuario -p pqrs_db < scripts/registrar_rutas_tipos_documento.sql
```

### 2. Reiniciar Aplicación
**IMPORTANTE**: Después de ejecutar el script de rutas públicas, reinicie la aplicación para que Spring Security reconozca las nuevas rutas públicas.

### 3. Verificar Funcionamiento
```bash
# Probar endpoint público
curl http://localhost:8080/api/tipos-documento/activos

# Verificar datos iniciales
curl http://localhost:8080/api/tipos-documento/codigo/CC
```

## Logging

El sistema incluye logging detallado en todos los niveles:
- INFO para operaciones principales
- DEBUG para consultas
- WARN para recursos no encontrados
- ERROR para errores de validación

## Manejo de Errores

### Errores Comunes
- `400 Bad Request`: Datos inválidos o violación de unicidad
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error del servidor

### Validaciones
- Código y nombre únicos
- Campos obligatorios
- Longitud máxima de campos
- Formato de datos

## Extensibilidad

El diseño permite fácil extensión para:
- Nuevos tipos de documento
- Campos adicionales
- Validaciones personalizadas
- Integración con otros módulos

## Pruebas

### Pruebas Manuales
1. Verificar creación de tabla
2. Probar endpoints públicos
3. Validar datos iniciales
4. Probar búsquedas
5. Verificar estadísticas

### Pruebas Automatizadas
Se recomienda crear pruebas unitarias para:
- Servicios
- Repositorios
- Controladores
- Mappers

## Mantenimiento

### Respaldos
- Respaldar tabla `tipo_documento` regularmente
- Mantener scripts de creación actualizados

### Monitoreo
- Verificar logs de aplicación
- Monitorear rendimiento de consultas
- Revisar estadísticas de uso

## Contacto y Soporte

Para dudas o problemas con la implementación de TipoDocumento, contactar al equipo de desarrollo del Sistema PQRS.
