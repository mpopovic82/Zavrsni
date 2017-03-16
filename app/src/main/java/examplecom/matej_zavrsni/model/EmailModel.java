package examplecom.matej_zavrsni.model;

/**
 * Created by goran on 11/1/2016.
 */
public class EmailModel {

    private String from;
    private String subject;
    private String emailText;

    public EmailModel(String from, String subject, String emailText) {
        this.from = from;
        this.subject = subject;
        this.emailText = emailText;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }
}
