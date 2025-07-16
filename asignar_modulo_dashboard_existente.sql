-- Script para asignar el módulo Dashboard existente a la ruta /user-dashboard

-- 1. Verificar módulos existentes
SELECT 'MÓDULOS DISPONIBLES:' as mensaje;
SELECT 
    id_modulo,
    nombre,
    descripcion,
    estado
FROM modulos 
WHERE estado = 1
ORDER BY nombre;

-- 2. Verificar la ruta /user-dashboard sin módulo
SELECT 'RUTA /user-dashboard SIN MÓDULO:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    id_modulo,
    estado
FROM rutas 
WHERE ruta = '/user-dashboard';

-- 3. Verificar que existe el módulo Dashboard
SELECT 'VERIFICANDO MÓDULO DASHBOARD:' as mensaje;
SELECT 
    id_modulo,
    nombre,
    descripcion,
    estado
FROM modulos 
WHERE nombre = 'Dashboard' AND estado = 1;

-- 4. Asignar el módulo Dashboard a la ruta /user-dashboard
DECLARE @id_modulo_dashboard INT;

-- Obtener el ID del módulo Dashboard
SELECT @id_modulo_dashboard = id_modulo 
FROM modulos 
WHERE nombre = 'Dashboard' AND estado = 1;

-- Verificar que se encontró el módulo
IF @id_modulo_dashboard IS NOT NULL
BEGIN
    -- Actualizar la ruta con el módulo Dashboard
    UPDATE rutas 
    SET id_modulo = @id_modulo_dashboard
    WHERE ruta = '/user-dashboard';
    
    PRINT 'Módulo Dashboard asignado exitosamente a la ruta /user-dashboard';
END
ELSE
BEGIN
    PRINT 'ERROR: No se encontró el módulo Dashboard activo';
    PRINT 'Módulos disponibles:';
    SELECT nombre FROM modulos WHERE estado = 1;
END

-- 5. Verificar que se asignó correctamente
SELECT 'VERIFICACIÓN - RUTA CON MÓDULO ASIGNADO:' as mensaje;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion,
    r.id_modulo,
    m.nombre as nombre_modulo,
    m.descripcion as descripcion_modulo,
    r.estado
FROM rutas r
LEFT JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE r.ruta = '/user-dashboard';

-- 6. Mostrar todas las rutas del módulo Dashboard
SELECT 'TODAS LAS RUTAS DEL MÓDULO DASHBOARD:' as mensaje;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion,
    r.estado
FROM rutas r
JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE m.nombre = 'Dashboard' AND m.estado = 1 AND r.estado = 1
ORDER BY r.ruta;

-- 7. Verificar otras rutas sin módulo (para detectar problemas similares)
SELECT 'OTRAS RUTAS SIN MÓDULO:' as mensaje;
SELECT 
    COUNT(*) as total_rutas_sin_modulo
FROM rutas 
WHERE id_modulo IS NULL AND estado = 1;

-- Si hay rutas sin módulo, mostrarlas
IF EXISTS (SELECT 1 FROM rutas WHERE id_modulo IS NULL AND estado = 1)
BEGIN
    SELECT 'LISTADO DE RUTAS SIN MÓDULO:' as mensaje;
    SELECT 
        id_ruta,
        ruta,
        descripcion
    FROM rutas 
    WHERE id_modulo IS NULL AND estado = 1
    ORDER BY ruta;
END
