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

import com.bloodcircle.app.Adapter.Others.FaqRecyclerAdapter;
import com.bloodcircle.app.Model.Others.FaqItem;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.NetworkHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FaqActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FaqRecyclerAdapter adapter;
    private ArrayList<FaqItem> mFaqItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        db = FirebaseFirestore.getInstance();

        RecyclerView mRecyclerView = findViewById(R.id.faq_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFaqItems = new ArrayList<>();
        adapter = new FaqRecyclerAdapter(this, mFaqItems);
        mRecyclerView.setAdapter(adapter);

        loadFaqList();
    }

    private void loadFaqList() {
        db.collection("faq")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("q");
                            String desc = document.getString("a");

                            mFaqItems.add(new FaqItem(title, desc));
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(FaqActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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