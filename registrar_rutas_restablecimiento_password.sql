-- Script para registrar rutas de restablecimiento de contraseña (SQL Server)
-- Ejecutar este script en la base de datos

-- 1. Configurar ID del módulo de Usuarios
DECLARE @modulo_usuarios_id INT = 1;
PRINT 'Usando módulo Usuarios con ID: ' + CAST(@modulo_usuarios_id AS VARCHAR(10));

-- Verificar que el módulo existe
IF NOT EXISTS (SELECT 1 FROM modulos WHERE id_modulo = @modulo_usuarios_id)
BEGIN
    PRINT 'ERROR: No existe el módulo con ID ' + CAST(@modulo_usuarios_id AS VARCHAR(10));
    RETURN;
END

-- 2. Registrar rutas de restablecimiento de contraseña
PRINT 'Registrando rutas de restablecimiento de contraseña...';

-- Ruta: POST /api/usuarios/solicitar-restablecimiento - Solicitar restablecimiento
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/usuarios/solicitar-restablecimiento')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_usuarios_id, '/api/usuarios/solicitar-restablecimiento', 'Solicitar restablecimiento de contraseña', 1, GETDATE(), 1);
    PRINT 'Ruta /api/usuarios/solicitar-restablecimiento creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1, descripcion = 'Solicitar restablecimiento de contraseña' 
    WHERE ruta = '/api/usuarios/solicitar-restablecimiento';
    PRINT 'Ruta /api/usuarios/solicitar-restablecimiento actualizada';
END

-- Ruta: POST /api/usuarios/restablecer-password - Restablecer contraseña
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/usuarios/restablecer-password')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_usuarios_id, '/api/usuarios/restablecer-password', 'Restablecer contraseña con token', 1, GETDATE(), 1);
    PRINT 'Ruta /api/usuarios/restablecer-password creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1, descripcion = 'Restablecer contraseña con token' 
    WHERE ruta = '/api/usuarios/restablecer-password';
    PRINT 'Ruta /api/usuarios/restablecer-password actualizada';
END

-- Ruta: GET /api/usuarios/validar-token-restablecimiento/** - Validar token
IF NOT EXISTS (SELECT 1 FROM rutas WHERE ruta = '/api/usuarios/validar-token-restablecimiento/**')
BEGIN
    INSERT INTO rutas (id_modulo, ruta, descripcion, estado, fecha_creacion, es_publica)
    VALUES (@modulo_usuarios_id, '/api/usuarios/validar-token-restablecimiento/**', 'Validar token de restablecimiento', 1, GETDATE(), 1);
    PRINT 'Ruta /api/usuarios/validar-token-restablecimiento/** creada exitosamente';
END
ELSE
BEGIN
    UPDATE rutas SET es_publica = 1, descripcion = 'Validar token de restablecimiento' 
    WHERE ruta = '/api/usuarios/validar-token-restablecimiento/**';
    PRINT 'Ruta /api/usuarios/validar-token-restablecimiento/** actualizada';
END

-- 3. Verificar rutas creadas
PRINT 'Verificando rutas creadas...';
SELECT 
    r.id_ruta,
    m.nombre as modulo,
    r.ruta,
    r.descripcion,
    r.es_publica,
    r.estado,
    r.fecha_creacion
FROM rutas r
INNER JOIN modulos m ON r.id_modulo = m.id_modulo
WHERE r.ruta IN (
    '/api/usuarios/solicitar-restablecimiento',
    '/api/usuarios/restablecer-password',
    '/api/usuarios/validar-token-restablecimiento/**'
)
ORDER BY r.ruta;

-- 4. Crear tabla de tokens de restablecimiento si no existe
PRINT 'Verificando tabla tokens_restablecimiento...';

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'tokens_restablecimiento')
BEGIN
    PRINT 'Creando tabla tokens_restablecimiento...';
    
    CREATE TABLE tokens_restablecimiento (
        id_token INT IDENTITY(1,1) PRIMARY KEY,
        token NVARCHAR(255) NOT NULL UNIQUE,
        codigo_restablecimiento NVARCHAR(10) NOT NULL,
        id_usuario INT NOT NULL,
        fecha_creacion DATETIME2 NOT NULL DEFAULT GETDATE(),
        fecha_expiracion DATETIME2 NOT NULL,
        fecha_uso DATETIME2 NULL,
        estado BIT NOT NULL DEFAULT 1,
        usado BIT NOT NULL DEFAULT 0,
        
        CONSTRAINT FK_tokens_restablecimiento_usuario 
            FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
    );
    
    -- Crear índices para optimizar consultas
    CREATE INDEX IX_tokens_restablecimiento_token ON tokens_restablecimiento(token);
    CREATE INDEX IX_tokens_restablecimiento_codigo ON tokens_restablecimiento(codigo_restablecimiento);
    CREATE INDEX IX_tokens_restablecimiento_usuario ON tokens_restablecimiento(id_usuario);
    CREATE INDEX IX_tokens_restablecimiento_expiracion ON tokens_restablecimiento(fecha_expiracion);
    
    PRINT 'Tabla tokens_restablecimiento creada exitosamente';
END
ELSE
BEGIN
    PRINT 'Tabla tokens_restablecimiento ya existe';
END

-- 5. Resumen final
PRINT '=== RESUMEN DE EJECUCIÓN ===';
PRINT 'Rutas de restablecimiento de contraseña registradas correctamente';
PRINT 'Todas las rutas están marcadas como públicas (es_publica = 1)';
PRINT 'Tabla tokens_restablecimiento verificada/creada';
PRINT 'Script ejecutado exitosamente';

-- Mostrar estadísticas finales
SELECT 
    COUNT(*) as total_rutas_restablecimiento
FROM rutas 
WHERE ruta LIKE '%restablec%' OR ruta LIKE '%password%';
