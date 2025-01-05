package com.bloodcircle.app.Activity.Requests;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Adapter.Requests.MyRequestsRecyclerAdapter;
import com.bloodcircle.app.Model.Requests.MyRequestsItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private MyRequestsRecyclerAdapter mAdapter;
    private ArrayList<MyRequestsItem> myRequestsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView mRecyclerView = findViewById(R.id.my_requests_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        myRequestsItems = new ArrayList<>();
        mAdapter = new MyRequestsRecyclerAdapter(this, myRequestsItems, 0);
        mRecyclerView.setAdapter(mAdapter);

        getMyRequests();
    }

    private void getMyRequests() {
        myRequestsItems.clear();
        mAdapter.notifyDataSetChanged();

        db.collection("blood_requests")
                .whereEqualTo("email", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
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

                            myRequestsItems.add(new MyRequestsItem(id, name, medical, phone, bloodGroup, unit, type, date, time, district, address, details));
                            mAdapter.notifyDataSetChanged();
                        }
                        if (myRequestsItems.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        else findViewById(R.id.empty_view).setVisibility(View.GONE);
                    } else {
                        showMessage(getString(R.string.something_went_wrong));
                    }
                    findViewById(R.id.progress_circle).setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());
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