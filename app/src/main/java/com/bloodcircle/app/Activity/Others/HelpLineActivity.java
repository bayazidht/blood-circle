package com.bloodcircle.app.Activity.Others;

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

import com.bloodcircle.app.Adapter.Others.HelpLineRecyclerAdapter;
import com.bloodcircle.app.Model.Others.HelpLineItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HelpLineActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private HelpLineRecyclerAdapter adapter;
    private ArrayList<HelpLineItem> mHelpLineItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_line);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView = findViewById(R.id.help_line_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mHelpLineItems = new ArrayList<>();
        adapter = new HelpLineRecyclerAdapter(this, mHelpLineItems);
        mRecyclerView.setAdapter(adapter);

        loadHelpLineList();
    }

    private void loadHelpLineList() {
        db.collection("help_line")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");

                            mHelpLineItems.add(new HelpLineItem(name, phone));
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(HelpLineActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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