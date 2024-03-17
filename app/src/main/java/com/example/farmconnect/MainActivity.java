package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.farmconnect.Adapters.ViewPagerMessengerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Elements
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ViewPagerMessengerAdapter adapter = new ViewPagerMessengerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set custom tab views with icons
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }

        // Set the default tab color
        updateTabIconColors(viewPager.getCurrentItem());

        // Set a listener for tab selection to update icon colors
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabIconColors(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for this example
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Method to update icon colors when a tab is selected
    private void updateTabIconColors(int selectedPosition) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View customView = tab.getCustomView();
                ImageView icon = customView.findViewById(R.id.tab_icon);

                // Set the color filter for the active tab
                int tabColor = (i == selectedPosition) ? ContextCompat.getColor(this, R.color.agri) : Color.GRAY;
                icon.setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}