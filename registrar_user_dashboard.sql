-- Script para registrar la ruta /user-dashboard y asignar permisos al rol USUARIO

-- 1. Verificar si ya existe la ruta /user-dashboard
SELECT 'VERIFICANDO RUTA /user-dashboard:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    estado,
    fecha_creacion
FROM rutas 
WHERE ruta = '/user-dashboard';

-- 2. Verificar el rol USUARIO
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
WHERE r.ruta = '/user-dashboard' AND rol.nombre = 'USUARIO';

-- 4. Crear la ruta /user-dashboard si no existe
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/user-dashboard')
BEGIN
    INSERT INTO rutas (ruta, descripcion, estado, fecha_creacion)
    VALUES ('/user-dashboard', 'Dashboard personalizado para usuarios con rol USUARIO', 1, GETDATE());
    PRINT 'Ruta /user-dashboard creada exitosamente';
END
ELSE
BEGIN
    PRINT 'La ruta /user-dashboard ya existe';
END

-- 5. Asignar permisos completos al rol USUARIO para /user-dashboard
IF NOT EXISTS (
    SELECT 1 
    FROM permisos_rol pr
    JOIN rutas r ON pr.id_ruta = r.id_ruta
    JOIN roles rol ON pr.id_rol = rol.id_rol
    WHERE r.ruta = '/user-dashboard' AND rol.nombre = 'USUARIO'
)
BEGIN
    DECLARE @id_ruta_dashboard INT;
    DECLARE @id_rol_usuario INT;
    
    -- Obtener IDs
    SELECT @id_ruta_dashboard = id_ruta FROM rutas WHERE ruta = '/user-dashboard';
    SELECT @id_rol_usuario = id_rol FROM roles WHERE nombre = 'USUARIO';
    
    -- Insertar permisos completos (CRUD) para el dashboard personalizado
    INSERT INTO permisos_rol (id_rol, id_ruta, puede_leer, puede_escribir, puede_actualizar, puede_eliminar, estado, fecha_creacion)
    VALUES (@id_rol_usuario, @id_ruta_dashboard, 1, 1, 1, 1, 1, GETDATE());
    
    PRINT 'Permisos completos (CRUD) asignados al rol USUARIO para /user-dashboard';
END
ELSE
BEGIN
    -- Verificar si el permiso existe pero está deshabilitado o incompleto
    IF EXISTS (
        SELECT 1 
        FROM permisos_rol pr
        JOIN rutas r ON pr.id_ruta = r.id_ruta
        JOIN roles rol ON pr.id_rol = rol.id_rol
        WHERE r.ruta = '/user-dashboard' AND rol.nombre = 'USUARIO'
        AND (pr.puede_leer = 0 OR pr.puede_escribir = 0 OR pr.puede_actualizar = 0 OR pr.puede_eliminar = 0 OR pr.estado = 0)
    )
    BEGIN
        -- Actualizar permiso existente para dar acceso completo
        UPDATE pr
        SET puede_leer = 1, 
            puede_escribir = 1, 
            puede_actualizar = 1, 
            puede_eliminar = 1, 
            estado = 1
        FROM permisos_rol pr
        JOIN rutas r ON pr.id_ruta = r.id_ruta
        JOIN roles rol ON pr.id_rol = rol.id_rol
        WHERE r.ruta = '/user-dashboard' AND rol.nombre = 'USUARIO';
        
        PRINT 'Permiso existente actualizado para dar acceso completo (CRUD)';
    END
    ELSE
    BEGIN
        PRINT 'El permiso ya existe y tiene acceso completo';
    END
END

-- 6. Verificación final de la ruta creada
SELECT 'RUTA CREADA - VERIFICACIÓN:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    estado,
    fecha_creacion
FROM rutas 
WHERE ruta = '/user-dashboard';

-- 7. Verificación final del permiso asignado
SELECT 'PERMISO ASIGNADO - VERIFICACIÓN:' as mensaje;
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
    pr.estado,
    pr.fecha_creacion,
    CASE 
        WHEN pr.puede_leer = 1 THEN 'LECTURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_escribir = 1 THEN 'ESCRITURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_actualizar = 1 THEN 'ACTUALIZAR '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_eliminar = 1 THEN 'ELIMINAR '
        ELSE ''
    END as permisos_otorgados
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/user-dashboard' AND rol.nombre = 'USUARIO';

-- 8. Mostrar resumen de todas las rutas del usuario después de la adición
SELECT 'RESUMEN - TODAS LAS RUTAS DEL USUARIO:' as mensaje;
SELECT 
    r.ruta,
    CASE 
        WHEN pr.puede_leer = 1 THEN 'LECTURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_escribir = 1 THEN 'ESCRITURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_actualizar = 1 THEN 'ACTUALIZAR '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_eliminar = 1 THEN 'ELIMINAR '
        ELSE ''
    END as permisos
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND pr.estado = 1
  AND r.estado = 1
ORDER BY r.ruta;
