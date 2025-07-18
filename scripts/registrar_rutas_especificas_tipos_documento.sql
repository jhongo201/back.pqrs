-- =====================================================
-- Script para registrar rutas específicas de Tipos de Documento
-- Registra todas las rutas específicas como públicas
-- Compatible con SQL Server
-- =====================================================

USE roles;
GO

-- Mostrar información inicial
PRINT 'Registrando rutas específicas de Tipos de Documento...';
PRINT '';

-- Registrar todas las rutas específicas como públicas
-- Ruta para listar activos
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/activos')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/activos', 'Listar tipos de documento activos', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/activos registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/activos ya existe';

-- Ruta para obtener por ID
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/{id}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/{id}', 'Obtener tipo de documento por ID', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/{id} registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/{id} ya existe';

-- Ruta para obtener por código
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/codigo/{codigo}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/codigo/{codigo}', 'Obtener tipo de documento por código', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/codigo/{codigo} registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/codigo/{codigo} ya existe';

-- Ruta para buscar
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/buscar')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/buscar', 'Buscar tipos de documento por término', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/buscar registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/buscar ya existe';

-- Ruta para buscar activos
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/buscar/activos')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/buscar/activos', 'Buscar tipos de documento activos por término', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/buscar/activos registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/buscar/activos ya existe';

-- Ruta para estadísticas
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/estadisticas')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/estadisticas', 'Obtener estadísticas de tipos de documento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/estadisticas registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/estadisticas ya existe';

-- Ruta para verificar existencia por código
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/existe/codigo/{codigo}')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/existe/codigo/{codigo}', 'Verificar existencia por código', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/existe/codigo/{codigo} registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/existe/codigo/{codigo} ya existe';

-- Ruta para verificar existencia por nombre
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos/existe/nombre')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos/existe/nombre', 'Verificar existencia por nombre', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos/existe/nombre registrada';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos/existe/nombre ya existe';

PRINT '';
PRINT 'Todas las rutas específicas han sido registradas como públicas';

-- Mostrar todas las rutas registradas
PRINT '';
PRINT 'Rutas de Tipos de Documento registradas:';
SELECT 
    id_ruta,
    ruta,
    descripcion,
    CASE 
        WHEN es_publica = 1 THEN 'Pública'
        ELSE 'Privada'
    END AS tipo_acceso
FROM rutas 
WHERE ruta LIKE '/api/tipodocumentos%'
ORDER BY ruta;

PRINT '';
PRINT 'IMPORTANTE: Reinicie la aplicación para que las rutas tomen efecto';
