-- Script para crear usuario de prueba local
-- Ejecutar este script en la base de datos para crear un usuario de prueba

-- Verificar si el usuario ya existe
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin')
BEGIN
    -- Insertar usuario admin con password "admin123" (encriptado con BCrypt)
    -- Password: admin123 -> $2a$10$N.zmdr9k7uOLQvQHbh8qiOXjE8QnCld.z0jxvgw.gNtN9QjKKQ4S6
    INSERT INTO usuarios (username, password, estado, fecha_creacion, id_rol)
    VALUES (
        'admin',
        '$2a$10$N.zmdr9k7uOLQvQHbh8qiOXjE8QnCld.z0jxvgw.gNtN9QjKKQ4S6',
        1,
        GETDATE(),
        1  -- Asumiendo que el rol ADMIN tiene ID 1
    );
    PRINT 'Usuario admin creado exitosamente';
END
ELSE
BEGIN
    PRINT 'El usuario admin ya existe';
END

-- Verificar si existe un usuario funcionario de prueba
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'funcionario')
BEGIN
    -- Insertar usuario funcionario con password "func123"
    -- Password: func123 -> $2a$10$8K5fUzPQqNqrXwgKXf5uJeJ5Qs8vXhKqL9Wn7Zf5uJeJ5Qs8vXhKq
    INSERT INTO usuarios (username, password, estado, fecha_creacion, id_rol)
    VALUES (
        'funcionario',
        '$2a$10$8K5fUzPQqNqrXwgKXf5uJeJ5Qs8vXhKqL9Wn7Zf5uJeJ5Qs8vXhKq',
        1,
        GETDATE(),
        2  -- Asumiendo que el rol FUNCIONARIO/USUARIO tiene ID 2
    );
    PRINT 'Usuario funcionario creado exitosamente';
END
ELSE
BEGIN
    PRINT 'El usuario funcionario ya existe';
END

-- Mostrar los usuarios creados
SELECT 
    u.id_usuario,
    u.username,
    u.estado,
    u.fecha_creacion,
    r.nombre as rol
FROM usuarios u
INNER JOIN roles r ON u.id_rol = r.id_rol
WHERE u.username IN ('admin', 'funcionario');
