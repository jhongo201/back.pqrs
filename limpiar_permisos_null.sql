-- Script para limpiar registros de permisos_rol con ruta null

-- 1. Mostrar registros con problemas
SELECT 'REGISTROS CON RUTA NULL:' as mensaje;
SELECT 
    pr.id_permiso,
    pr.id_rol,
    pr.id_ruta,
    rol.nombre as rol_nombre,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado
FROM permisos_rol pr
LEFT JOIN roles rol ON pr.id_rol = rol.id_rol
LEFT JOIN rutas r ON pr.id_ruta = r.id_ruta
WHERE pr.id_ruta IS NULL OR r.id_ruta IS NULL;

-- 2. Contar registros problemáticos
SELECT 'TOTAL DE REGISTROS CON PROBLEMAS:' as mensaje;
SELECT COUNT(*) as total_problematicos
FROM permisos_rol pr
LEFT JOIN rutas r ON pr.id_ruta = r.id_ruta
WHERE pr.id_ruta IS NULL OR r.id_ruta IS NULL;

-- 3. Eliminar registros con ruta null o ruta inexistente
DELETE FROM permisos_rol 
WHERE id_ruta IS NULL 
   OR id_ruta NOT IN (SELECT id_ruta FROM rutas);

PRINT 'Registros con ruta null o inexistente eliminados';

-- 4. Verificar resultado
SELECT 'VERIFICACIÓN FINAL:' as mensaje;
SELECT COUNT(*) as total_registros_restantes
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol;

-- 5. Mostrar registros válidos restantes
SELECT 'REGISTROS VÁLIDOS RESTANTES:' as mensaje;
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
ORDER BY pr.id_rol, r.ruta;
