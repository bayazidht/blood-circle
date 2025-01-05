package com.bloodcircle.app.Adapter.Requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Model.Requests.MyRequestsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.ArrayList;
import java.util.Locale;

public class MyRequestsRecyclerAdapter extends RecyclerView.Adapter<MyRequestsRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<MyRequestsItem> myRequestsItems;

    public MyRequestsRecyclerAdapter(Context context, ArrayList<MyRequestsItem> myRequestsItems, int layout) {
        this.mContext = context;
        this.myRequestsItems = myRequestsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyRequestsItem item = myRequestsItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvMedical.setText(item.getMedical());
        holder.tvPhone.setText(String.format("+88%s", item.getPhone()));
        holder.tvBloodGroup.setText(mContext.getResources().getStringArray(R.array.blood_groups)[item.getBloodGroup()]);
        holder.tvUnitType.setText(String.format(getLocal(), "%d "+mContext.getResources().getString(R.string.unit_bag)+" %s", Integer.parseInt(item.getUnit()), mContext.getResources().getStringArray(R.array.blood_types)[item.getType()]));
        holder.tvDateTime.setText(String.format("%s  %s", item.getDate(), item.getTime()));
        holder.tvAddress.setText(String.format("%s, %s", mContext.getResources().getStringArray(R.array.districts)[item.getDistrict()], item.getAddress()));
        holder.tvDetails.setText(item.getDetails());

        PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(mContext);
        pleaseWaitDialog.setMessage(mContext.getString(R.string.deleting));
        pleaseWaitDialog.setCancelable(false);

        holder.ivDelete.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(mContext)
                    .setTitle(R.string.delete_request)
                    .setIcon(R.drawable.ic_delete)
                    .setMessage(R.string.delete_request_message)
                    .setPositiveButton(R.string.yes_delete, (dialog, which) -> {
                        pleaseWaitDialog.show();
                        FirebaseFirestore.getInstance().collection("blood_requests").document(item.getId())
                                .delete()
                                .addOnSuccessListener(documentReference -> {
                                    myRequestsItems.remove(position);
                                    notifyDataSetChanged();
                                    pleaseWaitDialog.dismiss();
                                })
                                .addOnFailureListener(e -> {});
                    })
                    .setNegativeButton(R.string.no_cancel, null)
                    .show();
        });
    }

    private Locale getLocal() {
        return Locale.forLanguageTag(new LocaleHelper(mContext).getLocal());
    }

    @Override
    public int getItemCount() {
        return myRequestsItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName, tvMedical, tvPhone, tvBloodGroup, tvUnitType, tvDateTime, tvAddress, tvDetails;
        private final ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMedical = itemView.findViewById(R.id.tv_medical);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvUnitType = itemView.findViewById(R.id.tv_unit_type);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDetails = itemView.findViewById(R.id.tv_details);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

}
