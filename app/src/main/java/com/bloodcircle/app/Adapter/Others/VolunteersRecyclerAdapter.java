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

import com.bloodcircle.app.Model.Others.VolunteersItem;
import com.bloodcircle.app.R;

import java.util.ArrayList;

public class VolunteersRecyclerAdapter extends RecyclerView.Adapter<VolunteersRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<VolunteersItem> volunteersItems;

    public VolunteersRecyclerAdapter(Context context, ArrayList<VolunteersItem> volunteersItems) {
        this.mContext = context;
        this.volunteersItems = volunteersItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_volunteers, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VolunteersItem item = volunteersItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPhone.setText(String.format("+88%s", item.getPhone()));
        holder.tvBloodGroup.setText(mContext.getResources().getStringArray(R.array.blood_groups)[item.getBloodGroup()]);
        holder.tvAddress.setText(String.format("%s, %s", mContext.getResources().getStringArray(R.array.districts)[item.getDistrict()], item.getAddress()));

        holder.rlCall.setOnClickListener(view -> {
            mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+88"+item.getPhone())));
        });
    }

    @Override
    public int getItemCount() {
        return volunteersItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName, tvPhone, tvBloodGroup, tvAddress;
        private final RelativeLayout rlCall;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvAddress = itemView.findViewById(R.id.tv_address);
            rlCall = itemView.findViewById(R.id.rl_call);
        }
    }

}
