package com.example.farmconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.farmconnect.HomeFragment;
import com.example.farmconnect.MarketplaceFragment;
import com.example.farmconnect.MessagesFragment;
import com.example.farmconnect.NotificationsFragment;
import com.example.farmconnect.R;
import com.example.farmconnect.UsersFragment;

public class ViewPagerMessengerAdapter extends FragmentPagerAdapter {
    private Context context;

    public ViewPagerMessengerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position == 1) {
            return new UsersFragment();
        } else if (position == 2) {
            return new MessagesFragment();
        } else if (position == 3) {
            return new MarketplaceFragment();
        } else {
            return new NotificationsFragment();
        }
    }

    @Override
    public int getCount() {
        return 5; //no of tabs
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        ImageView icon = view.findViewById(R.id.tab_icon);
        icon.setImageResource(getIcon(position));
        return view;
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.baseline_home_24; // Replace with your home icon
            case 1:
                return R.drawable.baseline_people_24; // Replace with your users icon
            case 2:
                return R.drawable.baseline_chat_24; // Replace with your messages icon
            case 3:
                return R.drawable.baseline_store_24; // Replace with your marketplace icon
            case 4:
                return R.drawable.baseline_notifications_24; // Replace with your notifications icon
            default:
                return 0; // Default icon (you can use a placeholder or handle this case)
        }
    }
}