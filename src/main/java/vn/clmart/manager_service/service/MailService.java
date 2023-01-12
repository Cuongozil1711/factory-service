package vn.clmart.manager_service.service;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.config.mail.Mail;
import vn.clmart.manager_service.config.mail.MailFormNew;
import vn.clmart.manager_service.dto.request.MailSendExport;
import vn.clmart.manager_service.untils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class MailService {

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;


    private JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public boolean sendEmailStatusExport(MailSendExport mailSendExport, File file) {
        Mail mail = new Mail();
        mail.setDefaultMailFrom(username);
        mail.setMailSender(getJavaMailSender());
        try {
            VelocityEngine engine = new VelocityEngine();
            Properties config = new Properties();
            config.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
            config.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            config.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            engine.init(config);
            mail.setVelocityEngine(engine);

            MailFormNew mailForm = new MailFormNew();
            mailForm.setSubject("[" + mailSendExport.getStatus() + "] Xuất kho tại " + mailSendExport.getCompanyName());
            mailForm.setTemplateDir(Constants.MAIL_ON_SUCCESS_03);
            Map<String, String> velocityContextMap = new HashMap<String, String>();
            velocityContextMap.put("companyName", mailSendExport.getCompanyName());
            velocityContextMap.put("employeeExport", mailSendExport.getEmployeeExport());
            velocityContextMap.put("codeExport", mailSendExport.getCodeExport());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            velocityContextMap.put("dateExport", sdf.format(new Date()));
            velocityContextMap.put("companyNameTo", mailSendExport.getCompanyNameTo());
            velocityContextMap.put("status", mailSendExport.getStatus());


            mailForm.setVelocityContextMap(velocityContextMap);
            // mail from : giá trị khai báo trong spring-mail.xml
            mailForm.setMailFrom(mail.getDefaultMailFrom());

            List<String> listUserTo = new ArrayList<>();
            listUserTo.add("cuongnv.nsmv@gmail.com");
            listUserTo.add("linhk95lhp@gmail.com");
            mailForm.setLstUserSendTo(listUserTo);

            // đính kèm file
            List<File> fileList = new ArrayList<>();
            fileList.add(file);
            // đính kèm file
            mailForm.setLstFileAttachment(fileList);

            // thực hiện gửi mail
            mail.sendMailHtml(mailForm);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }
}
