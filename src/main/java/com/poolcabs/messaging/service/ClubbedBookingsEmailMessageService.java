/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.messaging.service;

import com.poolcabs.messaging.util.MailComposer;
import com.poolcabs.model.Booking;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class ClubbedBookingsEmailMessageService {

    private String subject = "Bookings for next hour";
    private String emailTemplatePath = "mail/ClubbedBookingsNotification.vsl";

    private MimeMessage createMailMessage(List<Booking> bookings, String[] toEmailAddress) {
        VelocityEngine engine = new VelocityEngine();
        configure(engine);
        Template template = engine.getTemplate(emailTemplatePath);
        VelocityContext context = new VelocityContext();
        context.put("name", bookings.get(0).getCustomerName());
        context.put("bookingList", bookings);
        context.put("organization", ResourceBundle.getBundle("/Bundle").getString("Organization_Name"));
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        String body = writer.toString();
        MailComposer composer = new MailComposer(toEmailAddress, body, subject);
        MimeMessage message = composer.compose();
        return message;
    }

    public void sendMail(List<Booking> bookings, String[] toEmailAddress) {
        MimeMessage message = createMailMessage(bookings, toEmailAddress);
        try {
            Transport.send(message);
        } catch (MessagingException ex) {
            Logger.getLogger(ClubbedBookingsEmailMessageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void configure(VelocityEngine engine) {
        Properties properties = new Properties();
        properties.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath." + Velocity.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
        engine.init(properties);
    }
}
