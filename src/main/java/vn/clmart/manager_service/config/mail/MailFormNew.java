package vn.clmart.manager_service.config.mail;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MailFormNew {
    private String mailFrom;
    private String subject;
    private String templateDir;
    private String sendTo;
    private List<String> lstUserSendTo;
    private List<String> lstUserSendCc;
    private List<String> lstUserSendBcc;
    // file attach
    private Map<String, String> lstFilePathAttachment;
    private List<File> lstFileAttachment;
    // pass param into template mail
    private Map<String, String> velocityContextMap;

    public MailFormNew() {
        super();
    }

    public MailFormNew(String mailFrom, String subject, String templateDir, List<String> lstUserSendTo,
                       List<String> lstUserSendCc, List<String> lstUserSendBcc, Map<String, String> lstFilePathAttachment,
                       Map<String, String> velocityContextMap) {
        super();
        this.mailFrom = mailFrom;
        this.subject = subject;
        this.templateDir = templateDir;
        this.lstUserSendTo = lstUserSendTo;
        this.lstUserSendCc = lstUserSendCc;
        this.lstUserSendBcc = lstUserSendBcc;
        this.lstFilePathAttachment = lstFilePathAttachment;
        this.velocityContextMap = velocityContextMap;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public List<String> getLstUserSendTo() {
        return lstUserSendTo;
    }

    public void setLstUserSendTo(List<String> lstUserSendTo) {
        this.lstUserSendTo = lstUserSendTo;
    }

    public List<String> getLstUserSendCc() {
        return lstUserSendCc;
    }

    public void setLstUserSendCc(List<String> lstUserSendCc) {
        this.lstUserSendCc = lstUserSendCc;
    }

    public List<String> getLstUserSendBcc() {
        return lstUserSendBcc;
    }

    public void setLstUserSendBcc(List<String> lstUserSendBcc) {
        this.lstUserSendBcc = lstUserSendBcc;
    }

    public Map<String, String> getLstFilePathAttachment() {
        return lstFilePathAttachment;
    }

    public void setLstFilePathAttachment(Map<String, String> lstFilePathAttachment) {
        this.lstFilePathAttachment = lstFilePathAttachment;
    }

    public Map<String, String> getVelocityContextMap() {
        return velocityContextMap;
    }

    public void setVelocityContextMap(Map<String, String> velocityContextMap) {
        this.velocityContextMap = velocityContextMap;
    }

    public List<File> getLstFileAttachment() {
        return lstFileAttachment;
    }

    public void setLstFileAttachment(List<File> lstFileAttachment) {
        this.lstFileAttachment = lstFileAttachment;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
}
