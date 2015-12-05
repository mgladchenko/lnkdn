package com.linkedin;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.SearchTerm;

public final class EmailManager {
    String host;
    String username;
    String password;
    Session session;
    Store store;

    public EmailManager(String username, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.stmp.user", username);
        props.put("mail.stmp.password", password);
        props.put("mail.store.protocol", "imaps");
        this.host = props.getProperty("mail.smtp.host");
        this.username = username;
        this.password = password;
        session = createSession(props);

    }

    private Session createSession(Properties props) {
        Authenticator auth = null;
        {
            auth = new UsernamePasswordAuthenticator(props.getProperty("mail.stmp.user"), props.getProperty("mail.stmp.password"));
        }
        return Session.getInstance(props, auth);
    }


    public void sendMessage(String[] messageRecipients, String messageSubject, String messageBody, URL messageAttachments) {

        try {
            Message message = new MimeMessage(session);
            InternetAddress[] addressTo = new InternetAddress[messageRecipients.length];
            for (int i = 0; i < messageRecipients.length; i++) {
                System.out.println("Message Recepient:"+messageRecipients[i]);
                addressTo[i] = new InternetAddress(messageRecipients[i]);
            }
            message.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            message.setSubject(messageSubject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(messageBody);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();

            try {
                String path = Paths.get(messageAttachments.toURI()).toFile().getAbsolutePath();
                DataSource source = new FileDataSource(path);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(path.substring(path.lastIndexOf("/") + 1));
                multipart.addBodyPart(messageBodyPart);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            // Send the complete message parts
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Message sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Message getBlankMessage() {
        return new MimeMessage(session);
    }

    public Message[] findMessageBySubject(final String subject, String folderName) {
        SearchTerm searchCondition = new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    if ((message.getSubject() != null) && (message.getSubject().contains(subject))) {
                        return true;
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };

        Message[] searchResults = new Message[0];
        try {
            Folder folder = getFolder(folderName);
            if (!folder.isOpen()) folder.open(Folder.READ_WRITE);
            searchResults = folder.search(searchCondition);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public Message[] findMessageBySender(final String senderEmail, String folderName) {
        SearchTerm searchCondition = new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    if (message.getFrom().toString().contains(senderEmail)) {
                        return true;
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };

        Message[] searchResults = new Message[0];
        try {
            Folder folder = getFolder(folderName);
            if (!folder.isOpen()) folder.open(Folder.READ_WRITE);
            searchResults = folder.search(searchCondition);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return searchResults;
    }


    public String getMessageBody(final Message[] searchResult) {
        String body = null;
        try {
            for (Message m : searchResult) {
                Object content = null;
                try {
                    content = m.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (content instanceof String) {
                    body = (String) content;
                } else if (content instanceof Multipart) {
                    try {
                        content = m.getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Multipart multipart = (Multipart) content;
                    BodyPart part = multipart.getBodyPart(0);
                    try {
                        body = (String) part.getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return body;
    }

    public String getLinkFromMessageBody(final Message[] searchResult) {
        String[] urls = getMessageBody(searchResult).split("http://");
        return "http://" + urls[1];
    }

    public String getMessageSender(final Message[] searchResult) {
        String messageSender = null;
        for (Message m : searchResult) {
            try {
                messageSender = m.getFrom()[0].toString();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return messageSender;
    }

    public void deleteMessage(final Message[] searchResult) {
        for (Message m : searchResult) {
            try {
                m.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMessageInFolder(final Message[] searchResult) {
        boolean isExist = (searchResult.length > 0);
        return isExist;
    }

    private Folder getFolder(String folderName) throws MessagingException {
        //Store store = session.getStore("imaps");
        Store store = getStore();
        store.close();
        store.connect(host, username, password);
        if (!store.isConnected()) {
            store.close();
            store.connect(host, username, password);
        }

        return store.getFolder(folderName);
    }

    private Store getStore() throws MessagingException {
        if (store == null) {
            store = session.getStore("imaps");
        }
        return store;
    }

    public void closeFolder(String folderName) throws MessagingException {
        Folder folder = getFolder(folderName);
        if (folder.isOpen()) folder.close(true);
    }

    public void closeStore() {
        try {
            Store store = getStore();
            if (store.isConnected()) store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public boolean moveMessages(String fromFolder, String toFolder) {
        closeStore();
        Folder inFolder = null;
        Folder outFolder = null;

        try {
            outFolder = getFolder(fromFolder);
            outFolder.open(Folder.READ_WRITE);
            inFolder = getFolder(toFolder);
            outFolder.copyMessages(outFolder.getMessages(), inFolder);
            for (Message msg : outFolder.getMessages()) {
                msg.setFlag(Flags.Flag.DELETED, true);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inFolder != null && inFolder.isOpen()) {
                    inFolder.close(true);
                }
                if (outFolder != null && outFolder.isOpen()) {
                    outFolder.close(true);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void cleanFolder(String folderName, int limit) throws MessagingException {
        closeStore();
        Folder folder = null;

        try {
            folder = getFolder(folderName);
            if (folder.getMessageCount() > limit) {
                folder.open(Folder.READ_WRITE);

                for (Message msg : folder.getMessages()) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                }
            }
        } finally {
            if (folder != null && folder.isOpen()) {
                // with option true - clean messages flagged as DELETE
                folder.close(true);
            }
        }
    }

    // --- Embedded ---

    private static class UsernamePasswordAuthenticator extends Authenticator {
        public final String username;
        public final String password;

        private UsernamePasswordAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}

