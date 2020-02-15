package com.applock.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.applock.R;
import com.applock.dealFiles.files;
import com.applock.model.AppItem;

import java.util.List;



public class gridviewAdapter extends BaseAdapter {

    Context context;
    List<AppItem> appItemList;

    public gridviewAdapter(Context ctx,List<AppItem> appItemList)
    {
        this.appItemList = appItemList;
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return appItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return appItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.appstapgrid,parent,false);

        ImageView imageView=convertView.findViewById(R.id.app_Icon_click);
        imageView.setImageDrawable(appItemList.get(position).getIcon());

        TextView textView = convertView.findViewById(R.id.app_name_click);
        textView.setText(appItemList.get(position).getName());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                files fil = new files();
                fil.writeFileOnInternalStorage(context,"Yes");
                String pkg = appItemList.get(position).getPackageName();
                Intent i = context.getPackageManager().getLaunchIntentForPackage(pkg);
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
