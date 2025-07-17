package com.claude.springboot.app.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base64FileUtils {
    
    private static final Pattern DATA_URL_PATTERN = Pattern.compile("data:([^;]+);base64,(.+)");
    
    /**
     * Convierte una cadena base64 a MultipartFile
     * @param base64String Cadena base64 (puede incluir data URL prefix)
     * @param defaultFilename Nombre por defecto si no se puede determinar
     * @return MultipartFile o null si la cadena es inválida
     */
    public static MultipartFile base64ToMultipartFile(String base64String, String defaultFilename) {
        if (base64String == null || base64String.trim().isEmpty()) {
            return null;
        }
        
        try {
            String contentType = "application/octet-stream";
            String filename = defaultFilename;
            String base64Data = base64String.trim();
            
            // Verificar si es un data URL (data:mime/type;base64,data)
            Matcher matcher = DATA_URL_PATTERN.matcher(base64Data);
            if (matcher.matches()) {
                contentType = matcher.group(1);
                base64Data = matcher.group(2);
                
                // Determinar extensión basada en content type
                filename = getFilenameFromContentType(contentType);
            } else {
                // Si no es data URL, intentar detectar por magic bytes
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
                    String detectedType = detectFileTypeByMagicBytes(decodedBytes);
                    filename = getFilenameFromContentType(detectedType);
                    contentType = detectedType;
                } catch (Exception e) {
                    filename = "archivo_adjunto.txt"; // Por defecto si falla
                }
            }
            
            // Decodificar base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            
            // Crear implementación personalizada de MultipartFile
            return new Base64MultipartFile(
                "archivo",
                filename,
                contentType,
                decodedBytes
            );
            
        } catch (Exception e) {
            // Si hay error en la decodificación, retornar null
            return null;
        }
    }
    
    /**
     * Verifica si una cadena es un base64 válido
     */
    public static boolean isValidBase64(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) {
            return false;
        }
        
        try {
            String base64Data = base64String.trim();
            
            // Si es data URL, extraer solo la parte base64
            Matcher matcher = DATA_URL_PATTERN.matcher(base64Data);
            if (matcher.matches()) {
                base64Data = matcher.group(2);
            }
            
            Base64.getDecoder().decode(base64Data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Detecta el tipo de archivo basándose en los magic bytes (primeros bytes del archivo)
     */
    private static String detectFileTypeByMagicBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "text/plain";
        }
        
        // PDF
        if (bytes.length >= 4 && 
            bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) {
            return "application/pdf";
        }
        
        // PNG
        if (bytes.length >= 8 && 
            bytes[0] == (byte)0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47 &&
            bytes[4] == 0x0D && bytes[5] == 0x0A && bytes[6] == 0x1A && bytes[7] == 0x0A) {
            return "image/png";
        }
        
        // JPEG
        if (bytes.length >= 3 && 
            bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8 && bytes[2] == (byte)0xFF) {
            return "image/jpeg";
        }
        
        // GIF
        if (bytes.length >= 6 && 
            bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x38 &&
            (bytes[4] == 0x37 || bytes[4] == 0x39) && bytes[5] == 0x61) {
            return "image/gif";
        }
        
        // ZIP (también para docx, xlsx, pptx)
        if (bytes.length >= 4 && 
            bytes[0] == 0x50 && bytes[1] == 0x4B && bytes[2] == 0x03 && bytes[3] == 0x04) {
            
            // Verificar si es un documento de Office moderno
            String content = new String(bytes, 0, Math.min(bytes.length, 1000));
            if (content.contains("word/")) {
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (content.contains("xl/")) {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (content.contains("ppt/")) {
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            }
            return "application/zip";
        }
        
        // DOC (Word 97-2003)
        if (bytes.length >= 8 && 
            bytes[0] == (byte)0xD0 && bytes[1] == (byte)0xCF && bytes[2] == 0x11 && bytes[3] == (byte)0xE0 &&
            bytes[4] == (byte)0xA1 && bytes[5] == (byte)0xB1 && bytes[6] == 0x1A && bytes[7] == (byte)0xE1) {
            return "application/msword";
        }
        
        // BMP
        if (bytes.length >= 2 && bytes[0] == 0x42 && bytes[1] == 0x4D) {
            return "image/bmp";
        }
        
        // TIFF
        if (bytes.length >= 4 && 
            ((bytes[0] == 0x49 && bytes[1] == 0x49 && bytes[2] == 0x2A && bytes[3] == 0x00) ||
             (bytes[0] == 0x4D && bytes[1] == 0x4D && bytes[2] == 0x00 && bytes[3] == 0x2A))) {
            return "image/tiff";
        }
        
        // RAR
        if (bytes.length >= 7 && 
            bytes[0] == 0x52 && bytes[1] == 0x61 && bytes[2] == 0x72 && bytes[3] == 0x21 &&
            bytes[4] == 0x1A && bytes[5] == 0x07 && bytes[6] == 0x00) {
            return "application/x-rar-compressed";
        }
        
        // Verificar si es texto (caracteres imprimibles)
        boolean isText = true;
        int textBytes = Math.min(bytes.length, 512); // Verificar primeros 512 bytes
        for (int i = 0; i < textBytes; i++) {
            byte b = bytes[i];
            if (b < 0x09 || (b > 0x0D && b < 0x20) || b == 0x7F) {
                if (b != 0x0A && b != 0x0D && b != 0x09) { // Permitir LF, CR, TAB
                    isText = false;
                    break;
                }
            }
        }
        
        if (isText) {
            return "text/plain";
        }
        
        // Por defecto, binario
        return "application/octet-stream";
    }
    
    /**
     * Determina el nombre de archivo con extensión basado en el content type
     */
    private static String getFilenameFromContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return "archivo_adjunto.bin";
        }
        
        // Normalizar content type
        contentType = contentType.toLowerCase();
        
        // Imágenes
        if (contentType.startsWith("image/")) {
            String extension = contentType.substring(6);
            // Mapear algunos tipos comunes
            switch (extension) {
                case "jpeg": return "archivo_adjunto.jpg";
                case "png": return "archivo_adjunto.png";
                case "gif": return "archivo_adjunto.gif";
                case "bmp": return "archivo_adjunto.bmp";
                case "webp": return "archivo_adjunto.webp";
                case "svg+xml": return "archivo_adjunto.svg";
                default: return "archivo_adjunto." + extension;
            }
        }
        
        // Documentos PDF
        if (contentType.equals("application/pdf")) {
            return "archivo_adjunto.pdf";
        }
        
        // Documentos de Office modernos (OpenXML)
        if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return "archivo_adjunto.docx";
        }
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return "archivo_adjunto.xlsx";
        }
        if (contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
            return "archivo_adjunto.pptx";
        }
        
        // Documentos de Office clásicos
        if (contentType.equals("application/msword")) {
            return "archivo_adjunto.doc";
        }
        if (contentType.equals("application/vnd.ms-excel")) {
            return "archivo_adjunto.xls";
        }
        if (contentType.equals("application/vnd.ms-powerpoint")) {
            return "archivo_adjunto.ppt";
        }
        
        // Documentos de Office (detección genérica)
        if (contentType.contains("word") || contentType.contains("document")) {
            if (contentType.contains("officedocument")) {
                return "archivo_adjunto.docx";
            } else {
                return "archivo_adjunto.doc";
            }
        }
        
        if (contentType.contains("excel") || contentType.contains("spreadsheet")) {
            if (contentType.contains("officedocument")) {
                return "archivo_adjunto.xlsx";
            } else {
                return "archivo_adjunto.xls";
            }
        }
        
        if (contentType.contains("presentation")) {
            if (contentType.contains("officedocument")) {
                return "archivo_adjunto.pptx";
            } else {
                return "archivo_adjunto.ppt";
            }
        }
        
        // Archivos de texto
        if (contentType.startsWith("text/")) {
            String subtype = contentType.substring(5);
            switch (subtype) {
                case "plain": return "archivo_adjunto.txt";
                case "html": return "archivo_adjunto.html";
                case "css": return "archivo_adjunto.css";
                case "javascript": return "archivo_adjunto.js";
                case "csv": return "archivo_adjunto.csv";
                default: return "archivo_adjunto.txt";
            }
        }
        
        // Archivos comprimidos
        if (contentType.equals("application/zip")) {
            return "archivo_adjunto.zip";
        }
        if (contentType.equals("application/x-rar-compressed")) {
            return "archivo_adjunto.rar";
        }
        
        // JSON y XML
        if (contentType.equals("application/json")) {
            return "archivo_adjunto.json";
        }
        if (contentType.equals("application/xml") || contentType.equals("text/xml")) {
            return "archivo_adjunto.xml";
        }
        
        // Por defecto
        return "archivo_adjunto.bin";
    }
    
    /**
     * Implementación personalizada de MultipartFile para archivos base64
     */
    private static class Base64MultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        
        public Base64MultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        @Override
        public String getContentType() {
            return contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }
        
        @Override
        public long getSize() {
            return content != null ? content.length : 0;
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("transferTo not supported for Base64MultipartFile");
        }
    }
}
