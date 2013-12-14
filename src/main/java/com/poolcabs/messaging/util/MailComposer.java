/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.messaging.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Manish
 */
public class MailComposer {

    private String from;
    private String to;
    private String body;
    private String subject;

    public MailComposer(String to, String body, String subject) {
        this.to = to;
        this.body = body;
        this.subject = subject;
    }

    public MimeMessage compose() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("mail/mail.properties"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MailComposer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MailComposer.class.getName()).log(Level.SEVERE, null, ex);
        }

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom();
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
        } catch (MessagingException ex) {
            Logger.getLogger(MailComposer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return message;
    }
}
