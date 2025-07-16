-- Script para corregir todas las rutas que no tienen módulo asignado

-- 1. Identificar todas las rutas sin módulo
SELECT 'RUTAS SIN MÓDULO ASIGNADO:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    id_modulo,
    estado,
    fecha_creacion
FROM rutas 
WHERE id_modulo IS NULL 
ORDER BY ruta;

-- 2. Mostrar módulos disponibles
SELECT 'MÓDULOS DISPONIBLES:' as mensaje;
SELECT 
    id_modulo,
    nombre,
    descripcion,
    estado
FROM modulos 
WHERE estado = 1
ORDER BY nombre;

-- 3. Crear módulo "General" si no existe (para rutas que no encajan en otros módulos)
IF NOT EXISTS (SELECT 1 FROM modulos WHERE nombre = 'General' AND estado = 1)
BEGIN
    INSERT INTO modulos (nombre, descripcion, estado, fecha_creacion)
    VALUES ('General', 'Módulo general para rutas diversas del sistema', 1, GETDATE());
    PRINT 'Módulo "General" creado';
END

-- 4. Asignar módulos a rutas basado en patrones de URL
DECLARE @id_modulo_general INT;
DECLARE @id_modulo_auth INT;
DECLARE @id_modulo_usuario INT;
DECLARE @id_modulo_admin INT;
DECLARE @id_modulo_pqrs INT;

-- Obtener IDs de módulos
SELECT @id_modulo_general = id_modulo FROM modulos WHERE nombre = 'General' AND estado = 1;
SELECT @id_modulo_auth = id_modulo FROM modulos WHERE nombre LIKE '%auth%' AND estado = 1;
SELECT @id_modulo_usuario = id_modulo FROM modulos WHERE nombre LIKE '%usuario%' AND estado = 1;
SELECT @id_modulo_admin = id_modulo FROM modulos WHERE nombre LIKE '%admin%' AND estado = 1;
SELECT @id_modulo_pqrs = id_modulo FROM modulos WHERE nombre LIKE '%pqrs%' AND estado = 1;

-- Asignar módulos basado en patrones de ruta
UPDATE rutas 
SET id_modulo = CASE
    -- Rutas de autenticación
    WHEN ruta LIKE '/api/auth%' THEN COALESCE(@id_modulo_auth, @id_modulo_general)
    -- Rutas de usuario/dashboard
    WHEN ruta LIKE '/user-dashboard%' OR ruta LIKE '/api/usuario%' THEN COALESCE(@id_modulo_usuario, @id_modulo_general)
    -- Rutas de administración
    WHEN ruta LIKE '/api/admin%' OR ruta LIKE '/api/roles%' OR ruta LIKE '/api/permisos%' THEN COALESCE(@id_modulo_admin, @id_modulo_general)
    -- Rutas de PQRS
    WHEN ruta LIKE '/api/pqrs%' THEN COALESCE(@id_modulo_pqrs, @id_modulo_general)
    -- Rutas de menú
    WHEN ruta LIKE '/api/menu%' THEN COALESCE(@id_modulo_usuario, @id_modulo_general)
    -- Todas las demás al módulo general
    ELSE @id_modulo_general
END
WHERE id_modulo IS NULL;

PRINT 'Módulos asignados automáticamente basado en patrones de URL';

-- 5. Verificar rutas que aún no tienen módulo
SELECT 'RUTAS QUE AÚN NO TIENEN MÓDULO:' as mensaje;
SELECT 
    id_ruta,
    ruta,
    descripcion,
    id_modulo
FROM rutas 
WHERE id_modulo IS NULL;

-- 6. Asignar módulo general a cualquier ruta restante
UPDATE rutas 
SET id_modulo = @id_modulo_general
WHERE id_modulo IS NULL;

PRINT 'Módulo general asignado a rutas restantes';

-- 7. Verificación final - todas las rutas deben tener módulo
SELECT 'VERIFICACIÓN FINAL - RUTAS CON MÓDULOS:' as mensaje;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion,
    m.nombre as modulo,
    r.estado
FROM rutas r
JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE r.estado = 1
ORDER BY m.nombre, r.ruta;

-- 8. Contar rutas por módulo
SELECT 'RESUMEN - CONTEO POR MÓDULO:' as mensaje;
SELECT 
    m.nombre as modulo,
    COUNT(r.id_ruta) as total_rutas
FROM modulos m
LEFT JOIN rutas r ON m.id_modulo = r.id_modulo AND r.estado = 1
WHERE m.estado = 1
GROUP BY m.id_modulo, m.nombre
ORDER BY COUNT(r.id_ruta) DESC, m.nombre;

-- 9. Verificar que no hay rutas sin módulo
SELECT 'VERIFICACIÓN - RUTAS SIN MÓDULO (DEBE SER 0):' as mensaje;
SELECT COUNT(*) as rutas_sin_modulo
FROM rutas 
WHERE id_modulo IS NULL AND estado = 1;
