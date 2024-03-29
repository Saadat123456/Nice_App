package com.applock.model;

import android.content.Context;

import io.paperdb.Paper;

public class Password {

    private String PASSWORD_KEY = "PASSWORD_KEY";
    public String STATUS_FIRST_STEP = "Draw an unlock pattern";
    public String STATUS_NEXT_STEP = "Draw pattern again to confirm";
    public String STATUS_PASSWORD_CORRECT = "Pattern Set";
    public String STATUS_PASSWORD_INCORRECT = "Try Again";
    public String SCHEMA_FAILED = "Connect at least 4 dots";

    private boolean isFirstStep=true;

    public Password(Context ctx)
    {
        Paper.init(ctx);
    }

    public void setPassword(String pwd)
    {
        if (pwd == null) {
            if (Paper.book().exist(PASSWORD_KEY))
            {
                Paper.book().delete(PASSWORD_KEY);
                Paper.book().delete("Email");
            }
        }else {
            Paper.book().write(PASSWORD_KEY,pwd);
        }
    }

    public void setEmail(String mail)
    {
        Paper.book().write("Email",mail);
    }
    public String getEmail(){ return Paper.book().read("Email"); }

    public String getPassword()
    {
        return Paper.book().read(PASSWORD_KEY);
    }

    public boolean isFirstStep()
    {
        return isFirstStep;
    }

    public void setFirstStep(boolean firstStep)
    {
        isFirstStep = firstStep;
    }

    public boolean isCorrect(String pwd)
    {
        return pwd.equals(getPassword());
    }

}
