package com.bloodcircle.app.Activity.Donors;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tashila.pleasewait.PleaseWaitDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DonorProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvBloodGroup, tvLastDate, tvGender, tvDistrict, tvAddress, tvNote;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        ivProfile = findViewById(R.id.iv_profile);

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvBloodGroup = findViewById(R.id.tv_blood_group);
        tvLastDate = findViewById(R.id.tv_last_date);
        tvGender = findViewById(R.id.tv_gender);
        tvDistrict = findViewById(R.id.tv_district);
        tvAddress = findViewById(R.id.tv_address);
        tvNote = findViewById(R.id.tv_note);

        getUserData(getIntent().getStringExtra("id"));

        findViewById(R.id.iv_report).setOnClickListener(view -> {
            if (!tvName.getText().toString().isEmpty()) reportDialog();
        });
    }

    private int reason = 0;
    private void reportDialog() {
        PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(this);
        pleaseWaitDialog.setMessage(getString(R.string.submitting));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_report_user, null);
        builder.setView(v);

        TextInputLayout etMessage = v.findViewById(R.id.et_message);

        AutoCompleteTextView acReasons = v.findViewById(R.id.ac_reasons);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.report_reasons));
        acReasons.setAdapter(adapter);
        acReasons.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 4) {
                etMessage.setVisibility(View.VISIBLE);
            } else {
                etMessage.setVisibility(View.GONE);
            }
            reason = position;
        });

        AlertDialog dialog = builder.show();
        v.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        v.findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if (acReasons.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.select_reason, Toast.LENGTH_SHORT).show();
            } else if (etMessage.getVisibility() == View.VISIBLE && Objects.requireNonNull(etMessage.getEditText()).getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.enter_message, Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                pleaseWaitDialog.show();

                Map<String, Object> note = new HashMap<>();
                note.put("reporter_email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                note.put("donor_email", tvEmail.getText().toString());
                note.put("reason", reason);
                if (etMessage.getVisibility() == View.VISIBLE)
                    note.put("message",  Objects.requireNonNull(etMessage.getEditText()).getText().toString());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("reports").document()
                        .set(note)
                        .addOnSuccessListener(documentReference -> {
                            pleaseWaitDialog.dismiss();
                            Toast.makeText(this, R.string.report_sent, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            pleaseWaitDialog.dismiss();
                            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void getUserData(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phone = document.getString("phone");
                            int gender = Objects.requireNonNull(document.getDouble("gender")).intValue();
                            int bloodGroup = Objects.requireNonNull(document.getDouble("blood_group")).intValue();
                            int district = Objects.requireNonNull(document.getDouble("district")).intValue();

                            String date = document.getString("last_date");
                            String last_date =  String.format("%s", Objects.equals(date, "0") ? getString(R.string.new_donor) : date);

                            String address = document.getString("address");
                            String note = document.getString("note");
                            String imgUrl = document.getString("img_url");

                            tvName.setText(name);
                            tvEmail.setText(email);
                            tvPhone.setText(String.format("+88%s", phone));
                            tvLastDate.setText(String.format("%s • %s", getString(R.string.last_donated), last_date));
                            tvGender.setText(String.format("%s • %s", getString(R.string.gender), getResources().getStringArray(R.array.genders)[gender]));
                            tvAddress.setText(address);
                            tvNote.setText(note);
                            tvBloodGroup.setText(String.format(getResources().getString(R.string.blood_group)+" (%s)", getResources().getStringArray(R.array.blood_groups)[bloodGroup]));
                            tvDistrict.setText(String.format("%s • %s", getString(R.string.district), getResources().getStringArray(R.array.districts)[district]));

                            setView(email, phone, imgUrl);

                            findViewById(R.id.details_layout).setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                    findViewById(R.id.progress_circle).setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show());
    }

    private void setView(String email, String phone, String imgUrl) {
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ic_avatar)
                .circleCrop()
                .into(ivProfile);

        findViewById(R.id.iv_call).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+88"+phone))));

        findViewById(R.id.iv_email).setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")));
            }
        });

        findViewById(R.id.iv_whatsapp).setOnClickListener(view -> {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("whatsapp://send?phone="+ "+880"+phone +"&text="));
                startActivity(i);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);
    }
}