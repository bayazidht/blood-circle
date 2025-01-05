package com.bloodcircle.app.Activity.Requests;

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

import com.bloodcircle.app.Adapter.Requests.RequestsRecyclerAdapter;
import com.bloodcircle.app.Model.Requests.RequestsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RequestsRecyclerAdapter mAdapter;
    private ArrayList<RequestsItem> mRequestsItems;

    private int district, blood = 0;
    private boolean hasInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView = findViewById(R.id.recent_requests_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRequestsItems = new ArrayList<>();
        mAdapter = new RequestsRecyclerAdapter(this, mRequestsItems, 1);
        mRecyclerView.setAdapter(mAdapter);

        String[] districts = getResources().getStringArray(R.array.districts);
        String[] bloods = getResources().getStringArray(R.array.blood_groups);

        Spinner spDistricts = findViewById(R.id.sp_districts);
        ArrayAdapter<String> distAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        distAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spDistricts.setAdapter(distAdapter);
        spDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getMyRequests(blood, i);
                district = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Spinner spBloods = findViewById(R.id.sp_bloods);
        ArrayAdapter<String> bldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloods);
        bldAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spBloods.setAdapter(bldAdapter);
        spBloods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (hasInit) getMyRequests(i, district);
                blood = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void getMyRequests(int group, int dist) {
        findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        mRequestsItems.clear();
        mAdapter.notifyDataSetChanged();

        Query query = db.collection("blood_requests");

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
                            String name = document.getString("name");
                            String medical = document.getString("medical");
                            String phone = document.getString("phone");
                            int bloodGroup = Objects.requireNonNull(document.getDouble("blood_group")).intValue();
                            String unit = document.getString("unit");
                            int type = Objects.requireNonNull(document.getDouble("type")).intValue();
                            String date = document.getString("date");
                            String time = document.getString("time");
                            int district = Objects.requireNonNull(document.getDouble("district")).intValue();
                            String address = document.getString("address");
                            String details = document.getString("details");
                            String email = document.getString("email");
                            String uploadedTime = document.getString("uploaded_time");

                            mRequestsItems.add(new RequestsItem(name, medical, phone, bloodGroup, unit, type, date, time, district, address, details, email, uploadedTime));
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mRequestsItems.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
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
