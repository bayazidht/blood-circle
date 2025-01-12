package com.bloodcircle.app.Activity.Donors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Activity.Account.AccountActivity;
import com.bloodcircle.app.Adapter.Donors.DonorsRecyclerAdapter;
import com.bloodcircle.app.Model.Donors.DonorsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class DonorsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DonorsRecyclerAdapter mAdapter;
    private ArrayList<DonorsItem> mDonorsList;

    private int district, blood = 0;
    private boolean hasInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donors);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        SharedPreferences sharedPref = getSharedPreferences("BLOOD_CIRCLE", MODE_PRIVATE);
        if (!sharedPref.getBoolean("donate", false)) findViewById(R.id.btn_become_donor).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_become_donor).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView =  findViewById(R.id.donors_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mDonorsList = new ArrayList<>();
        mAdapter = new DonorsRecyclerAdapter(this, mDonorsList);
        mRecyclerView.setAdapter(mAdapter);

        String[] districts = getResources().getStringArray(R.array.districts);
        String[] bloods = getResources().getStringArray(R.array.blood_groups);

        blood = getIntent().getIntExtra("data", 0);

        Spinner spDistricts = findViewById(R.id.sp_districts);
        ArrayAdapter<String> distAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        distAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spDistricts.setAdapter(distAdapter);
        spDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDonorsList(blood, i);
                district = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Spinner spBloods = findViewById(R.id.sp_bloods);
        ArrayAdapter<String> bldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloods);
        bldAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spBloods.setAdapter(bldAdapter);
        if (getIntent().getExtras() != null) spBloods.setSelection(blood);
        spBloods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (hasInit) loadDonorsList(i, district);
                blood = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loadDonorsList(int group, int dist) {
        findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        mDonorsList.clear();
        mAdapter.notifyDataSetChanged();

        Query query = db.collection("users");
        query = query.whereEqualTo("donate", true);

        if (dist != 0) {
            query = query.whereEqualTo("district", dist);
        }
        if (group != 0) {
            query = query.whereEqualTo("blood_group", group);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String last_date = document.getString("last_date");
                            int bloodGroup = Objects.requireNonNull(document.getDouble("blood_group")).intValue();
                            int district = Objects.requireNonNull(document.getDouble("district")).intValue();
                            String address = document.getString("address");
                            String imgUrl = document.getString("img_url");

                            mDonorsList.add(new DonorsItem(id, name, phone, last_date, bloodGroup, district, address, imgUrl));
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mDonorsList.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    findViewById(R.id.progress_circle).setVisibility(View.GONE);
                    hasInit = true;
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);
    }
}