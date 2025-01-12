package com.bloodcircle.app.Activity.Account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.Config;
import com.bloodcircle.app.Tools.GpsHelper;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPref;

    private String name, email, phone, last_date, address, note;
    private int gender, bloodGroup, district;
    private TextView tvName, tvEmail, tvPhone, tvGender, tvBloodGroup, tvLastDate, tvDistrict, tvAddress, tvNote;

    private EditText etName, etEmail, etPhone, etLastDate, etAddress, etNote;
    private AutoCompleteTextView acGender, acBloodGroup, acDistrict;

    private CheckBox cbNew;

    private PleaseWaitDialog pleaseWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        sharedPref = getSharedPreferences("BLOOD_CIRCLE", MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageView ivProfile = findViewById(R.id.iv_profile);
        Uri uri = currentUser.getPhotoUrl();
        Glide.with(this).load(uri).into(ivProfile);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etLastDate = findViewById(R.id.et_last_date);
        etAddress = findViewById(R.id.et_address);
        etNote = findViewById(R.id.et_note);

        acBloodGroup = findViewById(R.id.ac_blood_group);
        acGender = findViewById(R.id.ac_gender);
        acDistrict = findViewById(R.id.ac_district);

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvBloodGroup = findViewById(R.id.tv_blood_group);
        tvGender = findViewById(R.id.tv_gender);
        tvLastDate = findViewById(R.id.tv_last_date);
        tvDistrict = findViewById(R.id.tv_district);
        tvAddress = findViewById(R.id.tv_address);
        tvNote = findViewById(R.id.tv_note);

        cbNew = findViewById(R.id.cb_new);

        pleaseWaitDialog = new PleaseWaitDialog(this);
        pleaseWaitDialog.setCancelable(false);

        findViewById(R.id.ti_date).setEnabled(!cbNew.isActivated());
        cbNew.setOnCheckedChangeListener((compoundButton, b) -> {
            findViewById(R.id.ti_date).setEnabled(!b);
            etLastDate.setText(null);
        });

        setGender();
        setBloodGroup();
        setDistrict();
        setLastDate();
        setAddress();

        getUserData();

        findViewById(R.id.btn_edit).setOnClickListener(view -> {
            findViewById(R.id.details_layout).setVisibility(View.GONE);
            findViewById(R.id.edit_layout).setVisibility(View.VISIBLE);
        });
        findViewById(R.id.btn_update).setOnClickListener(view -> {
           if (validData()) {
               updateUserData();
           }
        });
    }

    private void getUserData() {
        db.collection("users").document(Objects.requireNonNull(currentUser.getEmail()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.getString("name");
                            email = document.getString("email");
                            phone = document.getString("phone");
                            gender = Objects.requireNonNull(document.getDouble("gender")).intValue();
                            bloodGroup = Objects.requireNonNull(document.getDouble("blood_group")).intValue();
                            district = Objects.requireNonNull(document.getDouble("district")).intValue();

                            String date = document.getString("last_date");
                            last_date =  String.format("%s", Objects.equals(date, "0") ? getString(R.string.new_donor) : date);

                            address = document.getString("address");
                            note = document.getString("note");

                            tvName.setText(name);
                            tvEmail.setText(email);
                            tvPhone.setText(String.format("+88%s", phone));
                            tvLastDate.setText(last_date);
                            tvAddress.setText(address);
                            tvNote.setText(note);
                            tvBloodGroup.setText(getResources().getStringArray(R.array.blood_groups)[bloodGroup]);
                            tvGender.setText((getResources().getStringArray(R.array.genders)[gender]));
                            tvDistrict.setText(getResources().getStringArray(R.array.districts)[district]);

                            etName.setText(name);
                            etEmail.setText(email);
                            etPhone.setText(phone);
                            acGender.setThreshold(gender);
                            etLastDate.setText(last_date);
                            etAddress.setText(address);
                            etNote.setText(note);
                            acGender.setText(getResources().getStringArray(R.array.genders)[gender], false);
                            acBloodGroup.setText(getResources().getStringArray(R.array.blood_groups)[bloodGroup], false);
                            acDistrict.setText(getResources().getStringArray(R.array.districts)[district], false);

                            cbNew.setChecked(Objects.equals(document.getString("last_date"), "0"));

                            findViewById(R.id.details_layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_layout).setVisibility(View.GONE);
                        } else {
                            etName.setText(currentUser.getDisplayName());
                            etEmail.setText(currentUser.getEmail());

                            findViewById(R.id.details_layout).setVisibility(View.GONE);
                            findViewById(R.id.edit_layout).setVisibility(View.VISIBLE);
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong));
                    }
                    findViewById(R.id.progress_circle).setVisibility(View.GONE);
                    pleaseWaitDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.something_went_wrong));
                    pleaseWaitDialog.dismiss();
                });
    }

    private void updateUserData() {
        pleaseWaitDialog.setMessage(getString(R.string.updating));
        pleaseWaitDialog.show();

        Map<String, Object> note = new HashMap<>();
        note.put("name", etName.getText().toString());
        note.put("email", etEmail.getText().toString());
        note.put("phone", etPhone.getText().toString());
        note.put("gender", gender);
        note.put("blood_group", bloodGroup);
        note.put("last_date", cbNew.isChecked()? "0" : etLastDate.getText().toString());
        note.put("district", district);
        note.put("address", etAddress.getText().toString());
        note.put("note", etNote.getText().toString());
        note.put("img_url", Objects.requireNonNull(currentUser.getPhotoUrl()).toString());
        note.put("donate", sharedPref.getBoolean("donate", false));
        note.put("volunteer", sharedPref.getBoolean("volunteer", false));

        db.collection("users").document(Objects.requireNonNull(currentUser.getEmail()))
                .set(note)
                .addOnSuccessListener(documentReference -> {
                    getUserData();

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_name", etName.getText().toString());
                    editor.putBoolean("is_profile_complete", true);
                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    pleaseWaitDialog.dismiss();
                    showMessage(getString(R.string.something_went_wrong));
                });
    }

    private void setDistrict() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.districts));
        acDistrict.setAdapter(adapter);
        acDistrict.setOnItemClickListener((parent, view, position, id) -> {
            district = position;
            acDistrict.setError(null);
        });
    }

    private void setGender() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.genders));
        acGender.setAdapter(adapter);
        acGender.setOnItemClickListener((parent, view, position, id) -> {
            gender = position;
            acGender.setError(null);
        });
    }

    private void setBloodGroup() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.blood_groups));
        acBloodGroup.setAdapter(adapter);
        acBloodGroup.setOnItemClickListener((parent, view, position, id) -> {
            bloodGroup = position;
            acBloodGroup.setError(null);
        });
    }

    private void setLastDate() {
       etLastDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.select_date)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault()).format(new Date(selection));
                etLastDate.setText(date);
                etLastDate.setError(null);
            });
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }

    private boolean validData() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError(getString(R.string.enter_name));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError(getString(R.string.enter_valid_email));
        } else if (!Patterns.PHONE.matcher(etPhone.getText().toString()).matches()) {
            etPhone.setError(getString(R.string.enter_phone_number));
            etPhone.requestFocus();
        } else if (TextUtils.isEmpty(acGender.getText().toString())) {
            acGender.setError(getString(R.string.select_gender));
            acGender.requestFocus();
        } else if (TextUtils.isEmpty(acBloodGroup.getText().toString()) || bloodGroup == 0) {
            acBloodGroup.setError(getString(R.string.select_blood_group));
            acBloodGroup.requestFocus();
        } else if (TextUtils.isEmpty(etLastDate.getText().toString()) && !cbNew.isChecked()) {
            etLastDate.setError(getString(R.string.select_last_donation_date));
            etLastDate.requestFocus();
        } else if (TextUtils.isEmpty(acDistrict.getText().toString()) || district == 0) {
            acDistrict.setError(getString(R.string.select_district));
            acDistrict.requestFocus();
        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            etAddress.setError(getString(R.string.enter_address));
            etAddress.requestFocus();
        } else if (TextUtils.isEmpty(etNote.getText().toString())) {
            etNote.setError(getString(R.string.enter_note));
            etNote.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddress() {
        etAddress.setOnClickListener(view -> requestLocationPermissions());
    }

    private FusedLocationProviderClient fusedLocationProviderClient;
    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        pleaseWaitDialog.setMessage(getString(R.string.getting_your_location));
        pleaseWaitDialog.setCancelable(true);
        pleaseWaitDialog.show();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location!=null) setAddress(location);
                    else getCurrentLocation();
                })
                .addOnFailureListener(e -> showMessage(getString(R.string.something_went_wrong)));
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(10000)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                setAddress(location);
            }
        }, Looper.myLooper());
    }

    private void setAddress(Location location) {
        if (location != null) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {
                    String address = addresses.get(0).getAddressLine(0);
                    etAddress.setText(address);
                    pleaseWaitDialog.dismiss();
                }
            } catch (IOException e) {
                //Log.d("GEOCODER", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermissions() {
        locationPermissionRequest.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }
    ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);

        if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
            if (new GpsHelper(this).isGpsEnabled()) getLastLocation();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.allow_location_title)
                    .setMessage(R.string.allow_location_message)
                    .setIcon(R.drawable.ic_location)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.allow_location_title, (paramDialogInterface, paramInt) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .show();
        }
    });

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);
    }

}