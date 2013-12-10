/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manish
 */
public class MessagingRunnble implements Runnable {

    private List<URL> urlList;

    public MessagingRunnble(List<URL> url) {
        this.urlList = url;
    }

    @Override
    public void run() {
        InputStream in = null;
        try {
            for (URL url : urlList) {
                in = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String i;
                String response = "";
                while ((i = reader.readLine()) != null) {
                    response += i;
                }
                Logger.getLogger(MessagingRunnble.class.getName()).log(Level.SEVERE, "URL : " + url + "Response : " + response);
            }
        } catch (IOException ex) {
            Logger.getLogger(MessagingRunnble.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
