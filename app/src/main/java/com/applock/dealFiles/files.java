package com.applock.dealFiles;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.applock.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class files {

    public void writeFileOnInternalStorage(Context mcoContext, String sBody){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mcoContext.openFileOutput("appOpen.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(sBody);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(Context context)
    {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("appOpen.txt");

            if(inputStream!=null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String recieveString = "";
                StringBuilder stringBuilder= new StringBuilder();

                while((recieveString = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(recieveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return ret;
    }

}
