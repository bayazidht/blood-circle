package com.bloodcircle.app.Adapter.Others;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Model.Others.OthersItem;
import com.bloodcircle.app.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OthersRecyclerAdapter extends RecyclerView.Adapter<OthersRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<OthersItem> othersItems;

    public OthersRecyclerAdapter(Context context, ArrayList<OthersItem> othersItems) {
        this.mContext = context;
        this.othersItems = othersItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_others, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OthersItem item = othersItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPhone.setText(String.format("+88%s", item.getPhone()));
        holder.tvAddress.setText(item.getAddress());
        holder.tvDistrict.setText(String.format("%s", mContext.getResources().getStringArray(R.array.districts)[item.getDistrict()]));

        holder.btnCall.setOnClickListener(view -> mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+88"+item.getPhone()))));
        holder.btnMessage.setOnClickListener(view -> {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("whatsapp://send?phone="+ "+880"+item.getPhone() +"&text="));
                mContext.startActivity(i);
            } catch (Exception e) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
            }
        });
        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                .into(holder.ivImg);
    }

    @Override
    public int getItemCount() {
        return othersItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName, tvPhone, tvAddress, tvDistrict;
        private final ImageView ivImg;
        private final Button btnCall, btnMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDistrict = itemView.findViewById(R.id.tv_district);
            ivImg = itemView.findViewById(R.id.iv_img);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnMessage = itemView.findViewById(R.id.btn_message);
        }
    }

}
