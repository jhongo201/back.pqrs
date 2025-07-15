-- Script para verificar y asignar permiso de lectura para /api/pqrss al rol USUARIO

-- 1. Verificar si existe la ruta /api/pqrss
SELECT 'VERIFICANDO RUTA /api/pqrss:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    estado
FROM rutas 
WHERE ruta = '/api/pqrss';

-- 2. Verificar si existe el rol USUARIO
SELECT 'VERIFICANDO ROL USUARIO:' as mensaje;
SELECT 
    id_rol,
    nombre,
    descripcion,
    estado
FROM roles 
WHERE nombre = 'USUARIO';

-- 3. Verificar si ya existe el permiso
SELECT 'VERIFICANDO PERMISO EXISTENTE:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_rol,
    pr.id_ruta,
    r.ruta,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/api/pqrss' AND rol.nombre = 'USUARIO';

-- 4. Crear la ruta si no existe
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrss')
BEGIN
    INSERT INTO rutas (ruta, descripcion, estado)
    VALUES ('/api/pqrss', 'Endpoint para estadísticas del dashboard PQRS', 1);
    PRINT 'Ruta /api/pqrss creada exitosamente';
END
ELSE
BEGIN
    PRINT 'La ruta /api/pqrss ya existe';
END

-- 5. Asignar permiso si no existe
IF NOT EXISTS (
    SELECT 1 
    FROM permisos_rol pr
    JOIN rutas r ON pr.id_ruta = r.id_ruta
    JOIN roles rol ON pr.id_rol = rol.id_rol
    WHERE r.ruta = '/api/pqrss' AND rol.nombre = 'USUARIO'
)
BEGIN
    DECLARE @id_ruta INT;
    DECLARE @id_rol INT;
    
    -- Obtener IDs
    SELECT @id_ruta = id_ruta FROM rutas WHERE ruta = '/api/pqrss';
    SELECT @id_rol = id_rol FROM roles WHERE nombre = 'USUARIO';
    
    -- Insertar permiso
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_actualizar, puede_eliminar, estado)
    VALUES (@id_rol, @id_ruta, 1, 0, 0, 0, 1);
    
    PRINT 'Permiso de lectura asignado al rol USUARIO para /api/pqrss';
END
ELSE
BEGIN
    -- Verificar si el permiso existe pero está deshabilitado
    IF EXISTS (
        SELECT 1 
        FROM permisos_rol pr
        JOIN rutas r ON pr.id_ruta = r.id_ruta
        JOIN roles rol ON pr.id_rol = rol.id_rol
        WHERE r.ruta = '/api/pqrss' AND rol.nombre = 'USUARIO'
        AND (pr.puede_leer = 0 OR pr.estado = 0)
    )
    BEGIN
        -- Actualizar permiso existente
        UPDATE pr
        SET puede_leer = 1, estado = 1
        FROM permisos_rol pr
        JOIN rutas r ON pr.id_ruta = r.id_ruta
        JOIN roles rol ON pr.id_rol = rol.id_rol
        WHERE r.ruta = '/api/pqrss' AND rol.nombre = 'USUARIO';
        
        PRINT 'Permiso existente actualizado para habilitar lectura';
    END
    ELSE
    BEGIN
        PRINT 'El permiso ya existe y está habilitado';
    END
END

-- 6. Verificación final
SELECT 'VERIFICACIÓN FINAL:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_rol,
    pr.id_ruta,
    r.ruta,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/api/pqrss' AND rol.nombre = 'USUARIO';

-- 7. Mostrar todos los permisos del rol USUARIO para verificar
SELECT 'TODOS LOS PERMISOS DEL ROL USUARIO:' as mensaje;
SELECT 
    pr.id_permiso,
    r.ruta,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' AND pr.estado = 1
ORDER BY r.ruta;
