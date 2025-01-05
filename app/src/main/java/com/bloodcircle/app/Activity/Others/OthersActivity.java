package com.bloodcircle.app.Activity.Others;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloodcircle.app.Adapter.Others.OthersRecyclerAdapter;
import com.bloodcircle.app.Model.Others.OthersItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class OthersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private OthersRecyclerAdapter mAdapter;
    private ArrayList<OthersItem> mOthersItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_others);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String collection_name = getIntent().getStringExtra("collection_name");
        String title = getIntent().getStringExtra("title");
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(title);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add).setOnClickListener(v -> addNew(title));
        findViewById(R.id.iv_add).setOnClickListener(v -> addNew(title));

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView = findViewById(R.id.items_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mOthersItems = new ArrayList<>();
        mAdapter = new OthersRecyclerAdapter(this, mOthersItems);
        mRecyclerView.setAdapter(mAdapter);

        String[] districts = getResources().getStringArray(R.array.districts);

        Spinner spDistricts = findViewById(R.id.sp_districts);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spDistricts.setAdapter(adapter);
        spDistricts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadAmbulanceList(i, collection_name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void addNew(String title) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.name)+":\n"+getString(R.string.phone)+":\n"+getString(R.string.address)+":\n"+getString(R.string.picture)+":\n");
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")));
        }
    }

    private void loadAmbulanceList(int dist, String collection_name) {
        findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        mOthersItems.clear();
        mAdapter.notifyDataSetChanged();

        Query query = db.collection(collection_name);
        if (dist != 0) {
            query = query.whereEqualTo("district", dist);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            int district = Objects.requireNonNull(document.getDouble("district")).intValue();
                            String address = document.getString("address");
                            String imgUrl = document.getString("imageUrl");

                            mOthersItems.add(new OthersItem(name, phone, district, address, imgUrl));
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mOthersItems.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        else findViewById(R.id.empty_view).setVisibility(View.GONE);
                    } else {
                        Toast.makeText(OthersActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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