package com.applock.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import io.paperdb.Paper;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class Utils {

    private String EXTRA_LAST_APP = "EXTRA_LAST_APP";
    private Context context;

    public Utils(Context context) {
        this.context = context;
        Paper.init(context);
    }

    public boolean isLock(String packageName)
    {
        return Paper.book().read(packageName) != null;
    }

    public void lock(String pk)
    {
        Paper.book().write(pk,pk);
    }

    public void unlock(String pk)
    {
        Paper.book().delete(pk);
    }

    public void setLastApp(String pk)
    {
        Paper.book().write(EXTRA_LAST_APP,pk);
    }

    public String getLastApp()
    {
        String last = Paper.book().read(EXTRA_LAST_APP);
        return last;
    }

    public void clearLastApp()
    {
        Paper.book().delete(EXTRA_LAST_APP);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean checkPermission(Context ctx)
    {

        AppOpsManager appOpsManager = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(),ctx.getPackageName());

        return mode == MODE_ALLOWED;
    }


    UsageStatsManager usageStatsManager;

    public String getLauncherTopApp()
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 10000;
        String result ="";
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime,endTime);

        while (usageEvents.hasNextEvent())
        {
            usageEvents.getNextEvent(event);
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND){
                result = event.getPackageName();
            }
        }

        if(!TextUtils.isEmpty(result))
            return result;

        return "";
    }
}
