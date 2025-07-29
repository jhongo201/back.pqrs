#!/bin/bash

# Script de Despliegue para Producción - Sistema PQRS
# Autor: Sistema PQRS Team
# Versión: 1.0.0

set -e  # Salir si cualquier comando falla

echo "=========================================="
echo "  DESPLIEGUE PRODUCCIÓN - SISTEMA PQRS"
echo "=========================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables de configuración
APP_NAME="pqrs-backend"
WAR_NAME="pqrs-backend-0.0.1-SNAPSHOT.war"
TOMCAT_WEBAPPS="/opt/tomcat/webapps"
BACKUP_DIR="/opt/backups/pqrs"
SERVICE_NAME="tomcat"

# Función para logging
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[ERROR] $1${NC}"
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING] $1${NC}"
}

# Verificar que se ejecuta como root
if [[ $EUID -ne 0 ]]; then
   error "Este script debe ejecutarse como root"
fi

# Verificar que existe el archivo .env.production
if [ ! -f ".env.production" ]; then
    error "Archivo .env.production no encontrado. Copie .env.production.template y configure los valores."
fi

log "Cargando variables de entorno de producción..."
source .env.production

# Verificar variables críticas
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ] || [ -z "$MAIL_PASSWORD" ]; then
    error "Variables de entorno críticas no configuradas. Revise .env.production"
fi

# Crear directorio de backups si no existe
log "Creando directorio de backups..."
mkdir -p $BACKUP_DIR

# Backup de la aplicación actual (si existe)
if [ -f "$TOMCAT_WEBAPPS/$WAR_NAME" ]; then
    log "Creando backup de la aplicación actual..."
    cp "$TOMCAT_WEBAPPS/$WAR_NAME" "$BACKUP_DIR/${WAR_NAME}.backup.$(date +%Y%m%d_%H%M%S)"
fi

# Compilar la aplicación
log "Compilando aplicación para producción..."
export SPRING_PROFILES_ACTIVE=prod
mvn clean package -DskipTests -Pprod || error "Error en la compilación"

# Verificar que el WAR se generó correctamente
if [ ! -f "target/$WAR_NAME" ]; then
    error "Archivo WAR no encontrado en target/"
fi

# Detener Tomcat
log "Deteniendo servicio Tomcat..."
systemctl stop $SERVICE_NAME || warning "No se pudo detener Tomcat (puede que no esté ejecutándose)"

# Eliminar aplicación anterior
if [ -f "$TOMCAT_WEBAPPS/$WAR_NAME" ]; then
    log "Eliminando aplicación anterior..."
    rm -f "$TOMCAT_WEBAPPS/$WAR_NAME"
fi

# Eliminar directorio desplegado anterior
if [ -d "$TOMCAT_WEBAPPS/pqrs-backend-0.0.1-SNAPSHOT" ]; then
    log "Eliminando directorio desplegado anterior..."
    rm -rf "$TOMCAT_WEBAPPS/pqrs-backend-0.0.1-SNAPSHOT"
fi

# Copiar nuevo WAR
log "Desplegando nueva aplicación..."
cp "target/$WAR_NAME" "$TOMCAT_WEBAPPS/"

# Configurar permisos
log "Configurando permisos..."
chown tomcat:tomcat "$TOMCAT_WEBAPPS/$WAR_NAME"
chmod 644 "$TOMCAT_WEBAPPS/$WAR_NAME"

# Crear directorio de uploads si no existe
log "Configurando directorio de uploads..."
mkdir -p "$UPLOAD_DIR"
chown -R tomcat:tomcat "$UPLOAD_DIR"
chmod -R 755 "$UPLOAD_DIR"

# Configurar variables de entorno del sistema
log "Configurando variables de entorno del sistema..."
cat > /etc/systemd/system/tomcat.service.d/pqrs-env.conf << EOF
[Service]
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=$DB_URL"
Environment="DB_USERNAME=$DB_USERNAME"
Environment="DB_PASSWORD=$DB_PASSWORD"
Environment="JWT_SECRET=$JWT_SECRET"
Environment="JWT_EXPIRATION=$JWT_EXPIRATION"
Environment="UPLOAD_DIR=$UPLOAD_DIR"
Environment="MAIL_HOST=$MAIL_HOST"
Environment="MAIL_PORT=$MAIL_PORT"
Environment="MAIL_USERNAME=$MAIL_USERNAME"
Environment="MAIL_PASSWORD=$MAIL_PASSWORD"
Environment="LDAP_URL=$LDAP_URL"
Environment="LDAP_DOMAIN=$LDAP_DOMAIN"
Environment="LDAP_SEARCH_BASE=$LDAP_SEARCH_BASE"
Environment="LDAP_SERVICE_USER=$LDAP_SERVICE_USER"
Environment="LDAP_SERVICE_PASSWORD=$LDAP_SERVICE_PASSWORD"
Environment="FRONTEND_URL=$FRONTEND_URL"
Environment="CORS_ORIGINS=$CORS_ORIGINS"
Environment="SERVER_PORT=$SERVER_PORT"
Environment="CONTEXT_PATH=$CONTEXT_PATH"
EOF

# Recargar systemd
log "Recargando configuración de systemd..."
systemctl daemon-reload

# Iniciar Tomcat
log "Iniciando servicio Tomcat..."
systemctl start $SERVICE_NAME

# Habilitar Tomcat para inicio automático
log "Habilitando inicio automático de Tomcat..."
systemctl enable $SERVICE_NAME

# Esperar a que la aplicación se despliegue
log "Esperando despliegue de la aplicación..."
sleep 30

# Verificar que la aplicación está funcionando
log "Verificando estado de la aplicación..."
if curl -f -s "http://localhost:$SERVER_PORT$CONTEXT_PATH/actuator/health" > /dev/null; then
    log "✅ Aplicación desplegada exitosamente"
    log "🌐 URL: http://localhost:$SERVER_PORT$CONTEXT_PATH"
    log "📊 Health Check: http://localhost:$SERVER_PORT$CONTEXT_PATH/actuator/health"
    log "ℹ️  Info: http://localhost:$SERVER_PORT$CONTEXT_PATH/actuator/info"
else
    error "❌ La aplicación no responde. Revise los logs: journalctl -u $SERVICE_NAME -f"
fi

# Mostrar logs recientes
log "Últimos logs de la aplicación:"
journalctl -u $SERVICE_NAME --no-pager -n 20

echo "=========================================="
echo "  ✅ DESPLIEGUE COMPLETADO EXITOSAMENTE"
echo "=========================================="

log "Para monitorear la aplicación:"
log "  - Logs en tiempo real: journalctl -u $SERVICE_NAME -f"
log "  - Estado del servicio: systemctl status $SERVICE_NAME"
log "  - Reiniciar servicio: systemctl restart $SERVICE_NAME"
