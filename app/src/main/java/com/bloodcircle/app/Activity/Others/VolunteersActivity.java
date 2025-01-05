package com.bloodcircle.app.Activity.Others;

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
import com.bloodcircle.app.Adapter.Others.VolunteersRecyclerAdapter;
import com.bloodcircle.app.Model.Others.VolunteersItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class VolunteersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private VolunteersRecyclerAdapter mAdapter;
    private ArrayList<VolunteersItem> mVolunteersItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_become_volunteer).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });

        SharedPreferences sharedPref = getSharedPreferences("BLOOD_CIRCLE", MODE_PRIVATE);
        if (!sharedPref.getBoolean("volunteer", false)) findViewById(R.id.btn_become_volunteer).setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView = findViewById(R.id.volunteers_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mVolunteersItems = new ArrayList<>();
        mAdapter = new VolunteersRecyclerAdapter(this, mVolunteersItems);
        mRecyclerView.setAdapter(mAdapter);

        String[] districts = getResources().getStringArray(R.array.districts);

        Spinner spDistricts = findViewById(R.id.sp_districts);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spDistricts.setAdapter(adapter);
        spDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadVolunteersList(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadVolunteersList(int dist) {
        findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        mVolunteersItems.clear();
        mAdapter.notifyDataSetChanged();

        Query query = db.collection("users");
        query = query.whereEqualTo("volunteer", true);

        if (dist != 0) {
            query = query.whereEqualTo("district", dist);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            int bloodGroup = Objects.requireNonNull(document.getDouble("blood_group")).intValue();
                            int district = Objects.requireNonNull(document.getDouble("district")).intValue();
                            String address = document.getString("address");

                            mVolunteersItems.add(new VolunteersItem(name, phone, bloodGroup, district, address));
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mVolunteersItems.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        else findViewById(R.id.empty_view).setVisibility(View.GONE);
                    } else {
                        Toast.makeText(VolunteersActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    findViewById(R.id.progress_circle).setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        new NetworkHelper(this);
    }
}