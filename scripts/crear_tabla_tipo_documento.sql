-- =============================================
-- Script: Crear tabla tipo_documento
-- Descripción: Tabla para almacenar tipos de documentos de identidad
-- Autor: Sistema PQRS
-- Fecha: 2025-07-18
-- =============================================

USE [pqrs_db];
GO

-- Verificar si la tabla ya existe
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tipo_documento' AND xtype='U')
BEGIN
    PRINT 'Creando tabla tipo_documento...';
    
    CREATE TABLE tipo_documento (
        id INT IDENTITY(1,1) NOT NULL,
        codigo VARCHAR(10) NOT NULL,
        nombre VARCHAR(100) NOT NULL,
        descripcion VARCHAR(255) NULL,
        estado BIT NOT NULL DEFAULT 1,
        fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
        fecha_actualizacion DATETIME NULL,
        
        CONSTRAINT PK_tipo_documento PRIMARY KEY (id),
        CONSTRAINT UK_tipo_documento_codigo UNIQUE (codigo),
        CONSTRAINT UK_tipo_documento_nombre UNIQUE (nombre)
    );
    
    PRINT 'Tabla tipo_documento creada exitosamente.';
END
ELSE
BEGIN
    PRINT 'La tabla tipo_documento ya existe.';
END

GO

-- =============================================
-- INSERTAR DATOS INICIALES
-- =============================================

PRINT 'Insertando tipos de documento iniciales...';

-- Cédula de Ciudadanía
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'CC')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('CC', 'Cédula de Ciudadanía', 'Documento de identidad para ciudadanos colombianos mayores de edad', 1, GETDATE());
    PRINT '✓ Cédula de Ciudadanía insertada';
END
ELSE
BEGIN
    PRINT '- Cédula de Ciudadanía ya existe';
END

-- Tarjeta de Identidad
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'TI')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('TI', 'Tarjeta de Identidad', 'Documento de identidad para menores de edad entre 7 y 17 años', 1, GETDATE());
    PRINT '✓ Tarjeta de Identidad insertada';
END
ELSE
BEGIN
    PRINT '- Tarjeta de Identidad ya existe';
END

-- Registro Civil
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'RC')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('RC', 'Registro Civil', 'Documento de identidad para menores de 7 años', 1, GETDATE());
    PRINT '✓ Registro Civil insertado';
END
ELSE
BEGIN
    PRINT '- Registro Civil ya existe';
END

-- Cédula de Extranjería
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'CE')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('CE', 'Cédula de Extranjería', 'Documento de identidad para extranjeros residentes en Colombia', 1, GETDATE());
    PRINT '✓ Cédula de Extranjería insertada';
END
ELSE
BEGIN
    PRINT '- Cédula de Extranjería ya existe';
END

-- Pasaporte
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'PA')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('PA', 'Pasaporte', 'Documento de identidad internacional', 1, GETDATE());
    PRINT '✓ Pasaporte insertado';
END
ELSE
BEGIN
    PRINT '- Pasaporte ya existe';
END

-- NIT (Número de Identificación Tributaria)
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'NIT')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('NIT', 'NIT', 'Número de Identificación Tributaria para personas jurídicas', 1, GETDATE());
    PRINT '✓ NIT insertado';
END
ELSE
BEGIN
    PRINT '- NIT ya existe';
END

-- Permiso Especial de Permanencia
IF NOT EXISTS (SELECT 1 FROM tipo_documento WHERE codigo = 'PEP')
BEGIN
    INSERT INTO tipo_documento (codigo, nombre, descripcion, estado, fecha_creacion)
    VALUES ('PEP', 'Permiso Especial de Permanencia', 'Documento para ciudadanos venezolanos en Colombia', 1, GETDATE());
    PRINT '✓ Permiso Especial de Permanencia insertado';
END
ELSE
BEGIN
    PRINT '- Permiso Especial de Permanencia ya existe';
END

GO

-- =============================================
-- VERIFICAR DATOS INSERTADOS
-- =============================================

PRINT '';
PRINT '=== RESUMEN DE TIPOS DE DOCUMENTO ===';
SELECT 
    id,
    codigo,
    nombre,
    descripcion,
    CASE WHEN estado = 1 THEN 'Activo' ELSE 'Inactivo' END AS estado,
    fecha_creacion
FROM tipo_documento
ORDER BY codigo;

PRINT '';
PRINT '=== SCRIPT COMPLETADO ===';
PRINT 'Tabla tipo_documento creada exitosamente con 7 tipos de documento iniciales.';
PRINT 'Tipos disponibles: CC, TI, RC, CE, PA, NIT, PEP';
PRINT '';
