-- Script para registrar la ruta base /api/pqrs necesaria para el sistema de permisos
-- Esta ruta es requerida por las anotaciones @PermitirLectura, @PermitirEscritura, etc. en PqrsController

USE [tu_base_de_datos]; -- Reemplaza con el nombre de tu base de datos

-- 1. Obtener el ID del módulo PQRS
DECLARE @modulo_pqrs_id INT;
SELECT @modulo_pqrs_id = id_modulo 
FROM modulos 
WHERE nombre = 'PQRS' AND estado = 1;

-- Verificar si se encontró el módulo
IF @modulo_pqrs_id IS NULL
BEGIN
    PRINT 'ERROR: No se encontró el módulo PQRS activo';
    RETURN;
END

PRINT 'Módulo PQRS encontrado con ID: ' + CAST(@modulo_pqrs_id AS VARCHAR(10));

-- 2. Verificar si ya existe la ruta base /api/pqrs
IF EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs')
BEGIN
    PRINT 'La ruta /api/pqrs ya existe en la base de datos';
    
    -- Verificar si está activa
    IF EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs' AND estado = 1)
    BEGIN
        PRINT 'La ruta /api/pqrs está activa';
    END
    ELSE
    BEGIN
        -- Activar la ruta si está inactiva
        UPDATE rutas SET estado = 1 WHERE ruta = '/api/pqrs';
        PRINT 'Ruta /api/pqrs activada exitosamente';
    END
END
ELSE
BEGIN
    -- 3. Crear la ruta base /api/pqrs
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_pqrs_id, '/api/pqrs', 'Controlador base para gestión de PQRS', 1, GETDATE(), 0);
    
    PRINT 'Ruta base /api/pqrs creada exitosamente';
END

-- 4. Obtener el ID de la ruta recién creada o existente
DECLARE @ruta_id INT;
SELECT @ruta_id = id_ruta FROM rutas WHERE ruta = '/api/pqrs';

-- 5. Verificar permisos existentes para el rol USUARIO
DECLARE @rol_usuario_id INT;
SELECT @rol_usuario_id = id_rol FROM roles WHERE nombre = 'USUARIO' AND estado = 1;

IF @rol_usuario_id IS NULL
BEGIN
    PRINT 'ADVERTENCIA: No se encontró el rol USUARIO activo';
END
ELSE
BEGIN
    -- Verificar si ya tiene permisos de lectura
    IF NOT EXISTS (
        SELECT 1 FROM permiso_rol 
        WHERE id_rol = @rol_usuario_id 
        AND id_ruta = @ruta_id 
        AND puede_leer = 1 
        AND estado = 1
    )
    BEGIN
        -- Asignar permiso de lectura al rol USUARIO
        INSERT INTO permiso_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_actualizar, puede_eliminar, estado, fecha_creacion)
        VALUES (@rol_usuario_id, @ruta_id, 1, 1, 1, 0, 1, GETDATE());
        
        PRINT 'Permisos asignados al rol USUARIO para /api/pqrs (Lectura, Escritura, Actualización)';
    END
    ELSE
    BEGIN
        PRINT 'El rol USUARIO ya tiene permisos para /api/pqrs';
    END
END

-- 6. Verificar permisos para otros roles importantes (ADMINISTRADOR, FUNCIONARIO)
DECLARE @rol_admin_id INT, @rol_funcionario_id INT;
SELECT @rol_admin_id = id_rol FROM roles WHERE nombre = 'ADMINISTRADOR' AND estado = 1;
SELECT @rol_funcionario_id = id_rol FROM roles WHERE nombre = 'FUNCIONARIO' AND estado = 1;

-- Asignar permisos completos al ADMINISTRADOR
IF @rol_admin_id IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM permiso_rol 
    WHERE id_rol = @rol_admin_id 
    AND id_ruta = @ruta_id 
    AND estado = 1
)
BEGIN
    INSERT INTO permiso_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_actualizar, puede_eliminar, estado, fecha_creacion)
    VALUES (@rol_admin_id, @ruta_id, 1, 1, 1, 1, 1, GETDATE());
    
    PRINT 'Permisos completos asignados al rol ADMINISTRADOR para /api/pqrs';
END

-- Asignar permisos de lectura y actualización al FUNCIONARIO
IF @rol_funcionario_id IS NOT NULL AND NOT EXISTS (
    SELECT 1 FROM permiso_rol 
    WHERE id_rol = @rol_funcionario_id 
    AND id_ruta = @ruta_id 
    AND estado = 1
)
BEGIN
    INSERT INTO permiso_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_actualizar, puede_eliminar, estado, fecha_creacion)
    VALUES (@rol_funcionario_id, @ruta_id, 1, 0, 1, 0, 1, GETDATE());
    
    PRINT 'Permisos de lectura y actualización asignados al rol FUNCIONARIO para /api/pqrs';
END

-- 7. Verificación final
PRINT '';
PRINT '=== VERIFICACIÓN FINAL ===';
SELECT 
    r.ruta,
    r.descripcion,
    r.estado as ruta_activa,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar
FROM rutas r
LEFT JOIN permiso_rol pr ON r.id_ruta = pr.id_ruta AND pr.estado = 1
LEFT JOIN roles rol ON pr.id_rol = rol.id_rol AND rol.estado = 1
WHERE r.ruta = '/api/pqrs'
ORDER BY rol.nombre;

PRINT 'Script completado exitosamente';
