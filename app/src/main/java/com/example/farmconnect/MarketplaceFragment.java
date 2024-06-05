package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.farmconnect.Adapters.AdsRecyclerAdapter;
import com.example.farmconnect.Models.AdsModel;
import com.example.farmconnect.utils.AdApiCalls;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceFragment extends Fragment {
    Button sellBtn;
    RecyclerView ads_recycler_view;
    AdsRecyclerAdapter adapter;
    List<AdsModel> adsModelList;
    public MarketplaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);

        //initialize
        sellBtn = view.findViewById(R.id.sellBtn);
        ads_recycler_view = view.findViewById(R.id.ads_recycler_view);

        ads_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        adsModelList = new ArrayList<>();

        adapter = new AdsRecyclerAdapter(getContext(),adsModelList);

        ads_recycler_view.setAdapter(adapter);

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SellProductActivity.class);
                startActivity(intent);
            }
        });

        AdApiCalls.getAllAds(getContext(), new AdApiCalls.GetAdsCallback() {
            @Override
            public void onSuccess(ArrayList<AdsModel> ads) {
                // Handle success, update UI with ads data
                for (AdsModel ad : ads) {
                    Log.d("GetAdsSuccess", "Ad ID: " + ad.getId() + " Description: " + ad.getDiscription()+"\n"+ad.toString());
                    adsModelList.add(ad);
                }
                adapter.setAds(ads);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
                Log.e("GetAdsFailure", "Failed to fetch ads", e);
            }
        });

        return view;
    }
}