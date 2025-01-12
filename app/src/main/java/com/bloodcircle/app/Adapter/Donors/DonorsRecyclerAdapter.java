package com.bloodcircle.app.Adapter.Donors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Activity.Donors.DonorProfileActivity;
import com.bloodcircle.app.Model.Donors.DonorsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.Config;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bumptech.glide.Glide;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DonorsRecyclerAdapter extends RecyclerView.Adapter<DonorsRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<DonorsItem> donorsItem;

    public DonorsRecyclerAdapter(Context context, ArrayList<DonorsItem> donorsItem) {
        this.mContext = context;
        this.donorsItem = donorsItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_donors, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DonorsItem item = donorsItem.get(position);
        holder.tvBloodGroup.setText(mContext.getResources().getStringArray(R.array.blood_groups)[item.getBloodGroup()]);
        holder.tvName.setText(item.getName());
        holder.tvPhone.setText(String.format("+88%s", item.getPhone()));
        holder.tvLastDate.setText(String.format(mContext.getString(R.string.last_donated)+" - %s", Objects.equals(item.getLast_date(), "0") ? mContext.getString(R.string.new_donor) : item.getLast_date()));
        holder.tvDistrict.setText(String.format("%s", mContext.getResources().getStringArray(R.array.districts)[item.getDistrict()]));
        holder.tvAddress.setText(String.format("%s", item.getAddress()));

        Glide.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.ic_avatar)
                .circleCrop()
                .into(holder.ivImg);

        PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(mContext);
        pleaseWaitDialog.setCancelable(false);

        if (!item.getLast_date().equals("0")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault());

                Date currentDate = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                Date lastDate = sdf.parse(item.last_date);

                long startDate = currentDate != null ? currentDate.getTime() : 0;
                long endDate = lastDate != null ? lastDate.getTime() : 0;

                long diff = startDate - endDate;
                long days = 120-(diff/(24*60*60*1000));

                if (days>0) {
                    holder.tvStatus.setText(String.format(getLocal(),"%d %s", days, mContext.getString(R.string.days_remaining)));
                    holder.tvStatus.setTextColor(Color.parseColor("#EA4738"));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        holder.llDonor.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DonorProfileActivity.class);
            intent.putExtra("id", item.getId());
            mContext.startActivity(intent);
        });
    }

    private Locale getLocal() {
        return Locale.forLanguageTag(new LocaleHelper(mContext).getLocal());
    }

    @Override
    public int getItemCount() {
        return donorsItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvBloodGroup, tvName, tvPhone, tvStatus, tvLastDate, tvDistrict, tvAddress;
        private final LinearLayout llDonor;
        private final ImageView ivImg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvLastDate = itemView.findViewById(R.id.tv_last_date);
            tvDistrict = itemView.findViewById(R.id.tv_district);
            tvAddress = itemView.findViewById(R.id.tv_address);
            llDonor = itemView.findViewById(R.id.ll_donor);
            ivImg = itemView.findViewById(R.id.iv_img);
        }
    }

}
