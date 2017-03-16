package examplecom.matej_zavrsni;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import examplecom.matej_zavrsni.model.EmailAdapter;
import examplecom.matej_zavrsni.model.EmailModel;

public class  MainActivity extends AppCompatActivity {

    public static final String LOG = "MainActivity";

    private EmailModel emailModel;

    private List<EmailModel> emailModels;

    protected Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getEmails();

        //when clicking on floating action button at the bottom of screen it will open email client
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
//                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getEmails() {
        showProgressDialog();
        final Thread t = new Thread(new Runnable() {
            public void run() {
                Properties props = new Properties();
                //IMAPS protocol
                props.setProperty("mail.store.protocol", "imaps");
                //Set host address
                props.setProperty("mail.imaps.host", "imaps.gmail.com");
                //Set specified port
                props.setProperty("mail.imaps.port", "993");
                //Using SSL
                props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.imaps.socketFactory.fallback", "false");
                //Setting IMAP session
                Session imapSession = Session.getInstance(props);

                Store store = null;
                try {
                    store = imapSession.getStore("imaps");
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
                //Connect to server by sending username and password.
                String mailServer = "imap.gmail.com";
                String username = "matej.testni982@gmail.com";
                String password = "matej982";
                try {
                    store.connect(mailServer, username, password);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                //Get all mails in Inbox Forlder
                Folder inbox = null;
                try {
                    inbox = store.getFolder("Inbox");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                try {
                    inbox.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                //Return result to array of message
                Message[] result = null;
                try {
                    result = inbox.getMessages();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                if(result != null && result.length > 0) {
                    emailModels = new ArrayList<>();
                    for(Message message : result) {
                        printMessage(message);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        setEmailList();
                    }
                });
            }
        });

        t.start();
    }

    public void printMessage(Message message) {

        System.out.println("------START MESSAGE-----------");
        String myMail = "";
        String from = "";
        String subject = "";

        try {
            // Get the header information
            from = ((InternetAddress) message.getFrom()[0])
                    .getPersonal();



            if (from == null)
                from = ((InternetAddress) message.getFrom()[0]).getAddress();
            System.out.println("FROM: " + from);
            subject = message.getSubject();
            System.out.println("SUBJECT: " + subject);
            // -- Get the message part (i.e. the message itself) --
            Part messagePart = message;
            Object content = messagePart.getContent();
            // -- or its first body part if it is a multipart message --
            if (content instanceof Multipart) {
                messagePart = ((Multipart) content).getBodyPart(0);
                System.out.println("[ Multipart Message ]");
            }
            // -- Get the content type --
            String contentType = messagePart.getContentType();
            // -- If the content is plain text, we can print it --
            System.out.println("CONTENT:" + contentType);
            if (contentType.startsWith("TEXT/PLAIN")
                    || contentType.startsWith("TEXT/HTML")) {
                InputStream is = messagePart.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                String thisLine = reader.readLine();
                while (thisLine != null) {
                    System.out.println(thisLine);
                    myMail = myMail + thisLine;
                    thisLine = reader.readLine();
                }


            }
            System.out.println("-------END MESSAGE-----------");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d(LOG, "myEmail: " + myMail);
        EmailModel emailModel = new EmailModel(from, subject, myMail);
        emailModels.add(emailModel);
//        return myMail;

    }

    private void setEmailList() {
        EmailAdapter adapter = new EmailAdapter(this, emailModels);

// Attach the adapter to a ListView

        ListView listView = (ListView) findViewById(R.id.email_list);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_emails) {
            getEmails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showProgressDialog() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            if (!isFinishing()) {
                progressDialog.show();
            }
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            if (!isFinishing()) {
                progressDialog.dismiss();
            }
        }
    }
}
