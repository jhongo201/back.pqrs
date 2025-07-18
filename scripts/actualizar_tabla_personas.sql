-- Script para actualizar la tabla personas
-- Agregar nuevas columnas sin afectar registros existentes
-- Fecha: 2025-07-18

USE [roles];
GO

-- Verificar si las columnas ya existen antes de agregarlas
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'personas' AND COLUMN_NAME = 'primer_nombre')
BEGIN
    ALTER TABLE [dbo].[personas] ADD [primer_nombre] NVARCHAR(100) NULL;
    PRINT 'Columna primer_nombre agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna primer_nombre ya existe';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'personas' AND COLUMN_NAME = 'otros_nombres')
BEGIN
    ALTER TABLE [dbo].[personas] ADD [otros_nombres] NVARCHAR(200) NULL;
    PRINT 'Columna otros_nombres agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna otros_nombres ya existe';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'personas' AND COLUMN_NAME = 'primer_apellido')
BEGIN
    ALTER TABLE [dbo].[personas] ADD [primer_apellido] NVARCHAR(100) NULL;
    PRINT 'Columna primer_apellido agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna primer_apellido ya existe';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'personas' AND COLUMN_NAME = 'segundo_apellido')
BEGIN
    ALTER TABLE [dbo].[personas] ADD [segundo_apellido] NVARCHAR(100) NULL;
    PRINT 'Columna segundo_apellido agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna segundo_apellido ya existe';
END

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'personas' AND COLUMN_NAME = 'id_municipio')
BEGIN
    ALTER TABLE [dbo].[personas] ADD [id_municipio] VARCHAR(5) NULL;
    PRINT 'Columna id_municipio agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La columna id_municipio ya existe';
END

-- Agregar la clave foránea para municipio si no existe
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_personas_municipio')
BEGIN
    ALTER TABLE [dbo].[personas] 
    ADD CONSTRAINT [FK_personas_municipio] 
    FOREIGN KEY ([id_municipio]) REFERENCES [dbo].[municipios]([codigo_dane]);
    PRINT 'Clave foránea FK_personas_municipio agregada exitosamente';
END
ELSE
BEGIN
    PRINT 'La clave foránea FK_personas_municipio ya existe';
END

-- Script opcional para migrar datos existentes
-- Descomenta las siguientes líneas si quieres separar automáticamente los nombres y apellidos existentes
/*
-- Migrar nombres existentes (solo si las nuevas columnas están vacías)
UPDATE [dbo].[personas] 
SET 
    [primer_nombre] = CASE 
        WHEN CHARINDEX(' ', LTRIM(RTRIM([nombres]))) > 0 
        THEN LEFT(LTRIM(RTRIM([nombres])), CHARINDEX(' ', LTRIM(RTRIM([nombres]))) - 1)
        ELSE LTRIM(RTRIM([nombres]))
    END,
    [otros_nombres] = CASE 
        WHEN CHARINDEX(' ', LTRIM(RTRIM([nombres]))) > 0 
        THEN LTRIM(RTRIM(SUBSTRING([nombres], CHARINDEX(' ', LTRIM(RTRIM([nombres]))) + 1, LEN([nombres]))))
        ELSE NULL
    END
WHERE [primer_nombre] IS NULL AND [nombres] IS NOT NULL;

-- Migrar apellidos existentes (solo si las nuevas columnas están vacías)
UPDATE [dbo].[personas] 
SET 
    [primer_apellido] = CASE 
        WHEN CHARINDEX(' ', LTRIM(RTRIM([apellidos]))) > 0 
        THEN LEFT(LTRIM(RTRIM([apellidos])), CHARINDEX(' ', LTRIM(RTRIM([apellidos]))) - 1)
        ELSE LTRIM(RTRIM([apellidos]))
    END,
    [segundo_apellido] = CASE 
        WHEN CHARINDEX(' ', LTRIM(RTRIM([apellidos]))) > 0 
        THEN LTRIM(RTRIM(SUBSTRING([apellidos], CHARINDEX(' ', LTRIM(RTRIM([apellidos]))) + 1, LEN([apellidos]))))
        ELSE NULL
    END
WHERE [primer_apellido] IS NULL AND [apellidos] IS NOT NULL;

PRINT 'Migración de datos existentes completada';
*/

-- Verificar la estructura actualizada
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'personas'
ORDER BY ORDINAL_POSITION;

PRINT 'Script de actualización de tabla personas completado exitosamente';
