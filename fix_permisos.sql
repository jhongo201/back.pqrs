-- Script para corregir permisos de /api/permisorols (SQL Server)
-- Ejecutar este script en la base de datos

-- 1. Verificar si la ruta ya existe
SELECT 'Verificando ruta existente...' as mensaje;
SELECT * FROM rutas WHERE ruta = '/api/permisorols';

-- 2. Insertar la ruta si no existe
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/permisorols')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (2, '/api/permisorols', 'Gestión de permisos por rol', 1, GETDATE(), 0);
    PRINT 'Ruta /api/permisorols creada exitosamente';
END
ELSE
BEGIN
    PRINT 'La ruta /api/permisorols ya existe';
END

-- 3. Obtener el ID de la ruta y crear el permiso
DECLARE @ruta_id INT;
SET @ruta_id = (SELECT id_ruta FROM rutas WHERE ruta = '/api/permisorols');
SELECT 'ID de ruta /api/permisorols: ' + CAST(@ruta_id AS VARCHAR(10)) as mensaje;

-- 4. Verificar si ya existe el permiso
SELECT 'Verificando permiso existente...' as mensaje;
SELECT * FROM permisos_rol WHERE id_rol = 2 AND id_ruta = @ruta_id;

-- 5. Insertar el permiso de lectura para el rol USUARIO (id=2)
IF NOT EXISTS (SELECT 1 FROM permisos_rol WHERE id_rol = 2 AND id_ruta = @ruta_id)
BEGIN
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_eliminar, estado, fecha_creacion, puede_actualizar)
    VALUES (2, @ruta_id, 1, 0, 0, 1, GETDATE(), 0);
    PRINT 'Permiso de lectura creado exitosamente para el rol USUARIO';
END
ELSE
BEGIN
    PRINT 'El permiso ya existe, verificando si tiene lectura habilitada...';
    
    -- Actualizar el permiso para asegurar que tenga lectura habilitada
    UPDATE permisos_rol 
    SET puede_leer = 1, estado = 1
    WHERE id_rol = 2 AND id_ruta = @ruta_id;
    
    PRINT 'Permiso actualizado para habilitar lectura';
END

-- 6. Verificar que se creó correctamente
SELECT 'Verificación final...' as mensaje;
SELECT pr.*, r.ruta, rol.nombre as rol_nombre
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/api/permisorols' AND rol.nombre = 'USUARIO';

SELECT 'Script completado exitosamente!' as mensaje;
