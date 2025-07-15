-- Script para verificar permisos existentes
-- Ejecutar para confirmar que los datos están correctos

-- 1. Verificar rutas que contienen 'permisorols'
SELECT 'RUTAS CON permisorols:' as seccion;
SELECT id_ruta, ruta, descripcion, estado 
FROM rutas 
WHERE ruta LIKE '%permisorols%';

-- 2. Verificar rol USUARIO
SELECT 'ROL USUARIO:' as seccion;
SELECT id_rol, nombre, descripcion, estado 
FROM roles 
WHERE nombre = 'USUARIO';

-- 3. Verificar permisos del rol USUARIO para rutas con 'permisorols'
SELECT 'PERMISOS DEL ROL USUARIO PARA RUTAS permisorols:' as seccion;
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
WHERE rol.nombre = 'USUARIO' 
  AND r.ruta LIKE '%permisorols%';

-- 4. Verificar si existe el permiso específico que necesitamos
SELECT 'VERIFICACIÓN ESPECÍFICA - USUARIO + /api/permisorols + LECTURA:' as seccion;
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
        ) THEN 'PERMISO EXISTE Y ESTÁ ACTIVO'
        ELSE 'PERMISO NO EXISTE O NO ESTÁ ACTIVO'
    END as resultado;
