-- =====================================================
-- Script para registrar rutas públicas de Tipos de Documento
-- Sistema PQRS - Gestión de Tipos de Documento
-- Compatible con SQL Server
-- =====================================================

USE roles;
GO

-- Mostrar información inicial
PRINT 'Iniciando registro de rutas públicas para Tipos de Documento...';

-- Verificar si la tabla rutas existe
PRINT 'Verificando existencia de tabla rutas...';

-- Insertar ruta pública para tipos de documento
-- Nota: La anotación @PermitirLectura solo verifica la ruta base construida automáticamente

-- Ruta base para tipos de documento (construida automáticamente por la anotación)
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/tipodocumentos')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (6, '/api/tipodocumentos', 'Acceso público a endpoints de tipos de documento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/tipodocumentos registrada como PÚBLICA';
END
ELSE
    PRINT 'Ruta /api/tipodocumentos ya existe';


-- Mostrar confirmación de rutas insertadas
PRINT 'Rutas públicas registradas exitosamente';
PRINT '';

-- Mostrar las rutas de tipos de documento registradas
PRINT 'Rutas de tipos de documento registradas:';
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion,
    CASE 
        WHEN r.es_publica = 1 THEN 'Pública'
        ELSE 'Privada'
    END AS tipo_acceso,
    CASE 
        WHEN r.estado = 1 THEN 'Activa'
        ELSE 'Inactiva'
    END AS estado,
    r.fecha_creacion
FROM rutas r
WHERE r.ruta LIKE '/api/tipodocumentos%'
ORDER BY r.ruta;

-- Mostrar resumen de rutas públicas totales
PRINT '';
PRINT 'Resumen de rutas públicas:';
SELECT 
    COUNT(*) as total_rutas_publicas,
    'rutas públicas registradas en el sistema' as descripcion
FROM rutas 
WHERE es_publica = 1 AND estado = 1;

-- Mostrar mensaje final
PRINT '';
PRINT 'Script completado exitosamente';
PRINT 'IMPORTANTE: Reinicie la aplicación para que las nuevas rutas públicas tomen efecto';
PRINT '';

-- Mostrar las rutas específicas de tipos de documento para verificación
PRINT 'Rutas de Tipos de Documento registradas:';
SELECT '  - ' + ruta AS rutas_registradas
FROM rutas 
WHERE ruta LIKE '/api/tipodocumentos%' 
  AND es_publica = 1 
  AND estado = 1
ORDER BY ruta;

PRINT '';
PRINT 'Proceso completado. Las rutas están listas para usar después del reinicio de la aplicación.';
