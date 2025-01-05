package com.bloodcircle.app.Adapter.Requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Dialog.RequestBottomSheetDialog;
import com.bloodcircle.app.Model.Requests.RequestsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;

import java.util.ArrayList;
import java.util.Locale;

public class RequestsRecyclerAdapter extends RecyclerView.Adapter<RequestsRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<RequestsItem> requestsItems;

    public RequestsRecyclerAdapter(Context context, ArrayList<RequestsItem> requestsItems, int layout) {
        this.mContext = context;
        this.requestsItems = requestsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_requests, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RequestsItem item = requestsItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvMedical.setText(item.getMedical());
        holder.tvPhone.setText(String.format("+88%s", item.getPhone()));
        holder.tvBloodGroup.setText(mContext.getResources().getStringArray(R.array.blood_groups)[item.getBloodGroup()]);
        holder.tvUnitType.setText(String.format(getLocal(), "%d "+mContext.getResources().getString(R.string.unit_bag)+" %s", Integer.parseInt(item.getUnit()), mContext.getResources().getStringArray(R.array.blood_types)[item.getType()]));
        holder.tvDateTime.setText(String.format(mContext.getString(R.string.donatin_date)+" - %s  %s", item.getDate(), item.getTime()));
        holder.tvDistrict.setText(String.format("%s", mContext.getResources().getStringArray(R.array.districts)[item.getDistrict()]));

        holder.llRequest.setOnClickListener(view -> {
            RequestBottomSheetDialog requestBottomSheetDialog =
                    new RequestBottomSheetDialog(
                            item.getName(),
                            item.getMedical(),
                            item.getPhone(),
                            item.getBloodGroup(),
                            item.getUnit(),
                            item.getType(),
                            item.getDate(),
                            item.getTime(),
                            item.getDistrict(),
                            item.getAddress(),
                            item.getDetails(),
                            item.getEmail(),
                            item.getUploadedTime()
                    );
            FragmentManager fragmentManager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
            requestBottomSheetDialog.show(fragmentManager, "RequestBottomSheetDialog");
        });
    }

    private Locale getLocal() {
        return Locale.forLanguageTag(new LocaleHelper(mContext).getLocal());
    }

    @Override
    public int getItemCount() {
        return requestsItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName, tvMedical, tvPhone, tvBloodGroup, tvUnitType, tvDateTime, tvDistrict;
        private final LinearLayout llRequest;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMedical = itemView.findViewById(R.id.tv_medical);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvUnitType = itemView.findViewById(R.id.tv_unit_type);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvDistrict = itemView.findViewById(R.id.tv_district);
            llRequest = itemView.findViewById(R.id.ll_request);
        }
    }

}
