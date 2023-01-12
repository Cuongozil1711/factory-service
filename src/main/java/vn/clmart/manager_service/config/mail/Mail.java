
package vn.clmart.manager_service.config.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.*;
import java.io.File;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Mail {
    private JavaMailSender mailSender;

    private VelocityEngine velocityEngine;
    private String defaultMailFrom;

    private static final Logger logger = LogManager.getLogger(Mail.class);
    /******************************************
     * Set value mailSender
     *
     * @param
     * @return
     ******************************************/
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String getDefaultMailFrom() {
        return defaultMailFrom;
    }

    public void setDefaultMailFrom(String defaultMailFrom) {
        this.defaultMailFrom = defaultMailFrom;
    }
    /******************************************
     * Send mail
     *
     * @param , mailTo, mailSubject, mailContent mode = 0(register),
     *                  1(reset)
     * @return
     ******************************************/
    public void sendMail(MailFormNew mailForm) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailForm.getMailFrom());
            helper.setSubject(mailForm.getSubject());
            // set send to
            String[] arraySendTo = mailForm.getLstUserSendTo().stream().distinct().toArray(String[]::new);
            helper.setTo(arraySendTo);
            // set send cc
            if (null != mailForm.getLstUserSendCc() && mailForm.getLstUserSendCc().size() > 0) {
                String[] arraySendCC = mailForm.getLstUserSendCc().stream().distinct().toArray(String[]::new);
                helper.setCc(arraySendCC);
            }

            if(null == mailForm.getLstUserSendBcc()) {
//				List<String> tmp = new ArrayList<String>();
//				tmp.add(mailForm.getMailFrom());
//				mailForm.setLstUserSendBcc(tmp);
            } else {
                mailForm.getLstUserSendBcc().add(mailForm.getMailFrom());
            }

            // set send bcc
            if (null != mailForm.getLstUserSendBcc() && mailForm.getLstUserSendBcc().size() > 0) {
                String[] arraySendBcc = mailForm.getLstUserSendBcc().toArray(new String[0]);
                helper.setBcc(arraySendBcc);
            }
            // set template
            Template template = velocityEngine.getTemplate(mailForm.getTemplateDir(), "UTF-8");
            VelocityContext velocityContext = new VelocityContext();
            for (String key : mailForm.getVelocityContextMap().keySet()) {
                velocityContext.put(key, mailForm.getVelocityContextMap().get(key));
            }
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            helper.setText(stringWriter.toString());
            // set attach file
            if (null != mailForm.getLstFilePathAttachment()) {
                for (String fileName : mailForm.getLstFilePathAttachment().keySet()) {
                    FileSystemResource fileAttach = new FileSystemResource(new File(mailForm.getLstFilePathAttachment().get(fileName)));
                    // encode file name to UTF 8 (Japanese font error)
                    String fileNameEncode = fileName;
                    try {
                        fileNameEncode = MimeUtility.encodeText(fileNameEncode, "UTF-8", null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    helper.addAttachment(fileNameEncode, fileAttach);
                }
            }
            for (File file : mailForm.getLstFileAttachment()) {
                FileSystemResource fileAttach = new FileSystemResource(file);
                // encode file name to UTF 8 (Japanese font error)
                String fileNameEncode = file.getName();
                try {
                    fileNameEncode = MimeUtility.encodeText(fileNameEncode, "UTF-8", null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                helper.addAttachment(fileNameEncode, fileAttach);
            }
            // send email
            mailSender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void sendMailHtml(MailFormNew mailForm) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            BodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            // MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(new InternetAddress(mailForm.getMailFrom(), "Hệ thống SmartShop", "utf-8"));
            message.setSubject(mailForm.getSubject(), "utf-8");
            // set send to
            if (null != mailForm.getSendTo() && !mailForm.getSendTo().isEmpty()) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailForm.getSendTo()));
            }

            if (null != mailForm.getLstUserSendTo() && !mailForm.getLstUserSendTo().isEmpty()) {
                Address[] address = new Address[mailForm.getLstUserSendTo().size()];
                // set send to
                for (int i = 0; i < mailForm.getLstUserSendTo().size(); i++) {
                    address[i] = new InternetAddress(mailForm.getLstUserSendTo().get(i));
                }
                message.addRecipients(Message.RecipientType.TO, address);
            }

            if (null != mailForm.getLstUserSendCc() && !mailForm.getLstUserSendCc().isEmpty()) {
                Address[] address = new Address[mailForm.getLstUserSendCc().size()];
                // set send to
                for (int i = 0; i < mailForm.getLstUserSendCc().size(); i++) {
                    address[i] = new InternetAddress(mailForm.getLstUserSendCc().get(i));
                }
                message.addRecipients(Message.RecipientType.CC, address);
            }
//			if(null != mailForm.getLstUserSendBcc() && !mailForm.getLstUserSendBcc().isEmpty()) {
//
//				mailForm.getLstUserSendBcc().add(mailForm.getMailFrom());
//			} else {
////				List<String> tmp = new ArrayList<String>();
////				tmp.add(mailForm.getMailFrom());
////				mailForm.setLstUserSendBcc(tmp);
//			}
            if (null != mailForm.getLstUserSendBcc()  && !mailForm.getLstUserSendBcc().isEmpty()) {
                Address[] address = new Address[mailForm.getLstUserSendBcc().size()];
                // set send to
                for (int i = 0; i < mailForm.getLstUserSendBcc().size(); i++) {
                    address[i] = new InternetAddress(mailForm.getLstUserSendBcc().get(i));
                }
                message.addRecipients(Message.RecipientType.BCC, address);
            }

            // set template
            Template template = velocityEngine.getTemplate(mailForm.getTemplateDir(), "UTF-8");
            VelocityContext velocityContext = new VelocityContext();
            for (String key : mailForm.getVelocityContextMap().keySet()) {
                velocityContext.put(key, mailForm.getVelocityContextMap().get(key));
            }
            // fill param
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            // message.setContent(stringWriter.toString(), "text/html; charset=utf-8");
            messageBodyPart.setContent(stringWriter.toString(), "text/html; charset=utf-8");

            // set body content
            multipart.addBodyPart(messageBodyPart);

            // set attach file
            if (null != mailForm.getLstFilePathAttachment()) {
                for (String fileName : mailForm.getLstFilePathAttachment().keySet()) {
                    // encode file name to UTF 8 (Japanese font error)
                    String fileNameEncode = fileName;
                    try {
                        fileNameEncode = MimeUtility.encodeText(fileNameEncode, "UTF-8", null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(fileName);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileNameEncode);
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            if (null != mailForm.getLstFileAttachment()) {
                for (File file : mailForm.getLstFileAttachment()) {
                    // encode file name to UTF 8 (Japanese font error)
                    String fileNameEncode = file.getName();
                    try {
                        fileNameEncode = MimeUtility.encodeText(fileNameEncode, "UTF-8", null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileNameEncode);
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            message.setContent(multipart);
            // send email
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMailSimple(String defaultMailFrom, List<String> mailTo, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultMailFrom);
        message.setTo(mailTo.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
