-- Script para registrar rutas de Departamentos y Municipios
-- Ejecutar después de crear las entidades geográficas

-- Verificar si las rutas ya existen antes de insertarlas
-- Rutas de Departamentos
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos', 'Gestión de departamentos de Colombia', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'La ruta /api/departamentos ya existe';
END

-- Rutas de Municipios
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/municipios')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/municipios', 'Gestión de municipios de Colombia', 1, GETDATE(), 1);
    PRINT 'Ruta /api/municipios registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/municipios ya existe';
END

GO

-- =============================================
-- REGISTRAR SUB-RUTAS ESPECÍFICAS DE DEPARTAMENTOS
-- =============================================

-- Ruta: /api/departamentos/buscar
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/buscar')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/buscar', 'Buscar departamentos por nombre', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/buscar registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/buscar ya existe';
END

-- Ruta: /api/departamentos/activos
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/activos')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/activos', 'Listar departamentos activos', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/activos registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/activos ya existe';
END

-- Ruta: /api/departamentos/estadisticas
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/estadisticas')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/estadisticas', 'Estadísticas de departamentos', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/estadisticas registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/estadisticas ya existe';
END

-- Ruta: /api/departamentos/region (patrón genérico)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/region')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/region', 'Departamentos por región', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/region registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/region ya existe';
END

-- Ruta: /api/departamentos/{codigo} (con parámetro)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/{codigo}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/{codigo}', 'Obtener departamento por código DANE', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/{codigo} registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/{codigo} ya existe';
END

-- Ruta: /api/departamentos/region/{region} (con parámetro)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/departamentos/region/{region}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/departamentos/region/{region}', 'Departamentos por región específica', 1, GETDATE(), 1);
    PRINT 'Ruta /api/departamentos/region/{region} registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/departamentos/region/{region} ya existe';
END

-- =============================================
-- REGISTRAR SUB-RUTAS ESPECÍFICAS DE MUNICIPIOS
-- =============================================

-- Ruta: /api/municipios/con-departamento
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/municipios/con-departamento')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/municipios/con-departamento', 'Municipios con información de departamento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/municipios/con-departamento registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/municipios/con-departamento ya existe';
END

-- Ruta: /api/municipios/departamento (patrón genérico)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/municipios/departamento')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/municipios/departamento', 'Municipios por departamento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/municipios/departamento registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/municipios/departamento ya existe';
END

-- Ruta: /api/municipios/{codigo} (con parámetro)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/municipios/{codigo}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/municipios/{codigo}', 'Obtener municipio por código DANE', 1, GETDATE(), 1);
    PRINT 'Ruta /api/municipios/{codigo} registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/municipios/{codigo} ya existe';
END

-- Ruta: /api/municipios/departamento/{codigo} (con parámetro)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/municipios/departamento/{codigo}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica) 
    VALUES (5, '/api/municipios/departamento/{codigo}', 'Municipios por código de departamento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/municipios/departamento/{codigo} registrada exitosamente como PÚBLICA';
END
ELSE
BEGIN
    PRINT 'Ruta /api/municipios/departamento/{codigo} ya existe';
END

GO

-- Obtener los IDs de las rutas recién creadas
DECLARE @rutaDepartamentosId INT;
DECLARE @rutaMunicipiosId INT;

SELECT @rutaDepartamentosId = id_ruta FROM rutas WHERE ruta = '/api/departamentos';
SELECT @rutaMunicipiosId = id_ruta FROM rutas WHERE ruta = '/api/municipios';

-- NOTAS SOBRE MÓDULOS DISPONIBLES:
-- ID 1: Usuarios - Gestión de usuarios
-- ID 2: Roles - Gestión de roles  
-- ID 3: PQRS - Gestión de PQRS
-- ID 4: Seguridad - Módulo de gestión de seguridad
-- ID 5: Administración - Módulo de administración general
-- ID 6: Reportes - Módulo de reportes y estadísticas
-- Las rutas geográficas se asignan al módulo de Administración (ID = 5)

-- Crear permisos para el rol ADMINISTRADOR (ID = 1) - Todos los permisos
IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 1 AND id_ruta = @rutaDepartamentosId)
BEGIN
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
    VALUES (1, @rutaDepartamentosId, 1, 1, 1, 1, GETDATE(), 1);
    PRINT 'Permisos de ADMINISTRADOR para /api/departamentos creados';
