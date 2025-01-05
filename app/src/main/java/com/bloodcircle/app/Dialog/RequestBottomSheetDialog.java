package com.bloodcircle.app.Dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.bloodcircle.app.Activity.Donors.DonorProfileActivity;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.Config;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RequestBottomSheetDialog extends BottomSheetDialogFragment {

    public RequestBottomSheetDialog(String name, String medical, String phone, int bloodGroup, String unit, int type, String date, String time, int district, String address, String details, String email, String uploadedTime) {
        this.name = name;
        this.medical = medical;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.unit = unit;
        this.type = type;
        this.date = date;
        this.time = time;
        this.district = district;
        this.address = address;
        this.details = details;
        this.email = email;
        this.uploadedTime = uploadedTime;
    }

    public String name;
    public String medical;
    public String phone;
    public int bloodGroup;
    public String unit;
    public int type;
    public String date;
    public String time;
    public int district;
    public String address;
    public String details;
    public String email;
    public String uploadedTime;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet, container, false);

        TextView tv_name = v.findViewById(R.id.tv_name);
        TextView tv_medical = v.findViewById(R.id.tv_medical);
        TextView tv_phone = v.findViewById(R.id.tv_phone);
        TextView tv_blood_group = v.findViewById(R.id.tv_blood_group);
        TextView tv_unit_type = v.findViewById(R.id.tv_unit_type);
        TextView tv_date_time = v.findViewById(R.id.tv_date_time);
        TextView tv_district = v.findViewById(R.id.tv_district);
        TextView tv_address = v.findViewById(R.id.tv_address);
        TextView tv_details = v.findViewById(R.id.tv_details);
        TextView tv_by = v.findViewById(R.id.tv_by);
        TextView tv_uploaded_time = v.findViewById(R.id.tv_uploaded_time);

        tv_name.setText(name);
        tv_medical.setText(medical);
        tv_phone.setText(String.format("+88%s", phone));
        tv_blood_group.setText(String.format(requireContext().getResources().getString(R.string.blood_group)+" (%s)", requireContext().getResources().getStringArray(R.array.blood_groups)[bloodGroup]));
        tv_unit_type.setText(String.format(getLocal(), "%d "+requireContext().getResources().getString(R.string.unit_bag)+" %s",
                Integer.parseInt(unit), requireContext().getResources().getStringArray(R.array.blood_types)[type]));
        tv_date_time.setText(String.format(requireContext().getString(R.string.donatin_date)+" - %s  %s", date, time));
        tv_district.setText(String.format("%s", requireContext().getResources().getStringArray(R.array.districts)[district]));
        tv_address.setText(address);
        tv_details.setText(details);
        tv_by.setText(String.format(getString(R.string.uploaded_by)+" %s", email.split("@")[0]));
        tv_uploaded_time.setText(String.format("%s", uploadedTime));

        tv_by.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), DonorProfileActivity.class);
            intent.putExtra("id", email);
            startActivity(intent);
        });

        v.findViewById(R.id.btn_call).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+88"+phone))));
        v.findViewById(R.id.btn_message).setOnClickListener(view -> {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("whatsapp://send?phone="+ "+880"+phone +"&text="));
                startActivity(i);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
            }
        });

        return v;
    }

    private Locale getLocal() {
        return Locale.forLanguageTag(new LocaleHelper(requireContext()).getLocal());
    }
}
