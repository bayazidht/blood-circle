package com.bloodcircle.app.Activity.Others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bloodcircle.app.Activity.Account.LoginActivity;
import com.bloodcircle.app.Activity.Home.MainActivity;
import com.bloodcircle.app.R;
import com.bloodcircle.app.Tools.LocaleHelper;
import com.bloodcircle.app.Tools.ThemeHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnPrevious, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocaleHelper(this).setAppLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTheme();

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        layouts = new int[]{
                R.layout.welcome_slide_1,
                R.layout.welcome_slide_2,
                R.layout.welcome_slide_3,
                R.layout.welcome_slide_4
        };

        addBottomDots(0);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnPrevious.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current != 0) {
                viewPager.setCurrentItem(current-1);
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem()+1;
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                launchLoginScreen();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        int inactiveColor = Color.argb(150, 169, 169, 169);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        int activeColor = ContextCompat.getColor(this, typedValue.resourceId);

        TextView[] dots = new TextView[layouts.length];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(50);
            dots[i].setTextColor(inactiveColor);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) dots[currentPage].setTextColor(activeColor);
    }

    private void launchLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                btnNext.setText(R.string.let_s_get_started);
            } else {
                btnPrevious.setEnabled(position != 0);
                btnNext.setText(getString(R.string.next));
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}
        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if (position==3) {
                setLanguage(view);
                setTheme(view);
            }

            return view;
        }
        @Override
        public int getCount() {
            return layouts.length;
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private void setLanguage(View view) {
        MaterialButtonToggleGroup languageToggle = view.findViewById(R.id.language_toggle);

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

        if (new LocaleHelper(this).getLocal().equals("bn")) {
            languageToggle.check(R.id.btn_bangla);
        } else {
            languageToggle.check(R.id.btn_english);
        }
    }

    private void setTheme(View view) {
        MaterialButtonToggleGroup themeToggle = view.findViewById(R.id.theme_toggle);

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

    private void setTheme() {
        if (new ThemeHelper(this).getSelectedTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
            new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (new ThemeHelper(this).getSelectedTheme() == AppCompatDelegate.MODE_NIGHT_YES) {
            new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            new ThemeHelper(this).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) launchMainActivity();
    }

    private void launchMainActivity() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}