END

IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 1 AND id_ruta = @rutaMunicipiosId)
BEGIN
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
    VALUES (1, @rutaMunicipiosId, 1, 1, 1, 1, GETDATE(), 1);
    PRINT 'Permisos de ADMINISTRADOR para /api/municipios creados';
END

-- Crear permisos para el rol USUARIO (ID = 2) - Solo lectura
IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 2 AND id_ruta = @rutaDepartamentosId)
BEGIN
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
    VALUES (2, @rutaDepartamentosId, 1, 0, 0, 1, GETDATE(), 0);
    PRINT 'Permisos de USUARIO para /api/departamentos creados (solo lectura)';
END

IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 2 AND id_ruta = @rutaMunicipiosId)
BEGIN
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
    VALUES (2, @rutaMunicipiosId, 1, 0, 0, 1, GETDATE(), 0);
    PRINT 'Permisos de USUARIO para /api/municipios creados (solo lectura)';
END

-- Crear permisos para otros roles si existen
-- Rol FUNCIONARIO (ID = 3) - Lectura y escritura, sin eliminar
IF EXISTS (SELECT 1 FROM roles WHERE id_rol = 3)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 3 AND id_ruta = @rutaDepartamentosId)
    BEGIN
        INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
        VALUES (3, @rutaDepartamentosId, 1, 1, 0, 1, GETDATE(), 1);
        PRINT 'Permisos de FUNCIONARIO para /api/departamentos creados';
    END

    IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 3 AND id_ruta = @rutaMunicipiosId)
    BEGIN
        INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
        VALUES (3, @rutaMunicipiosId, 1, 1, 0, 1, GETDATE(), 1);
        PRINT 'Permisos de FUNCIONARIO para /api/municipios creados';
    END
END

-- Verificar que los permisos se crearon correctamente
PRINT '=== VERIFICACIÓN DE PERMISOS CREADOS ===';

SELECT 
    r.nombre as rol,
    rt.ruta,
    m.nombre as modulo,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
INNER JOIN roles r ON pr.id_rol = r.id_rol
INNER JOIN rutas rt ON pr.id_ruta = rt.id_ruta
INNER JOIN modulos m ON rt.id_modulo = m.id_modulo
WHERE rt.ruta IN ('/api/departamentos', '/api/municipios')
ORDER BY rt.ruta, r.nombre;

PRINT '=== SCRIPT COMPLETADO ===';
PRINT 'RUTAS PRINCIPALES Y SUB-RUTAS registradas como PÚBLICAS:';
PRINT '- /api/departamentos (principal)';
PRINT '- /api/departamentos/buscar';
PRINT '- /api/departamentos/activos';
PRINT '- /api/departamentos/estadisticas';
PRINT '- /api/departamentos/region';
PRINT '- /api/departamentos/{codigo}';
PRINT '- /api/departamentos/region/{region}';
PRINT '- /api/municipios (principal)';
PRINT '- /api/municipios/con-departamento';
PRINT '- /api/municipios/departamento';
PRINT '- /api/municipios/{codigo}';
PRINT '- /api/municipios/departamento/{codigo}';
PRINT '';
PRINT 'NOTA: Las rutas con {codigo} y {region} son placeholders literales.';
PRINT 'El sistema debe hacer matching exacto con estos patrones.';
PRINT '';
PRINT 'Al ser rutas públicas (es_publica = 1), estarán accesibles sin restricciones.';
PRINT 'Permisos adicionales: ADMINISTRADOR (todos), USUARIO (lectura), FUNCIONARIO (lectura/escritura).';
PRINT 'Recuerda reiniciar la aplicación Spring Boot para que los cambios surtan efecto.';
