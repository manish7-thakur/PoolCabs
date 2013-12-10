/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.messaging.service;

import com.poolcabs.messaging.util.MailComposer;
import com.poolcabs.model.User;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class UserRegistrationEmailMessageService {

    private String subject = "PoolCabs Registration";
    private String emailTemplatePath = "/mail/UserRegistrationInvoice.vsl";

    private MimeMessage createMailMessage(User user) {
        VelocityEngine engine = new VelocityEngine();
        engine.init();
        Template template = engine.getTemplate(emailTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("firstname", user.getName());
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        String body = writer.toString();
        MailComposer composer = new MailComposer(user.getEmail(), body, subject);
        MimeMessage message = composer.compose();
        return message;
    }

    public void sendMail(User user) {
        MimeMessage message = createMailMessage(user);
        try {
            Transport.send(message);
        } catch (MessagingException ex) {
            Logger.getLogger(UserRegistrationEmailMessageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
