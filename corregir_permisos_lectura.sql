-- Script para corregir permisos de lectura existentes
-- Los permisos existen pero tienen puede_leer = 0

-- 1. Mostrar estado actual
SELECT 'ESTADO ACTUAL DE PERMISOS:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_rol,
    pr.id_ruta,
    r.ruta,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND r.ruta LIKE '%permisorols%';

-- 2. Actualizar permisos para habilitar lectura
UPDATE permisos_rol 
SET puede_leer = 1, estado = 1
WHERE id_rol = 2 
  AND id_ruta IN (
    SELECT id_ruta 
    FROM rutas 
    WHERE ruta LIKE '%permisorols%'
  );

PRINT 'Permisos de lectura actualizados para el rol USUARIO en rutas permisorols';

-- 3. Verificar cambios
SELECT 'ESTADO DESPUÉS DE LA ACTUALIZACIÓN:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_rol,
    pr.id_ruta,
    r.ruta,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND r.ruta LIKE '%permisorols%';

-- 4. Verificación final
SELECT 'VERIFICACIÓN FINAL:' as mensaje;
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 
            FROM permisos_rol pr
            JOIN rutas r ON pr.id_ruta = r.id_ruta
            JOIN roles rol ON pr.id_rol = rol.id_rol
            WHERE rol.nombre = 'USUARIO' 
              AND r.ruta = '/api/permisorols'
              AND pr.puede_leer = 1
              AND pr.estado = 1
        ) THEN 'PERMISO DE LECTURA ACTIVO - OK'
        ELSE 'PROBLEMA: PERMISO AÚN NO ESTÁ ACTIVO'
    END as resultado;
