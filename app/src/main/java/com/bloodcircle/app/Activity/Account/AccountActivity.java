package com.bloodcircle.app.Activity.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bloodcircle.app.Activity.Requests.MyRequestsActivity;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tashila.pleasewait.PleaseWaitDialog;

public class AccountActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private PleaseWaitDialog pleaseWaitDialog;

    private SharedPreferences sharedPref;
    private MaterialSwitch donorSwitch, volunteerSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null) loadProfile();

        sharedPref = getSharedPreferences("BLOOD_CIRCLE", Context.MODE_PRIVATE);

        donorSwitch = findViewById(R.id.switch_become_donor);
        volunteerSwitch = findViewById(R.id.switch_become_volunteer);
        setDonorData();
        setVolunteerData();

        pleaseWaitDialog = new PleaseWaitDialog(this);
        pleaseWaitDialog.setCancelable(false);

        findViewById(R.id.iv_logout).setOnClickListener(view ->
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.ic_logout)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.logout_message)
                        .setPositiveButton(R.string.yes_logout, (dialog, which) -> logout())
                        .setNegativeButton(R.string.no_cancel, null)
                        .show()
        );
        findViewById(R.id.tv_profile).setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, ProfileActivity.class)));
        findViewById(R.id.tv_my_requests).setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, MyRequestsActivity.class)));
        findViewById(R.id.btn_delete_account).setOnClickListener(view -> deleteAccount());
    }

    private void loadProfile() {
        TextView tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserEmail.setText(currentUser.getEmail());

        ImageView ivProfile = findViewById(R.id.iv_profile);
        Uri uri = currentUser.getPhotoUrl();
        Glide.with(this).load(uri).into(ivProfile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);

        TextView tvUserName = findViewById(R.id.tv_user_name);
        tvUserName.setText(sharedPref.getString("user_name", currentUser.getDisplayName()));

        donorSwitch.setChecked(sharedPref.getBoolean("donate", false));
        volunteerSwitch.setChecked(sharedPref.getBoolean("volunteer", false));
    }

    private void setDonorData() {
        donorSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (sharedPref.getBoolean("is_profile_complete", false)) {
                db.document("users/" + currentUser.getEmail())
                        .update("donate", b)
                        .addOnSuccessListener(documentReference -> {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("donate", b);
                            editor.apply();
                        })
                        .addOnFailureListener(e -> showMessage(getString(R.string.something_went_wrong)));
            } else {
                donorSwitch.setChecked(false);
                Toast.makeText(this, R.string.profile_complete, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });
    }

    private void setVolunteerData() {
        volunteerSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (sharedPref.getBoolean("is_profile_complete", false)) {
                db.document("users/" + currentUser.getEmail())
                        .update("volunteer", b)
                        .addOnSuccessListener(documentReference -> {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("volunteer", b);
                            editor.apply();
                        })
                        .addOnFailureListener(e -> showMessage(getString(R.string.something_went_wrong)));
            } else {
                volunteerSwitch.setChecked(false);
                Toast.makeText(this, R.string.profile_complete, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });
    }

    private void logout() {
        sharedPref.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void deleteAccount() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.delete_account))
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.delete_account_message)
                .setNegativeButton(R.string.yes_delete, (dialog, which) ->
                        new MaterialAlertDialogBuilder(this)
                                .setMessage(R.string.delete_account_message)
                                .setNegativeButton(R.string.yes_delete, (dialog1, which1) -> {
                                    pleaseWaitDialog.setMessage(getString(R.string.deleting));
                                    pleaseWaitDialog.show();
                                    if (currentUser != null) {
                                        deleteDonors();
                                    } else pleaseWaitDialog.dismiss();
                                })
                                .setPositiveButton(R.string.no_cancel, null)
                                .show()
                )
                .setPositiveButton(R.string.no_cancel, null)
                .show();
    }
    private void deleteDonors() {
        db.collection("donors_list")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        deleteBloodRequests();
                    } else {
                        showMessage(getString(R.string.something_went_wrong));
                    }
                })
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.something_went_wrong));
                    pleaseWaitDialog.dismiss();
                });
    }
    private void deleteBloodRequests() {
        db.collection("blood_requests")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        deleteUserDate();
                    } else {
                        showMessage(getString(R.string.something_went_wrong));
                    }
                })
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.something_went_wrong));
                    pleaseWaitDialog.dismiss();
                });
    }
    private void deleteUserDate() {
        db.document("users/"+currentUser.getEmail())
                .delete()
                .addOnSuccessListener(documentReference2 -> currentUser.delete().addOnCompleteListener(task -> logout()))
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.something_went_wrong));
                    pleaseWaitDialog.dismiss();
                });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}