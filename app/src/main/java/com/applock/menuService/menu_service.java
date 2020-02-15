package com.applock.menuService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.applock.R;
import com.applock.model.AppItem;
import com.applock.utils.Utils;

import java.util.List;

public class menu_service extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;

    List<AppItem> notifiRunn;

    public menu_service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.nav_menu, null);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        //Initially view will be added to top-left corner
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 0;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        ImageButton drop_menu = (ImageButton) mChatHeadView.findViewById(R.id.moveDown);
        final LinearLayout ll = (LinearLayout) mChatHeadView.findViewById(R.id.latestAppsHere);

        Animation anim = new AlphaAnimation(0.4f, 1.0f);
        anim.setDuration(100); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        drop_menu.startAnimation(anim);


        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(90.0f, -90.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(1500);
        animRotate.setRepeatCount(Animation.INFINITE);
        animRotate.setRepeatMode(Animation.REVERSE);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);
        drop_menu.startAnimation(animSet);


        Utils utils = new Utils(this);
        String appRunning = utils.getLauncherTopApp();
        String oldOne = utils.getLastApp();


        PackageManager pk = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN,null);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = pk.queryIntentActivities(intent,0);


        for (ResolveInfo resolveInfo:resolveInfoList)
        {
            final ActivityInfo activityInfo = resolveInfo.activityInfo;
            final String pkN =activityInfo.packageName;

            if(utils.isLock(pkN)) {
                if(appRunning!=null && appRunning.equals(activityInfo.packageName)) {
                    ImageView icon = new ImageView(this);
                    notifiRunn.add(new AppItem(activityInfo.loadIcon(pk),activityInfo.loadLabel(pk).toString() ,activityInfo.packageName ));
                    icon.setImageDrawable(activityInfo.loadIcon(pk));
                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(menu_service.this,activityInfo.packageName.getClass()));
                        }
                    });
                    ll.addView(icon);

                }
                if(oldOne!=null && oldOne.equals(activityInfo.packageName))
                {
                    ImageView icon = new ImageView(this);
                    icon.setImageDrawable(activityInfo.loadIcon(pk));
                    ll.addView(icon);
                }
            }

        }


        drop_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll.getVisibility() == View.GONE)
                    ll.setVisibility(View.VISIBLE);
                else
                    ll.setVisibility(View.GONE);
            }
        });

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }
}
