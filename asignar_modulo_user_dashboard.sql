-- Script para asignar módulo a la ruta /user-dashboard

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

-- 3. Verificar si existe un módulo apropiado para dashboard/usuario
SELECT 'BUSCANDO MÓDULO APROPIADO:' as mensaje;
SELECT 
    id_modulo,
    nombre,
    descripcion
FROM modulos 
WHERE (nombre LIKE '%dashboard%' OR nombre LIKE '%usuario%' OR nombre LIKE '%user%' OR nombre LIKE '%principal%')
  AND estado = 1;

-- 4. Crear módulo "Dashboard Usuario" si no existe uno apropiado
IF NOT EXISTS (
    SELECT 1 FROM modulos 
    WHERE nombre IN ('Dashboard Usuario', 'User Dashboard', 'Dashboard', 'Usuario')
    AND estado = 1
)
BEGIN
    INSERT INTO modulos (nombre, descripcion, estado, fecha_creacion)
    VALUES ('Dashboard Usuario', 'Módulo para dashboards personalizados de usuarios', 1, GETDATE());
    PRINT 'Módulo "Dashboard Usuario" creado exitosamente';
END
ELSE
BEGIN
    PRINT 'Ya existe un módulo apropiado para dashboard';
END

-- 5. Asignar el módulo a la ruta /user-dashboard
DECLARE @id_modulo_dashboard INT;

-- Buscar el módulo más apropiado (prioridad: Dashboard Usuario > Dashboard > Usuario > primer módulo activo)
SELECT TOP 1 @id_modulo_dashboard = id_modulo
FROM modulos 
WHERE estado = 1
ORDER BY 
    CASE 
        WHEN nombre = 'Dashboard Usuario' THEN 1
        WHEN nombre LIKE '%Dashboard%' THEN 2
        WHEN nombre LIKE '%Usuario%' THEN 3
        WHEN nombre LIKE '%User%' THEN 4
        ELSE 5
    END,
    id_modulo;

-- Actualizar la ruta con el módulo
UPDATE rutas 
SET id_modulo = @id_modulo_dashboard
WHERE ruta = '/user-dashboard' AND id_modulo IS NULL;

PRINT 'Módulo asignado a la ruta /user-dashboard';

-- 6. Verificar que se asignó correctamente
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
JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE r.ruta = '/user-dashboard';

-- 7. Verificar todas las rutas sin módulo (para detectar otros problemas)
SELECT 'OTRAS RUTAS SIN MÓDULO:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    id_modulo,
    estado
FROM rutas 
WHERE id_modulo IS NULL AND estado = 1;

-- 8. Mostrar resumen de rutas por módulo
SELECT 'RESUMEN - RUTAS POR MÓDULO:' as mensaje;
SELECT 
    m.nombre as modulo,
    COUNT(r.id_ruta) as total_rutas,
    STRING_AGG(r.ruta, ', ') as rutas
FROM modulos m
LEFT JOIN rutas r ON m.id_modulo = r.id_modulo AND r.estado = 1
WHERE m.estado = 1
GROUP BY m.id_modulo, m.nombre
ORDER BY m.nombre;
