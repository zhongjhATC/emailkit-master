package com.smailnet.emailkit;

import android.text.TextUtils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

class EmailUtils {

    /**
     * 获取Session
     *
     * @param config
     * @return
     */
    static Session getSession(EmailKit.Config config) {
        //获取配置参数
        String smtpHost = config.getSMTPHost();
        String imapHost = config.getIMAPHost();
        String smtpPort = String.valueOf(config.getSMTPPort());
        String imapPort = String.valueOf(config.getIMAPPort());
        //配置
        Properties properties = new Properties();
        if (!TextUtils.isEmpty(smtpHost) && !TextUtils.isEmpty(smtpPort)) {
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.port", smtpPort);
            properties.put("mail.smtp.ssl.enable", config.isSMTPSSL());
            properties.setProperty("mail.smtp.starttls.enable", "true");
            if (config.getMailType() != null && config.getMailType().equals(EmailKit.MailType.GMAIL)) {
                properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.smtp.socketFactory.fallback", "false");
                properties.setProperty("mail.smtp.socketFactory.port", "465");
            }
            properties.put("mail.smtp.connectiontimeout", 15000); // 连接时间限制，单位毫秒。是关于与邮件服务器建立连接的时间长短的。默认是无限制。
            properties.put("mail.smtp.timeout", 15000); // 邮件接收时间限制，单位毫秒。这个是有关邮件接收时间长短。默认是无限制。
            properties.put("mail.smtp.writetimeout", 15000); // 邮件发送时间限制，单位毫秒。有关发送邮件时内容上传的时间长短。默认同样是无限制。
        }
        if (!TextUtils.isEmpty(imapHost) && !TextUtils.isEmpty(imapPort)) {
            properties.put("mail.imap.auth", true);
            properties.put("mail.imap.host", imapHost);
            properties.put("mail.imap.port", imapPort);
            properties.put("mail.imap.ssl.enable", config.isIMAPSSL());
            properties.setProperty("mail.imap.partialfetch", "false");
            properties.setProperty("mail.imaps.partialfetch", "false");
        }
        Session session;

        //用户验证并返回Session，开启用户验证，设置发送邮箱的账号密码。
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getAccount(), config.getPassword());
            }
        });
        ObjectManager.setSession(session);
        return session;
    }

    /**
     * 获取Transport
     *
     * @param config
     * @return
     * @throws MessagingException
     */
    static Transport getTransport(EmailKit.Config config) throws MessagingException {
        if (ObjectManager.getTransport() == null || !ObjectManager.getTransport().isConnected()) {
            Session session = getSession(config);
            Transport transport = session.getTransport("smtp");
            transport.connect(config.getSMTPHost(), config.getAccount(), config.getPassword());
            ObjectManager.setTransport(transport);
            return transport;
        } else {
            return ObjectManager.getTransport();
        }
    }

    /**
     * 获取IMAPStore
     *
     * @param config
     * @return
     * @throws MessagingException
     */
    static IMAPStore getStore(EmailKit.Config config) throws MessagingException {
        if (ObjectManager.getStore() == null || !ObjectManager.getStore().isConnected()) {
            Session session = getSession(config);
            IMAPStore store = (IMAPStore) session.getStore("imap");
            store.connect(config.getIMAPHost(), config.getAccount(), config.getPassword());
            ObjectManager.setStore(store);
            return store;
        } else {
            return ObjectManager.getStore();
        }
    }

    /**
     * 获取IMAPFolder
     *
     * @param folderName
     * @param store
     * @param config
     * @return
     * @throws MessagingException
     */
    static IMAPFolder getFolder(String folderName, IMAPStore store, EmailKit.Config config) throws MessagingException {
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        if (Converter.MailTypeUtils.isNetEaseMail(config.getAccount())) {
            folder.doCommand(protocol -> {
                protocol.id("FUTONG");
                return null;
            });
        }
        folder.open(Folder.READ_WRITE);
        return folder;
    }

}
