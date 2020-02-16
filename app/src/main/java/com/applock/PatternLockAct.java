package com.applock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.applock.dealFiles.files;
import com.applock.model.Password;
import com.applock.services.BackgroundManager;
import com.applock.utils.Utils;
import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.shuhart.stepview.StepView;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PatternLockAct extends AppCompatActivity {

    StepView stepView;
    LinearLayout normalLayout;
    TextView status_password;
    RelativeLayout relativeLayout;

    Password utilsPasword;
    String userPassword;
    Boolean passwordReset;
    int randomNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        BackgroundManager.getInstance().init(this).startService();

        if (getIntent().getStringExtra("pr") != null) {
            passwordReset = getIntent().getStringExtra("pr").equals("YES");
        }else
            passwordReset = false;
        files f = new files();

        String check = f.readFromFile(this);

        Toast.makeText(this, check, Toast.LENGTH_LONG).show();

        if (getIntent().getStringExtra("broadcast_reciever") != null)
        {
            if(!inApp()) {
                startCurrentHomePackage();
                finish();
                return;
            }
        }

        initIconApp();
        initLayout();
        initPatternListener();
        initClickListener();

    }

    void initClickListener()
    {

        findViewById(R.id.emailDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed = findViewById(R.id.email);
                if (ed.getText().equals(""))
                    Toast.makeText(PatternLockAct.this, "Enter Valid Email.", Toast.LENGTH_LONG).show();
                else {

                    if(isEmailValid(ed.getText().toString())) {
                        stepView.done(true);
                        utilsPasword.setEmail(ed.getText().toString());
                        startAct();
                    }else
                    {
                        Toast.makeText(PatternLockAct.this, "Enter Valid Email.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNumber = randomGen();
                sendMail(randomNumber);
                findViewById(R.id.allBody).setVisibility(View.GONE);
                findViewById(R.id.confirmEmail).setVisibility(View.VISIBLE);
            }
        });

        final EditCodeView editCodeView = (EditCodeView) findViewById(R.id.edit_code);
        editCodeView.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {
                int code_edit = Integer.parseInt(editCodeView.getCode());
                if(code_edit == randomNumber) {
                    PatternLockAct p = new PatternLockAct();
                    Intent i = new Intent(PatternLockAct.this, p.getClass());
                    i.putExtra("pr","YES");
                    startActivity(i);
                }
                else
                    Toast.makeText(PatternLockAct.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void sendMail(int number) {
        final String username = "saadatali0202@gmail.com";
        final String password = "saadatali117863";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            /*TODO: if s empty*/
            String s = utilsPasword.getEmail();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(s));
            message.setSubject("Sent from MobileApp for Reset Password" );

            message.setText("Enter The Following Code to reset Password : "+number);

            new SendMailTask().execute(message);

        }catch (MessagingException mex) {
            mex.printStackTrace();
        }


    }

    private class SendMailTask extends AsyncTask<Message,String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PatternLockAct.this,null, "Sending mail", true, false);
        }



        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            }
            catch(SendFailedException ee)
            {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                return "error1";
            }catch (MessagingException e) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                return "error2";
            }

        }


        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Success"))
            {

                super.onPostExecute(result);
                progressDialog.dismiss();
                Toast.makeText(PatternLockAct.this, "Mail Sent Successfully", Toast.LENGTH_LONG).show();

            }
            else
            if(result.equals("error1"))
                Toast.makeText(PatternLockAct.this, "Email Failure", Toast.LENGTH_LONG).show();
            else
            if(result.equals("error2"))
                Toast.makeText(PatternLockAct.this, "Email Sent problem2", Toast.LENGTH_LONG).show();

        }
    }


    int randomGen()
    {
        Random rn = new Random();
        int range = 99999 - 10000 + 1;
        return rn.nextInt(range) + 10000;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private boolean inApp() {
        files appCheck = new files();

        String check = appCheck.readFromFile(this);

        return check.contains("Yes");
    }

    private void initIconApp() {

        if (getIntent().getStringExtra("broadcast_reciever") != null)
        {
            ImageView icon = findViewById(R.id.app_icon);
            String current_app = new Utils(this).getLastApp();
            ApplicationInfo applicationInfo = null;

            try {
                applicationInfo = getPackageManager().getApplicationInfo(current_app,0);
            }catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }

            icon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
        }
    }


    public void initPatternListener()
    {
        final PatternLockView patternLockView = findViewById(R.id.pattern_view);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String pwd = PatternLockUtils.patternToString(patternLockView,pattern);

                if(pwd.length() <4 )
                {
                    status_password.setText(utilsPasword.SCHEMA_FAILED);
                    patternLockView.clearPattern();
                    return;
                }

                if(utilsPasword.getPassword() == null)
                {
                    if (utilsPasword.isFirstStep())
                    {
                        userPassword = pwd;
                        utilsPasword.setFirstStep(false);
                        status_password.setText(utilsPasword.STATUS_NEXT_STEP);
                        stepView.go(1,true);
                    }else
                    {
                        if(userPassword.equals(pwd)){
                            utilsPasword.setPassword(userPassword);
                            status_password.setText(utilsPasword.STATUS_PASSWORD_CORRECT);
                            stepView.go(2,true);
                            findViewById(R.id.emailLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.pattern_view).setVisibility(View.GONE);
                        }else{
                            status_password.setText(utilsPasword.STATUS_PASSWORD_INCORRECT);
                        }
                    }

                }
                else
                {
                    if(utilsPasword.isCorrect(pwd))
                    {
                        status_password.setText(utilsPasword.STATUS_PASSWORD_CORRECT);
                        startAct();
                    }else
                    {
                        status_password.setText(utilsPasword.STATUS_PASSWORD_INCORRECT);
                    }
                }

                patternLockView.clearPattern();
            }

            @Override
            public void onCleared() {

            }
        });
    }

    private void startAct() {
        if (getIntent().getStringExtra("broadcast_reciever") == null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private void initLayout()
    {
        stepView = findViewById(R.id.step_view);
        normalLayout = findViewById(R.id.normal_layout);
        relativeLayout = findViewById(R.id.root);
        status_password = findViewById(R.id.status_password);

        utilsPasword = new Password(this);
        status_password.setText(utilsPasword.STATUS_FIRST_STEP);

        if (passwordReset)
        {
            utilsPasword.setPassword(null);
        }

        if(utilsPasword.getPassword() == null)
        {
            normalLayout.setVisibility(View.GONE);
            stepView.      setVisibility(View.VISIBLE);
            stepView.setStepsNumber(3);
            stepView.go(0,true);
        }else
        {
            normalLayout.setVisibility(View.VISIBLE);
            stepView.setVisibility(View.GONE);

            int blackColor= ResourcesCompat.getColor(getResources(),R.color.blue,null);
            relativeLayout.setBackgroundColor(blackColor);
            status_password.setTextColor(Color.WHITE);

        }
    }

    @Override
    public void onBackPressed() {
        if (utilsPasword.getPassword() == null && !utilsPasword.isFirstStep())
        {
            stepView.go(0,true);
            utilsPasword.setFirstStep(true);
            status_password.setText(utilsPasword.STATUS_FIRST_STEP);
        }
        {
            startCurrentHomePackage();
            finish();
            super.onBackPressed();
        }
    }

    private void startCurrentHomePackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName,activityInfo.name);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);

        new Utils(this).clearLastApp();
    }
}
