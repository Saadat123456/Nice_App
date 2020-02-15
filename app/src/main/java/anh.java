//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.GridView;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.applock.adapter.AppAdapter;
//import com.applock.adapter.gridviewAdapter;
//import com.applock.dealFiles.files;
//import com.applock.menuService.menu_service;
//import com.applock.model.AppItem;
//import com.applock.utils.Utils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    LinearLayout layoutPermission;
//    Utils utils;
//    List<AppItem> lockedAppList;
//    gridviewAdapter gridViewAdapter;
//
//    ImageButton navAllApp,navSettings;
//    files file;
//    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//
//            startPaperdb();
//            initToolbar();
//            initView();
//
//    }
//
//
//    private void clickListener() {
//        navAllApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.lockTheApps).setVisibility(View.VISIBLE);
//                findViewById(R.id.lockedApps).setVisibility(View.GONE);
//            }
//        });
//
//        navSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.lockTheApps).setVisibility(View.GONE);
//                findViewById(R.id.lockedApps).setVisibility(View.VISIBLE);
//                settingsUp();
//            }
//        });
//    }
//    private void settingsUp()
//    {
//
//        GridView lockedGrid = findViewById(R.id.gridViewLockedApps);
//        lockedAppList = getLockedApps();
//        gridViewAdapter = new gridviewAdapter(this,lockedAppList);
//        lockedGrid.setAdapter(gridViewAdapter);
//    }
//
//
//
//
//    private void startPaperdb()
//    {
//        utils = new Utils(this);
//    }
//
//    private void initView()
//    {
//        RelativeLayout allA = findViewById(R.id.lockTheApps);
//        RelativeLayout lockedA = findViewById(R.id.lockedApps);
//
//        allA.setVisibility(View.GONE);
//        lockedA.setVisibility(View.VISIBLE);
//
//        RecyclerView recyclerView = findViewById(R.id.recycler_view_app);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        AppAdapter appAdapter = new AppAdapter(this,getAllApps());
//        recyclerView.setAdapter(appAdapter);
//
//        GridView lockedGrid = findViewById(R.id.gridViewLockedApps);
//        lockedAppList = getLockedApps();
//        gridViewAdapter = new gridviewAdapter(this,lockedAppList);
//        lockedGrid.setAdapter(gridViewAdapter);
//
//    }
//
//    private List<AppItem> getAllApps() {
//        List<AppItem> result = new ArrayList<>();
//
//        PackageManager pk = getPackageManager();
//
//        Intent intent = new Intent(Intent.ACTION_MAIN,null);
//
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> resolveInfoList = pk.queryIntentActivities(intent,0);
//
//        for (ResolveInfo resolveInfo:resolveInfoList)
//        {
//            ActivityInfo activityInfo = resolveInfo.activityInfo;
//            result.add(new AppItem(activityInfo.loadIcon(pk),activityInfo.loadLabel(pk).toString(),activityInfo.packageName));
//        }
//
//        return result;
//    }
//
//
//
//    private void initToolbar()
//    {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitleTextColor(Color.WHITE);
//
//        getSupportActionBar().setTitle("App List");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//    }
//

//
//    public void setPermission(View view) {
//        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//    }
//
//
//    @Override
//    protected void onResume() {
//        stopService(new Intent(MainActivity.this,menu_service.class));
//
//        file = new files();
//
//        file.writeFileOnInternalStorage(this,"Yes");
//
//        if(Utils.checkPermission(this))
//        {
//            layoutPermission.setVisibility(View.GONE);
//        }else
//        {
//            layoutPermission.setVisibility(View.VISIBLE);
//        }
//
//
//
//
//
//        super.onResume();
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        startService(new Intent(MainActivity.this, menu_service.class));
//        finish();
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onPause() {
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                file = new files();
//
//                file.writeFileOnInternalStorage(MainActivity.this,"NO");
//            }
//        }, 5000);
//
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                file = new files();
//
//                file.writeFileOnInternalStorage(MainActivity.this,"NO");
//            }
//        }, 5000);
//        super.onStop();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//
//            //Check if the permission is granted or not.
//            if (resultCode == RESULT_OK) {
//                layoutPermission = findViewById(R.id.layout_permission);
//
//                initNav();
//                clickListener();
//
//                startPaperdb();
//                initToolbar();
//                initView();
//            } else { //Permission is not available
//                Toast.makeText(this,
//                        "Draw over other app permission not available. Closing the application",
//                        Toast.LENGTH_SHORT).show();
//
//                finish();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//}
