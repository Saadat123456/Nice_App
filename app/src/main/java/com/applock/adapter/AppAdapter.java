package com.applock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applock.INTerface.AppOnClickListener;
import com.applock.MainActivity;
import com.applock.R;
import com.applock.ViewHolder.AppViewHolder;
import com.applock.model.AppItem;
import com.applock.utils.Utils;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {

    private Context mContext;
    private List<AppItem> appItemList;
    private Utils utils;

    public AppAdapter(Context mContext, List<AppItem> appItemList) {
        this.mContext = mContext;
        this.appItemList = appItemList;
        this.utils = new Utils(mContext);
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_apps,parent,false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppViewHolder holder, int position) {
        holder.name_app.setText(appItemList.get(position).getName());
        holder.icon_app.setImageDrawable(appItemList.get(position).getIcon());

        final String pk = appItemList.get(position).getPackageName();

        if(utils.isLock(pk))
        {
            holder.lock_app.setImageResource(R.drawable.ic_lock_outline_black_24dp);
        }else
        {
            holder.lock_app.setImageResource(R.drawable.ic_lock_open_black_24dp);
        }

        holder.setListener(new AppOnClickListener() {
            @Override
            public void selectApps(int pos) {
                if(utils.isLock(pk))
                {
                    holder.lock_app.setImageResource(R.drawable.ic_lock_open_black_24dp);
                    utils.unlock(pk);
                }else
                {
                    holder.lock_app.setImageResource(R.drawable.ic_lock_outline_black_24dp);
                    utils.lock(pk);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }
}
