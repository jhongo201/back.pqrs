-- Script para eliminar rutas duplicadas /api/permisorols
-- Mantener solo una ruta y consolidar permisos

-- 1. Mostrar rutas duplicadas
SELECT 'RUTAS DUPLICADAS ENCONTRADAS:' as mensaje;
SELECT id_ruta, ruta, descripcion, estado, fecha_creacion
FROM rutas 
WHERE ruta = '/api/permisorols'
ORDER BY id_ruta;

-- 2. Mostrar permisos asociados a cada ruta duplicada
SELECT 'PERMISOS ASOCIADOS A CADA RUTA:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_ruta,
    r.ruta,
    pr.id_rol,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/api/permisorols'
ORDER BY pr.id_ruta, pr.id_rol;

-- 3. Identificar cuál ruta mantener (la de menor ID)
DECLARE @ruta_mantener INT;
DECLARE @ruta_eliminar INT;

SELECT @ruta_mantener = MIN(id_ruta) FROM rutas WHERE ruta = '/api/permisorols';
SELECT @ruta_eliminar = MAX(id_ruta) FROM rutas WHERE ruta = '/api/permisorols';

PRINT 'Ruta a mantener (ID menor): ' + CAST(@ruta_mantener AS VARCHAR);
PRINT 'Ruta a eliminar (ID mayor): ' + CAST(@ruta_eliminar AS VARCHAR);

-- 4. Mover permisos de la ruta duplicada a la ruta principal (si no existen ya)
-- Primero verificar si hay conflictos
SELECT 'VERIFICANDO CONFLICTOS DE PERMISOS:' as mensaje;
SELECT 
    'Conflicto: Rol ' + rol.nombre + ' tiene permisos en ambas rutas' as conflicto
FROM permisos_rol pr1
JOIN permisos_rol pr2 ON pr1.id_rol = pr2.id_rol
JOIN roles rol ON pr1.id_rol = rol.id_rol
WHERE pr1.id_ruta = @ruta_mantener 
  AND pr2.id_ruta = @ruta_eliminar;

-- 5. Actualizar permisos de la ruta duplicada hacia la ruta principal
-- Solo si no hay conflictos (mismo rol en ambas rutas)
UPDATE permisos_rol 
SET id_ruta = @ruta_mantener
WHERE id_ruta = @ruta_eliminar
  AND id_rol NOT IN (
    SELECT id_rol 
    FROM permisos_rol 
    WHERE id_ruta = @ruta_mantener
  );

PRINT 'Permisos movidos de ruta duplicada a ruta principal';

-- 6. Eliminar permisos duplicados que no se pudieron mover
DELETE FROM permisos_rol 
WHERE id_ruta = @ruta_eliminar;

PRINT 'Permisos duplicados eliminados';

-- 7. Eliminar la ruta duplicada
DELETE FROM rutas 
WHERE id_ruta = @ruta_eliminar;

PRINT 'Ruta duplicada eliminada';

-- 8. Verificar resultado final
SELECT 'RESULTADO FINAL:' as mensaje;
SELECT id_ruta, ruta, descripcion, estado
FROM rutas 
WHERE ruta = '/api/permisorols';

SELECT 'PERMISOS FINALES:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_ruta,
    r.ruta,
    pr.id_rol,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE r.ruta = '/api/permisorols'
ORDER BY pr.id_rol;
