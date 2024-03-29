package com.applock.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.applock.broadcast.RestartServiceWhenStoped;

public class BackgroundManager {

    private static final int period = 15*10000;
    private static BackgroundManager instance;
    private static int ALARM_ID = 159874;

    private Context context;

    public static BackgroundManager getInstance() {
        if (instance == null )
            instance = new BackgroundManager();
        return instance;
    }

    public BackgroundManager init(Context c)
    {
        context = c;
        return this;
    }

    public boolean isServiceRunning(Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName()))
            {
                return true;
            }
        }

        return false;
    }


    public void startService()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(ServiceApplockJobIntent.class)) {
                Intent intent = new Intent(context, ServiceApplockJobIntent.class);
                ServiceApplockJobIntent.enqueueWork(context, intent);
            }
        }
        else
        {
            if (!isServiceRunning(ServiceApplock.class)) {
                context.startService(new Intent(context,ServiceApplock.class));
            }
        }
    }

    public void stopService(Class<?> serviceClass)
    {
        if(isServiceRunning(serviceClass))
        {
            context.startService(new Intent(context,serviceClass));
        }
    }


    public void startAlarmManager(){
        Intent intent = new Intent(context, RestartServiceWhenStoped.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,ALARM_ID,intent,0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+period,pendingIntent);
    }

    public void stopAlarm()
    {
        Intent intent = new Intent(context,RestartServiceWhenStoped.class);
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context,ALARM_ID,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
