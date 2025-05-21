package com.claude.springboot.app.security.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.claude.springboot.app.entities.Pqrs;
import com.claude.springboot.app.security.entities.Usuario;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${cors.allowed-origins}")
    private String frontendUrl;
    
    public void enviarEmailActivacion(String to, String token, String codigo, String nombre) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Activación de cuenta");
    
            // Crear contexto para el template
            Context context = new Context();
            context.setVariable("nombre", nombre);
            context.setVariable("token", token);
            context.setVariable("codigo", codigo);
            context.setVariable("urlActivacion", frontendUrl + "/activate-user/" + token);
            
            // Procesar el template
            String contenido = templateEngine.process("activacion-cuenta", context);
            
            helper.setText(contenido, true);
            
            mailSender.send(message);
            log.info("Email de activación enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error enviando email de activación", e);
            throw new RuntimeException("Error enviando email de activación", e);
        }
    }

    public void enviarConfirmacionPQRS(String email, String numeroRadicado, String linkConsulta) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Confirmación de Radicación PQRS " + numeroRadicado);
            
            String contenido = String.format(
                "Estimado usuario,%n%n" +
                "Su PQRS ha sido registrado exitosamente con el número de radicado: %s%n%n" +
                "Para consultar el estado y dar seguimiento a su solicitud, puede acceder al siguiente enlace:%n" +
                "%s/consulta-pqrs/%s%n%n" +
                "Por favor, conserve este correo para futuras referencias.%n%n" +
                "Atentamente,%n" +
                "Sistema de PQRS",
                numeroRadicado,
                frontendUrl,
                linkConsulta
            );
            
            helper.setText(contenido);
            mailSender.send(message);
            
        } catch (Exception e) {
            log.error("Error enviando email de confirmación PQRS: {}", e.getMessage());
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }

    public void notificarNuevaRespuestaSolicitante(Pqrs pqrs) {
        try {
            Usuario funcionario = pqrs.getUsuarioAsignado();
            if (funcionario == null || funcionario.getPersona() == null) {
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(funcionario.getPersona().getEmail());
            helper.setSubject("Nueva respuesta del solicitante - PQRS " + pqrs.getNumeroRadicado());
            
            String contenido = String.format(
                "Estimado %s,%n%n" +
                "El solicitante ha proporcionado una nueva respuesta al PQRS %s.%n%n" +
                "Para revisar la respuesta, acceda al sistema mediante el siguiente enlace:%n" +
                "%s/pqrs/%d%n%n" +
                "Atentamente,%n" +
                "Sistema de PQRS",
                funcionario.getPersona().getNombreCompleto(),
                pqrs.getNumeroRadicado(),
                frontendUrl,
                pqrs.getIdPqrs()
            );
            
            helper.setText(contenido);
            mailSender.send(message);
            
        } catch (Exception e) {
            log.error("Error enviando notificación de respuesta: {}", e.getMessage());
            throw new RuntimeException("Error enviando notificación", e);
        }
    }

    public void notificarNuevaRespuestaUsuario(Pqrs pqrs) {
        try {
            // Obtener el funcionario asignado
            Usuario funcionario = pqrs.getUsuarioAsignado();
            if (funcionario == null || funcionario.getPersona() == null) {
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(funcionario.getPersona().getEmail());
            helper.setSubject("Nueva respuesta de usuario registrado - PQRS " + pqrs.getNumeroRadicado());
            
            String contenido = String.format(
                "Estimado %s,%n%n" +
                "El usuario registrado ha proporcionado una nueva respuesta al PQRS %s.%n%n" +
                "Para revisar la respuesta, acceda al sistema mediante el siguiente enlace:%n" +
                "%s/pqrs/%d%n%n" +
                "Atentamente,%n" +
                "Sistema de PQRS",
                funcionario.getPersona().getNombreCompleto(),
                pqrs.getNumeroRadicado(),
                frontendUrl,
                pqrs.getIdPqrs()
            );
            
            helper.setText(contenido);
            mailSender.send(message);
            
        } catch (Exception e) {
            log.error("Error enviando notificación de respuesta de usuario: {}", e.getMessage());
            // No lanzamos la excepción para no interrumpir el flujo principal
        }
    }

    public void enviarCorreoConfirmacionPqrsAUsuarioRegistrado(Pqrs pqrs, Usuario usuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(usuario.getPersona().getEmail());
            helper.setSubject("Confirmación de Radicación PQRS " + pqrs.getNumeroRadicado());
            
            // Crear contexto para el template
            Context context = new Context();
            context.setVariable("nombreUsuario", usuario.getPersona().getNombreCompleto());
            context.setVariable("numeroRadicado", pqrs.getNumeroRadicado());
            context.setVariable("titulo", pqrs.getTitulo());
            context.setVariable("tema", pqrs.getTema().getNombre());
            context.setVariable("prioridad", pqrs.getPrioridad());
            context.setVariable("urlConsulta", frontendUrl + "/pqrs/consulta-pqrs/" + pqrs.getTokenConsulta());
            
            // Procesar el template
            String contenido = templateEngine.process("notificacion-confirmation", context);
            log.debug("Contenido del email generado: {}", contenido); // Para debug
            
            helper.setText(contenido, true);
            mailSender.send(message);
            log.info("Email de confirmación enviado a: {}", usuario.getPersona().getEmail());
            
        } catch (Exception e) {
            log.error("Error enviando email de confirmación PQRS a usuario registrado: {}", e.getMessage());
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }

    public void notificarNuevoSeguimiento(Pqrs pqrs, String tipoSeguimiento) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(pqrs.getEmailSolicitante());
            helper.setSubject("Actualización en su PQRS " + pqrs.getNumeroRadicado());
    
            // Crear contexto para el template
            Context context = new Context();
            context.setVariable("nombreUsuario", pqrs.getNombreSolicitante());
            context.setVariable("numeroRadicado", pqrs.getNumeroRadicado());
            context.setVariable("titulo", pqrs.getTitulo());
            context.setVariable("estadoPqrs", pqrs.getEstadoPqrs());
            context.setVariable("tipoActualizacion", tipoSeguimiento);
            context.setVariable("fechaActualizacion", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    
            // URL de consulta dependiendo del tipo de usuario
            String urlConsulta = (pqrs.getTokenConsulta() != null) ?
                frontendUrl + "/consulta-pqrs/" + pqrs.getTokenConsulta() :
                frontendUrl + "/pqrs/" + pqrs.getIdPqrs();
            context.setVariable("urlConsulta", urlConsulta);
            
            // Procesar el template
            String contenido = templateEngine.process("notificacion-seguimiento", context);
            
            helper.setText(contenido, true);
            mailSender.send(message);
            
            log.info("Notificación de seguimiento enviada a: {}", pqrs.getEmailSolicitante());
            
        } catch (Exception e) {
            log.error("Error enviando notificación de seguimiento: {}", e.getMessage());
            // No lanzamos la excepción para no interrumpir el flujo principal
        }
    }


}
