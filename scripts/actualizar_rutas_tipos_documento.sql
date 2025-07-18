-- =====================================================
-- Script para actualizar rutas existentes de Tipos de Documento
-- Cambia de /api/tipos-documento a /api/tipodocumentos
-- Compatible con SQL Server
-- =====================================================

USE roles;
GO

-- Mostrar información inicial
PRINT 'Iniciando actualización de rutas de Tipos de Documento...';
PRINT 'Cambiando de /api/tipos-documento a /api/tipodocumentos';
PRINT '';

-- Mostrar rutas existentes antes de la actualización
PRINT 'Rutas existentes antes de la actualización:';
SELECT 
    id_ruta,
    ruta,
    descripcion,
    CASE 
        WHEN es_publica = 1 THEN 'Pública'
        ELSE 'Privada'
    END AS tipo_acceso
FROM rutas 
WHERE ruta LIKE '/api/tipos-documento%'
ORDER BY ruta;

PRINT '';
PRINT 'Iniciando actualización de rutas...';

-- Actualizar ruta base
UPDATE rutas 
SET ruta = '/api/tipodocumentos',
    descripcion = 'Acceso público a endpoints de tipos de documento'
WHERE ruta = '/api/tipos-documento';

IF @@ROWCOUNT > 0
    PRINT 'Ruta base /api/tipos-documento actualizada a /api/tipodocumentos';
ELSE
    PRINT 'No se encontró la ruta base /api/tipos-documento';

-- Eliminar rutas específicas que ya no se necesitan (la anotación solo verifica la ruta base)
DELETE FROM rutas 
WHERE ruta IN (
    '/api/tipos-documento/activos',
    '/api/tipos-documento/{id}',
    '/api/tipos-documento/codigo/{codigo}',
    '/api/tipos-documento/buscar',
    '/api/tipos-documento/buscar/activos',
    '/api/tipos-documento/estadisticas',
    '/api/tipos-documento/existe/codigo/{codigo}',
    '/api/tipos-documento/existe/nombre'
);

IF @@ROWCOUNT > 0
    PRINT CONCAT('Se eliminaron ', @@ROWCOUNT, ' rutas específicas innecesarias');
ELSE
    PRINT 'No se encontraron rutas específicas para eliminar';

PRINT '';
PRINT 'Actualización completada.';

-- Mostrar rutas después de la actualización
PRINT '';
PRINT 'Rutas después de la actualización:';
SELECT 
    id_ruta,
    ruta,
    descripcion,
    CASE 
        WHEN es_publica = 1 THEN 'Pública'
        ELSE 'Privada'
    END AS tipo_acceso,
    fecha_creacion
FROM rutas 
WHERE ruta LIKE '/api/tipodocumentos%'
ORDER BY ruta;

-- Verificar que solo existe la ruta base
PRINT '';
PRINT 'Verificación final:';
DECLARE @count INT;
SELECT @count = COUNT(*) FROM rutas WHERE ruta LIKE '/api/tipodocumentos%';

IF @count = 1
    PRINT 'ÉXITO: Solo existe la ruta base /api/tipodocumentos como se esperaba';
ELSE
    PRINT CONCAT('ADVERTENCIA: Se encontraron ', @count, ' rutas de tipodocumentos');

PRINT '';
PRINT 'Script completado exitosamente';
PRINT 'IMPORTANTE: Reinicie la aplicación para que los cambios tomen efecto';
