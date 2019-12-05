package com.diloopstudios.main;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

 

public class EmailAPI {
    public void sendmail(String subject, String message, String recipient)
    {
        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("vssammcyber", "Cyber@12345"));
        email.setSSLOnConnect(true);
        try
        {
            email.setFrom("vssammcyber@gmail.com");
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(recipient); //sathishe@virtusa.com
            email.send();
            System.out.println("Email Sent Successfully!");
        }
        catch(Exception e)
        {
            System.out.println("Email Exception occured! " + e.getMessage());
        }
    }
}