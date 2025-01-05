package com.bloodcircle.app.Adapter.Others;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Model.Others.HelpLineItem;
import com.bloodcircle.app.R;

import java.util.ArrayList;

public class HelpLineRecyclerAdapter extends RecyclerView.Adapter<HelpLineRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<HelpLineItem> helpLineItems;

    public HelpLineRecyclerAdapter(Context context, ArrayList<HelpLineItem> helpLineItems) {
        this.mContext = context;
        this.helpLineItems = helpLineItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_helpline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HelpLineItem item = helpLineItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPhone.setText(item.getPhone());
        holder.rlCall.setOnClickListener(view -> {
            mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+item.getPhone())));
        });
    }

    @Override
    public int getItemCount() {
        return helpLineItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName, tvPhone;
        private final RelativeLayout rlCall;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            rlCall = itemView.findViewById(R.id.rl_call);
        }
    }

}
