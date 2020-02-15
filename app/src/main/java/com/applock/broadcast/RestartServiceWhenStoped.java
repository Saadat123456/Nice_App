package com.applock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.applock.PatternLockAct;
import com.applock.utils.Utils;

public class RestartServiceWhenStoped extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils utils = new Utils(context);
        String appRunning = utils.getLauncherTopApp();




        if(utils.isLock(appRunning)){

            if(!appRunning.equals(utils.getLastApp()))
            {
                utils.getLastApp();
                utils.setLastApp(appRunning);

                Intent i = new Intent(context, PatternLockAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("broadcast_receiver","broadcast_receiver");
                context.startActivity(i);
            }

        }
    }
}
