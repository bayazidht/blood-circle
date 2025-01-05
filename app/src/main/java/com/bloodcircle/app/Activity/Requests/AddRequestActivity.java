package com.bloodcircle.app.Activity.Requests;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.Config;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddRequestActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private PleaseWaitDialog pleaseWaitDialog;

    private EditText etName, etMedical, etPhone, etUnit, etDate, etTime, etAddress, etDetails;
    private AutoCompleteTextView acBloodGroup, acDistrict;
    private int district, bloodGroup;
    private int blood_type=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new LocaleHelper(this).setAppLocale();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_request);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        pleaseWaitDialog = new PleaseWaitDialog(this);
        pleaseWaitDialog.setCancelable(false);

        etName = findViewById(R.id.et_name);
        etMedical = findViewById(R.id.et_medical);
        etPhone = findViewById(R.id.et_phone);
        etUnit = findViewById(R.id.et_units);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etAddress = findViewById(R.id.et_address);
        etDetails = findViewById(R.id.et_details);

        acBloodGroup = findViewById(R.id.ac_blood_group);
        acDistrict = findViewById(R.id.ac_district);

        RadioGroup rgBloodType = findViewById(R.id.rg_blood_type);
        rgBloodType.check(rgBloodType.getChildAt(0).getId());
        rgBloodType.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = radioGroup.findViewById(radioButtonId);
            blood_type = radioGroup.indexOfChild(radioButton);
        });

        setBloodGroup();
        setDistrict();
        setDate();
        setTime();

        findViewById(R.id.btn_upload).setOnClickListener(view -> {
            if (validData()) {
                uploadRequest();
            }
        });
    }

    private void uploadRequest() {
        pleaseWaitDialog.setMessage(getString(R.string.uploading));
        pleaseWaitDialog.show();

        Map<String, Object> note = new HashMap<>();
        note.put("name", etName.getText().toString());
        note.put("medical", etMedical.getText().toString());
        note.put("phone", etPhone.getText().toString());
        note.put("blood_group", bloodGroup);
        note.put("unit", etUnit.getText().toString());
        note.put("type", blood_type);
        note.put("date", etDate.getText().toString());
        note.put("time", etTime.getText().toString());
        note.put("district", district);
        note.put("address", etAddress.getText().toString());
        note.put("details", etDetails.getText().toString());
        note.put("email", currentUser.getEmail());
        note.put("uploaded_time", new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault()).format(new Date()));

        db.collection("blood_requests").document()
                .set(note)
                .addOnSuccessListener(documentReference -> {
                    pleaseWaitDialog.dismiss();
                    startActivity(new Intent(AddRequestActivity.this, MyRequestsActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.something_went_wrong));
                    pleaseWaitDialog.dismiss();
                });
    }

    private boolean validData() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError(getString(R.string.enter_name));
            etName.requestFocus();
        } else if (TextUtils.isEmpty(etMedical.getText().toString())) {
            etMedical.setError(getString(R.string.enter_medical_name));
            etMedical.requestFocus();
        } else if (!android.util.Patterns.PHONE.matcher(etPhone.getText().toString()).matches()) {
            etPhone.setError(getString(R.string.enter_phone_number));
            etPhone.requestFocus();
        } else if (TextUtils.isEmpty(acBloodGroup.getText().toString()) || bloodGroup == 0) {
            acBloodGroup.setError(getString(R.string.select_blood_group));
            acBloodGroup.requestFocus();
        } else if (TextUtils.isEmpty(etUnit.getText().toString())) {
            etUnit.setError(getString(R.string.enter_unit_bag));
            etUnit.requestFocus();
        } else if (TextUtils.isEmpty(etDate.getText().toString())) {
            etDate.setError(getString(R.string.select_date));
            showMessage(getString(R.string.select_date));
        } else if (TextUtils.isEmpty(etTime.getText().toString())) {
            etTime.setError(getString(R.string.select_time));
            showMessage(getString(R.string.select_time));
        } else if (TextUtils.isEmpty(acDistrict.getText().toString()) || district == 0) {
            acDistrict.setError(getString(R.string.select_district));
            acDistrict.requestFocus();
        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            etAddress.setError(getString(R.string.enter_address));
            etAddress.requestFocus();
        } else if (TextUtils.isEmpty(etDetails.getText().toString())) {
            etDetails.setError(getString(R.string.enter_patient_details));
            etDetails.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    private void setBloodGroup() {
        acBloodGroup = findViewById(R.id.ac_blood_group);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.blood_groups));
        acBloodGroup.setAdapter(adapter);
        acBloodGroup.setOnItemClickListener((parent, view, position, id) -> {
            bloodGroup = position;
            acBloodGroup.setError(null);
        });
    }

    private void setDistrict() {
        acDistrict = findViewById(R.id.ac_district);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.districts));
        acDistrict.setAdapter(adapter);
        acDistrict.setOnItemClickListener((parent, view, position, id) -> {
            district = position;
            acDistrict.setError(null);
        });
    }

    private void setDate() {
        etDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault()).format(new Date(selection));
                etDate.setText(date);
                etDate.setError(null);
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }

    private void setTime() {
        etTime.setOnClickListener(view -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTitleText(R.string.select_time)
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(10)
                    .setMinute(0)
                    .build();
            timePicker.addOnPositiveButtonClickListener(selection -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String time =  ((hour > 12) ? hour % 12 : hour) + ":"
                        + (minute < 10 ? ("0" + minute) : minute) + " " + ((hour >= 12) ? "PM" : "AM");
                etTime.setText(time);
                etTime.setError(null);
            });
            timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);
    }

}
