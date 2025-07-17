-- Script para registrar rutas públicas de PQRS (SQL Server)
-- Ejecutar este script en la base de datos

-- 1. Verificar módulo PQRS
SELECT 'Verificando módulo PQRS...' as mensaje;
SELECT * FROM modulos WHERE nombre = 'PQRS';

-- Obtener el ID del módulo PQRS
DECLARE @modulo_pqrs_id INT;
SET @modulo_pqrs_id = (SELECT id_modulo FROM modulos WHERE nombre = 'PQRS');

IF @modulo_pqrs_id IS NULL
BEGIN
    PRINT 'ERROR: No se encontró el módulo PQRS';
    RETURN;
END

PRINT 'ID del módulo PQRS: ' + CAST(@modulo_pqrs_id AS VARCHAR(10));

-- 2. Registrar rutas públicas de PQRS
PRINT 'Registrando rutas públicas de PQRS...';

-- Ruta: POST /api/pqrs/publico - Crear PQRS público
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs/publico')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_pqrs_id, '/api/pqrs/publico', 'Crear PQRS público', 1, GETDATE(), 1);
    PRINT 'Ruta /api/pqrs/publico creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1 WHERE ruta = '/api/pqrs/publico';
    PRINT 'Ruta /api/pqrs/publico actualizada como pública';
END

-- Ruta: GET /api/pqrs/radicado/{numeroRadicado} - Consultar por radicado
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs/radicado/**')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_pqrs_id, '/api/pqrs/radicado/**', 'Consultar PQRS por radicado', 1, GETDATE(), 1);
    PRINT 'Ruta /api/pqrs/radicado/** creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1 WHERE ruta = '/api/pqrs/radicado/**';
    PRINT 'Ruta /api/pqrs/radicado/** actualizada como pública';
END

-- Ruta: GET /api/pqrs/consulta/{numeroRadicado}/{token} - Consulta pública con token
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs/consulta/**')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_pqrs_id, '/api/pqrs/consulta/**', 'Consulta pública de PQRS con token', 1, GETDATE(), 1);
    PRINT 'Ruta /api/pqrs/consulta/** creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1 WHERE ruta = '/api/pqrs/consulta/**';
    PRINT 'Ruta /api/pqrs/consulta/** actualizada como pública';
END

-- Ruta: POST /api/pqrs/respuesta/{numeroRadicado}/{token} - Respuesta del solicitante
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/pqrs/respuesta/**')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_pqrs_id, '/api/pqrs/respuesta/**', 'Respuesta del solicitante', 1, GETDATE(), 1);
    PRINT 'Ruta /api/pqrs/respuesta/** creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1 WHERE ruta = '/api/pqrs/respuesta/**';
    PRINT 'Ruta /api/pqrs/respuesta/** actualizada como pública';
END

-- 3. Verificar rutas públicas registradas
PRINT 'Verificando rutas públicas de PQRS registradas...';
SELECT r.ruta, r.descripcion, r.es_publica, m.nombre as modulo
FROM rutas r
INNER JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE r.es_publica = 1 AND m.nombre = 'PQRS';

PRINT 'Script completado exitosamente';
