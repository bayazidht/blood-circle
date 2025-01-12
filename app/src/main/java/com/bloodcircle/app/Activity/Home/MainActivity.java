package com.bloodcircle.app.Activity.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bloodcircle.app.Activity.Account.AccountActivity;
import com.bloodcircle.app.Activity.Donors.DonorsActivity;
import com.bloodcircle.app.Activity.Requests.AddRequestActivity;
import com.bloodcircle.app.Activity.Requests.RequestsActivity;
import com.bloodcircle.app.Adapter.Grid.GridBloodsAdapter;
import com.bloodcircle.app.Adapter.Grid.GridMenuAdapter;
import com.bloodcircle.app.Activity.Others.OthersActivity;
import com.bloodcircle.app.Activity.Others.FaqActivity;
import com.bloodcircle.app.Activity.Others.HelpLineActivity;
import com.bloodcircle.app.BuildConfig;
import com.bloodcircle.app.Model.Grid.GridBloodsItem;
import com.bloodcircle.app.Model.Grid.GridMenuItem;
import com.bloodcircle.app.Activity.Account.ProfileActivity;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.Config;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Activity.Others.VolunteersActivity;
import com.bloodcircle.app.Tools.ThemeHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private SharedPreferences sharedPref;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPref = getSharedPreferences("BLOOD_CIRCLE", MODE_PRIVATE);

        if (!sharedPref.getBoolean("is_profile_complete", false)) getProfile();
        setProfile();

        setGridMenu();
        setGridBloods();

        setNavDrawer();
        setBottomNav();

        findViewById(R.id.iv_menu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        checkAppUpdate();
    }

    private void setBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_donors) {
                startActivity(new Intent(this, DonorsActivity.class));
            } else if (id == R.id.nav_requests) {
                startActivity(new Intent(this, RequestsActivity.class));
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(view -> {
            if (sharedPref.getBoolean("is_profile_complete", false)) {
                startActivity(new Intent(this, AddRequestActivity.class));
            } else {
                Snackbar.make(view, R.string.profile_complete, Snackbar.LENGTH_LONG)
                        .setAction(R.string.profile, v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)))
                        .setAnchorView(fabAdd)
                        .show();
            }
        });
    }

    private void setNavDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_drawer);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_faq) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, FaqActivity.class));
                return true;
            } else if (id == R.id.nav_app_credit) {
                drawerLayout.closeDrawer(GravityCompat.START);
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.ic_app_credit)
                        .setTitle(R.string.app_credit)
                        .setMessage(getString(R.string.app_credit_message)+" "+BuildConfig.VERSION_NAME)
                        .setPositiveButton(R.string.close, null)
                        .show();
                return true;
            } else if (id == R.id.nav_contact_us) {
                drawerLayout.closeDrawer(GravityCompat.START);
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support_email)});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")));
                }
                return true;
            } else if (id == R.id.nav_share) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Blood Circle\n\nDownload Now: https://play.google.com/store/apps/details?id="+getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share"));
                return true;
            } else if (id == R.id.nav_rate_us) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                return true;
            } else if (id == R.id.nav_privacy) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PRIVACY_POLICY_URL)));
                return true;
            }
            return false;
        });

        setNotifications();
        setLanguage();
        setTheme();
    }

    private void setNotifications() {
        MenuItem notificationsItem = navigationView.getMenu().findItem(R.id.nav_notifications);
        MaterialSwitch notificationsSwitch = (MaterialSwitch) notificationsItem.getActionView();
        if (notificationsSwitch != null) {
            notificationsSwitch.setChecked(sharedPref.getBoolean("notification", true));
            notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("notification", isChecked);
                editor.apply();
            });
        }
    }

    private void setLanguage() {
        MenuItem languageItem = navigationView.getMenu().findItem(R.id.nav_language);
        MaterialButtonToggleGroup languageToggle = Objects.requireNonNull(languageItem.getActionView()).
                findViewById(R.id.language_toggle);

        languageToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_english) {
                    new LocaleHelper(this).setLocale("en");
                } else {
                    new LocaleHelper(this).setLocale("bn");
                }
            }

            if (!isChecked) recreate();
        });

        if (new LocaleHelper(this).getLocal().equals("en")) {
            languageToggle.check(R.id.btn_english);
        } else {
            languageToggle.check(R.id.btn_bangla);
        }
    }

    private void setTheme() {
        MenuItem themeItem = navigationView.getMenu().findItem(R.id.nav_theme);
        MaterialButtonToggleGroup themeToggle = Objects.requireNonNull(themeItem.getActionView()).
                findViewById(R.id.theme_toggle);

        themeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_light) {
                    new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.btn_dark) {
                    new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }
        });

        if (new ThemeHelper(this).getSelectedTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
            themeToggle.check(R.id.btn_light);
        } else if (new ThemeHelper(this).getSelectedTheme() == AppCompatDelegate.MODE_NIGHT_YES) {
            themeToggle.check(R.id.btn_dark);
        } else {
            themeToggle.check(R.id.btn_system);
        }
    }

    private void setGridBloods() {
        GridView gridBloods = findViewById(R.id.grid_bloods);
        ArrayList<GridBloodsItem> gridBloodsItems = new ArrayList<>();

        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);

        gridBloodsItems.add(new GridBloodsItem(bloodGroups[1], "#3A48BA"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[2], "#F69F23"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[3], "#00C2AF"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[4], "#FF2500"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[5], "#7421B1"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[6], "#016FC4"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[7], "#FB6311"));
        gridBloodsItems.add(new GridBloodsItem(bloodGroups[8], "#02B500"));

        GridBloodsAdapter adapter = new GridBloodsAdapter(this, gridBloodsItems);
        gridBloods.setAdapter(adapter);

        gridBloods.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DonorsActivity.class);
            intent.putExtra("data", position+1);
            startActivity(intent);
        });
    }

    private void setGridMenu() {
        GridView gridMenu = findViewById(R.id.grid_menu);
        ArrayList<GridMenuItem> gridMenuItems = new ArrayList<>();

        gridMenuItems.add(new GridMenuItem(getString(R.string.donors), R.drawable.ic_donors));
        gridMenuItems.add(new GridMenuItem(getString(R.string.recent_requests), R.drawable.ic_recent));
        gridMenuItems.add(new GridMenuItem(getString(R.string.request_blood), R.drawable.ic_request));
        gridMenuItems.add(new GridMenuItem(getString(R.string.become_a_donor), R.drawable.ic_donor));
        gridMenuItems.add(new GridMenuItem(getString(R.string.organization), R.drawable.ic_organization));
        gridMenuItems.add(new GridMenuItem(getString(R.string.blood_bank), R.drawable.ic_blood_bank));
        gridMenuItems.add(new GridMenuItem(getString(R.string.volunteers), R.drawable.ic_volunteer));
        gridMenuItems.add(new GridMenuItem(getString(R.string.ambulance), R.drawable.ic_ambulance));
        gridMenuItems.add(new GridMenuItem(getString(R.string.helpline), R.drawable.ic_helpline));

        GridMenuAdapter adapter = new GridMenuAdapter(this, gridMenuItems);
        gridMenu.setAdapter(adapter);

        gridMenu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, DonorsActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, RequestsActivity.class));
                    break;
                case 2:
                    if (sharedPref.getBoolean("is_profile_complete", false)) {
                        startActivity(new Intent(this, AddRequestActivity.class));
                    } else {
                        Snackbar.make(view, R.string.profile_complete, Snackbar.LENGTH_LONG)
                                .setAction(R.string.profile, v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)))
                                .setAnchorView(findViewById(R.id.bottomNavigationView))
                                .show();
                    }
                    break;
                case 3:
                    startActivity(new Intent(this, AccountActivity.class));
                    break;
                case 4:
                    Intent intent1 = new Intent(MainActivity.this, OthersActivity.class);
                    intent1.putExtra("collection_name", "organization");
                    intent1.putExtra("title", getString(R.string.organization));
                    startActivity(intent1);
                    break;
                case 5:
                    Intent intent2 = new Intent(MainActivity.this, OthersActivity.class);
                    intent2.putExtra("collection_name", "blood_bank");
                    intent2.putExtra("title", getString(R.string.blood_bank));
                    startActivity(intent2);
                    break;
                case 6:
                    startActivity(new Intent(this, VolunteersActivity.class));
                    break;
                case 7:
                    Intent intent4 = new Intent(MainActivity.this, OthersActivity.class);
                    intent4.putExtra("collection_name", "ambulance");
                    intent4.putExtra("title", getString(R.string.ambulance));
                    startActivity(intent4);
                    break;
                case 8:
                    startActivity(new Intent(this, HelpLineActivity.class));
                    break;
            }
        });
    }

    private void setProfile() {
        TextView tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserEmail.setText(currentUser.getEmail());

        ImageView ivProfile = findViewById(R.id.iv_profile);
        Uri uri = currentUser.getPhotoUrl();
        Glide.with(this).load(uri).into(ivProfile);

        findViewById(R.id.iv_profile).setOnClickListener(view -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void getProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(Objects.requireNonNull(currentUser.getEmail()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            boolean donate = Boolean.TRUE.equals(document.getBoolean("donate"));
                            boolean volunteer = Boolean.TRUE.equals(document.getBoolean("volunteer"));

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_name", name);
                            editor.putBoolean("donate", donate);
                            editor.putBoolean("volunteer", volunteer);
                            editor.putBoolean("is_profile_complete", true);
                            editor.apply();

                            setName();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setName() {
        TextView tvUserName = findViewById(R.id.tv_user_name);
        tvUserName.setText(sharedPref.getString("user_name", currentUser.getDisplayName()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        setName();
    }

    private void checkAppUpdate () {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate();
            } else {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    );
                }
            }
        });

        InstallStateUpdatedListener listener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate();
            }
        };
        appUpdateManager.registerListener(listener);
    }

    ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher =  registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(), result -> {}
    );

}