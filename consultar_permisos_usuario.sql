-- Script para consultar todas las rutas y permisos del rol USUARIO

-- 1. Información general del rol USUARIO
SELECT 'INFORMACIÓN DEL ROL USUARIO:' as seccion;
SELECT 
    id_rol,
    nombre,
    descripcion,
    estado,
    fecha_creacion
FROM roles 
WHERE nombre = 'USUARIO';

-- 2. Todas las rutas con permisos para el rol USUARIO (activos)
SELECT 'RUTAS CON PERMISOS ACTIVOS PARA ROL USUARIO:' as seccion;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion as descripcion_ruta,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado as permiso_activo,
    CASE 
        WHEN pr.puede_leer = 1 THEN 'LECTURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_escribir = 1 THEN 'ESCRITURA '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_actualizar = 1 THEN 'ACTUALIZAR '
        ELSE ''
    END +
    CASE 
        WHEN pr.puede_eliminar = 1 THEN 'ELIMINAR '
        ELSE ''
    END as permisos_otorgados
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND pr.estado = 1
  AND r.estado = 1
ORDER BY r.ruta;

-- 3. Solo rutas con permiso de LECTURA
SELECT 'RUTAS CON PERMISO DE LECTURA:' as seccion;
SELECT 
    r.ruta,
    r.descripcion
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND pr.puede_leer = 1
  AND pr.estado = 1
  AND r.estado = 1
ORDER BY r.ruta;

-- 4. Solo rutas con permiso de ESCRITURA
SELECT 'RUTAS CON PERMISO DE ESCRITURA:' as seccion;
SELECT 
    r.ruta,
    r.descripcion
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND pr.puede_escribir = 1
  AND pr.estado = 1
  AND r.estado = 1
ORDER BY r.ruta;

-- 5. Rutas con permisos completos (CRUD)
SELECT 'RUTAS CON PERMISOS COMPLETOS (CRUD):' as seccion;
SELECT 
    r.ruta,
    r.descripcion
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND pr.puede_leer = 1
  AND pr.puede_escribir = 1
  AND pr.puede_actualizar = 1
  AND pr.puede_eliminar = 1
  AND pr.estado = 1
  AND r.estado = 1
ORDER BY r.ruta;

-- 6. Permisos deshabilitados o inactivos
SELECT 'PERMISOS DESHABILITADOS PARA ROL USUARIO:' as seccion;
SELECT 
    r.ruta,
    r.descripcion,
    pr.puede_leer,
    pr.puede_escribir,
    pr.puede_actualizar,
    pr.puede_eliminar,
    pr.estado as permiso_activo,
    r.estado as ruta_activa
FROM permisos_rol pr
JOIN rutas r ON pr.id_ruta = r.id_ruta
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO' 
  AND (pr.estado = 0 OR r.estado = 0)
ORDER BY r.ruta;

-- 7. Todas las rutas existentes vs permisos del USUARIO
SELECT 'TODAS LAS RUTAS VS PERMISOS DEL USUARIO:' as seccion;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion,
    CASE 
        WHEN pr.id_permiso IS NOT NULL THEN 'SÍ'
        ELSE 'NO'
    END as tiene_permiso,
    COALESCE(pr.puede_leer, 0) as puede_leer,
    COALESCE(pr.puede_escribir, 0) as puede_escribir,
    COALESCE(pr.puede_actualizar, 0) as puede_actualizar,
    COALESCE(pr.puede_eliminar, 0) as puede_eliminar,
    COALESCE(pr.estado, 0) as permiso_activo
FROM rutas r
LEFT JOIN permisos_rol pr ON r.id_ruta = pr.id_ruta 
    AND pr.id_rol = (SELECT id_rol FROM roles WHERE nombre = 'USUARIO')
WHERE r.estado = 1
ORDER BY r.ruta;

-- 8. Resumen estadístico
SELECT 'RESUMEN ESTADÍSTICO:' as seccion;
SELECT 
    COUNT(*) as total_rutas_con_permiso,
    SUM(CASE WHEN pr.puede_leer = 1 THEN 1 ELSE 0 END) as total_lectura,
    SUM(CASE WHEN pr.puede_escribir = 1 THEN 1 ELSE 0 END) as total_escritura,
    SUM(CASE WHEN pr.puede_actualizar = 1 THEN 1 ELSE 0 END) as total_actualizar,
    SUM(CASE WHEN pr.puede_eliminar = 1 THEN 1 ELSE 0 END) as total_eliminar,
    SUM(CASE WHEN pr.estado = 1 THEN 1 ELSE 0 END) as total_activos,
    SUM(CASE WHEN pr.estado = 0 THEN 1 ELSE 0 END) as total_inactivos
FROM permisos_rol pr
JOIN roles rol ON pr.id_rol = rol.id_rol
WHERE rol.nombre = 'USUARIO';

-- 9. Rutas que podrían necesitar permisos (rutas sin permisos asignados)
SELECT 'RUTAS SIN PERMISOS ASIGNADOS AL USUARIO:' as seccion;
SELECT 
    r.id_ruta,
    r.ruta,
    r.descripcion
FROM rutas r
WHERE r.estado = 1
  AND r.id_ruta NOT IN (
    SELECT pr.id_ruta 
    FROM permisos_rol pr
    JOIN roles rol ON pr.id_rol = rol.id_rol
    WHERE rol.nombre = 'USUARIO'
  )
ORDER BY r.ruta;
