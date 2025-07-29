# Guía de Despliegue WAR - Sistema PQRS

## 📋 Información General

- **Aplicación:** Sistema de Soporte Técnico (PQRS)
- **Versión:** 1.0.0
- **Tipo de Empaquetado:** WAR
- **Java:** 17
- **Spring Boot:** 3.4.1
- **Archivo WAR:** `pqrs-backend-0.0.1-SNAPSHOT.war`

## 🔧 Prerrequisitos

### Servidor de Aplicaciones
- **Apache Tomcat 9.0+** o **Tomcat 10.0+**
- **Java 17** instalado
- **Memoria mínima:** 2GB RAM
- **Espacio en disco:** 5GB libres

### Base de Datos
- **Microsoft SQL Server 2012+**
- **Usuario con permisos** de lectura/escritura en la base de datos
- **Conectividad** desde el servidor de aplicaciones

### Servicios Externos
- **LDAP/Active Directory** (para autenticación)
- **Servidor SMTP** (para envío de correos)

## 🚀 Proceso de Despliegue

### 1. Preparación del Entorno

```bash
# Crear directorio de uploads
sudo mkdir -p /opt/pqrs/uploads
sudo chown tomcat:tomcat /opt/pqrs/uploads
sudo chmod 755 /opt/pqrs/uploads

# Crear directorio de backups
sudo mkdir -p /opt/backups/pqrs
```

### 2. Configuración de Variables de Entorno

```bash
# Copiar template de variables de entorno
cp .env.production.template .env.production

# Editar variables de entorno
nano .env.production
```

**Variables críticas a configurar:**
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (mínimo 256 bits en Base64)
- `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `LDAP_URL`, `LDAP_SERVICE_USER`, `LDAP_SERVICE_PASSWORD`

### 3. Compilación

```bash
# Compilar aplicación para producción
mvn clean package -DskipTests

# Verificar que se generó el WAR
ls -la target/pqrs-backend-0.0.1-SNAPSHOT.war
```

### 4. Despliegue Automático

```bash
# Hacer ejecutable el script
chmod +x deploy-production.sh

# Ejecutar despliegue (como root)
sudo ./deploy-production.sh
```

### 5. Despliegue Manual

```bash
# Detener Tomcat
sudo systemctl stop tomcat

# Backup de aplicación anterior (si existe)
sudo cp /opt/tomcat/webapps/pqrs-backend-0.0.1-SNAPSHOT.war \
       /opt/backups/pqrs/backup-$(date +%Y%m%d_%H%M%S).war

# Eliminar aplicación anterior
sudo rm -f /opt/tomcat/webapps/pqrs-backend-0.0.1-SNAPSHOT.war
sudo rm -rf /opt/tomcat/webapps/pqrs-backend-0.0.1-SNAPSHOT/

# Copiar nuevo WAR
sudo cp target/pqrs-backend-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/

# Configurar permisos
sudo chown tomcat:tomcat /opt/tomcat/webapps/pqrs-backend-0.0.1-SNAPSHOT.war

# Iniciar Tomcat
sudo systemctl start tomcat
```

## 🔍 Verificación del Despliegue

### URLs de Verificación

```bash
# Health Check
curl http://localhost:8080/pqrs-backend-0.0.1-SNAPSHOT/actuator/health

# Información de la aplicación
curl http://localhost:8080/pqrs-backend-0.0.1-SNAPSHOT/actuator/info

# Endpoint público de prueba
curl http://localhost:8080/pqrs-backend-0.0.1-SNAPSHOT/api/tipodocumentos
```

### Logs de la Aplicación

```bash
# Ver logs en tiempo real
sudo journalctl -u tomcat -f

# Ver logs recientes
sudo journalctl -u tomcat -n 100

# Logs de Tomcat
sudo tail -f /opt/tomcat/logs/catalina.out
```

## 🛠️ Configuración de Tomcat

### Variables de Entorno en systemd

Crear archivo: `/etc/systemd/system/tomcat.service.d/pqrs-env.conf`

```ini
[Service]
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:sqlserver://servidor:1433;databaseName=pqrs;encrypt=true"
Environment="DB_USERNAME=usuario_prod"
Environment="DB_PASSWORD=password_seguro"
Environment="JWT_SECRET=tu_secreto_jwt_base64"
Environment="UPLOAD_DIR=/opt/pqrs/uploads"
# ... más variables según .env.production
```

### Configuración de Memoria JVM

Editar `/opt/tomcat/bin/setenv.sh`:

```bash
export JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
export CATALINA_OPTS="-Dspring.profiles.active=prod"
```

## 📊 Monitoreo y Mantenimiento

### Comandos Útiles

```bash
# Estado del servicio
sudo systemctl status tomcat

# Reiniciar aplicación
sudo systemctl restart tomcat

# Ver procesos Java
jps -v

# Monitorear memoria
free -h
df -h
```

### Endpoints de Monitoreo

- **Health:** `/actuator/health`
- **Info:** `/actuator/info`
- **Métricas:** `/actuator/metrics` (si está habilitado)

## 🔒 Seguridad

### Configuraciones Importantes

1. **JWT Secret:** Usar secreto fuerte y único para producción
2. **Base de Datos:** Conexión encriptada (encrypt=true)
3. **HTTPS:** Configurar certificado SSL en el proxy/load balancer
4. **Firewall:** Restringir acceso a puertos necesarios
5. **Logs:** No exponer información sensible en logs

### Variables Sensibles

⚠️ **NUNCA** commitear en Git:
- Contraseñas de base de datos
- Secretos JWT
- Credenciales de correo
- Credenciales LDAP

## 🚨 Troubleshooting

### Problemas Comunes

1. **Puerto 8080 ocupado:**
   ```bash
   sudo netstat -tulpn | grep :8080
   sudo kill -9 <PID>
   ```

2. **Error de conexión a BD:**
   - Verificar conectividad: `telnet servidor_bd 1433`
   - Revisar credenciales en variables de entorno
   - Verificar firewall

3. **Error de memoria:**
   - Aumentar heap size en `setenv.sh`
   - Monitorear con `jstat -gc <pid>`

4. **Error de permisos:**
   ```bash
   sudo chown -R tomcat:tomcat /opt/pqrs/
   sudo chmod -R 755 /opt/pqrs/uploads/
   ```

### Logs de Error

```bash
# Logs específicos de la aplicación
sudo journalctl -u tomcat | grep "ERROR\|WARN"

# Logs de inicio de Spring Boot
sudo journalctl -u tomcat | grep "Started PqrsBackendApplication"
```

## 📞 Soporte

Para soporte técnico:
- **Logs:** Siempre incluir logs relevantes
- **Configuración:** Verificar variables de entorno
- **Conectividad:** Probar conexiones a servicios externos

---

**Última actualización:** $(date)
**Versión del documento:** 1.0.0
