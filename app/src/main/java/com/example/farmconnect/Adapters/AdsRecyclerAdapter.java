package com.example.farmconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.farmconnect.AdsReviewActivity;
import com.example.farmconnect.Models.AdsFile;
import com.example.farmconnect.Models.AdsModel;
import com.example.farmconnect.Models.StoryFile;
import com.example.farmconnect.Models.StoryModel;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdsRecyclerAdapter extends RecyclerView.Adapter<AdsRecyclerAdapter.AdsViewHolder> {
    private Context context;
    private List<AdsModel> adsModelList;

    public void setAds(List<AdsModel> adsModelList) {
        this.adsModelList = adsModelList;
        notifyDataSetChanged();
    }


    public AdsRecyclerAdapter(Context context, List<AdsModel> adsModelList) {
        this.context = context;
        this.adsModelList = adsModelList;
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ads_layout, parent, false);
        return new AdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {
        AdsModel ad = adsModelList.get(position);
        // Load user profile image using Glide
        AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+ad.getUser_profile_image_path(),holder.userProfileImageView);
        holder.userNameTextView.setText(ad.getUser_name());
        holder.description.setText(ad.getDiscription());
        holder.price_textView.setText(ad.getPrice()+" Rs.");
        holder.quantity_textView.setText("qty: "+ad.getQuantity());
        List<SlideModel> slideModels = new ArrayList<>();
        for (AdsFile url : ad.getFiles()) {
            slideModels.add(new SlideModel(context.getResources().getString(R.string.api_base_url)+":8000"+url.getFile_path(), ScaleTypes.FIT));
        }
        holder.imageSlider.setImageList(slideModels);
        holder.likes.setText(String.valueOf(ad.getLikes())+" likes");
        holder.reviews_textView.setText(String.valueOf(ad.getReviews())+" reviews");

        //on click listeners
        holder.reviews_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdsReviewActivity.class);
                intent.putParcelableArrayListExtra("reviews", new ArrayList<>(ad.getAllReviews()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adsModelList.size();
    }

    public static class AdsViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userNameTextView;
        TextView postTimeTextView;
        TextView title_textView;
        TextView price_textView;
        TextView quantity_textView;
        TextView description;
        TextView likes;
        TextView reviews_textView;
        ImageSlider imageSlider;
        MaterialButton button_like;
        MaterialButton button_review;
        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            userProfileImageView = itemView.findViewById(R.id.poster_profile_pic);
            userNameTextView = itemView.findViewById(R.id.poster_username_textView);
            postTimeTextView = itemView.findViewById(R.id.posting_time_textView);
            description = itemView.findViewById(R.id.description_textView);
            imageSlider = itemView.findViewById(R.id.image_slider);
            likes = itemView.findViewById(R.id.likes_textView);
            title_textView = itemView.findViewById(R.id.title_textView);
            price_textView = itemView.findViewById(R.id.price_textView);
            quantity_textView = itemView.findViewById(R.id.quantity_textView);
            button_like = itemView.findViewById(R.id.button_like);
            button_review = itemView.findViewById(R.id.button_review);
            reviews_textView = itemView.findViewById(R.id.reviews_textView);
        }
    }
}