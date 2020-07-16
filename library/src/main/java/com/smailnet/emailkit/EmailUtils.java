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
//        if (ObjectManager.getSession() == null) {
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
//            if (config.getMailType() != null && config.getMailType().equals(EmailKit.MailType.OUTLOOK)) {
                //设置邮件传输服务器，由于本次是发送邮件操作，所需我们需要配置smtp协议，按outlook官方同步邮件的要求，依次配置协议地址，端口号和加密方法
//                properties.setProperty("mail.smtp.host", "smtp.office365.com");
//                properties.setProperty("mail.smtp.port", "587");
                properties.setProperty("mail.smtp.starttls.enable", "true");
                //用户验证并返回Session，开启用户验证，设置发送邮箱的账号密码。
//                properties.setProperty("mail.smtp.auth", "true");
                session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getAccount(), config.getPassword());
                    }
                });
//            } else {
//                session = Session.getInstance(properties);
//            }
            ObjectManager.setSession(session);
            return session;
//        } else {
//            return ObjectManager.getSession();
//        }
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
