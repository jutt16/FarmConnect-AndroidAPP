package com.example.farmconnect.Adapters;

import android.content.Context;
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
import com.example.farmconnect.Models.StoryFile;
import com.example.farmconnect.Models.StoryModel;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class StoryViewRecyclerAdapter extends RecyclerView.Adapter<StoryViewRecyclerAdapter.StoriesViewHolder> {
    private Context context;
    private List<StoryModel> storyList;

    public void setStories(List<StoryModel> storyList) {
        this.storyList = storyList;
        notifyDataSetChanged();
    }


    public StoryViewRecyclerAdapter(Context context, List<StoryModel> storyList) {
            this.context = context;
            this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.story_layout, parent, false);
            return new StoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position) {
        StoryModel story = storyList.get(position);
        // Load user profile image using Glide
        AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+story.getUser().getProfile_image_path(),holder.userProfileImageView);
        holder.userNameTextView.setText(story.getUser().getName());
        holder.description.setText(story.getDiscription());
        List<SlideModel> slideModels = new ArrayList<>();
        for (StoryFile url : story.getFiles()) {
            slideModels.add(new SlideModel(context.getResources().getString(R.string.api_base_url)+":8000"+url.getFile_path(), ScaleTypes.FIT));
        }
        holder.imageSlider.setImageList(slideModels);
        holder.likes.setText(String.valueOf(story.getLikes())+" likes");
//        Log.d("Likes",String.valueOf(story.getLikes()));
    }

    @Override
    public int getItemCount() {
            return storyList.size();
    }

    public static class StoriesViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userNameTextView;
        TextView postTimeTextView;
        TextView description;
        TextView likes;
        ImageSlider imageSlider;
        public StoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            userProfileImageView = itemView.findViewById(R.id.poster_profile_pic);
            userNameTextView = itemView.findViewById(R.id.poster_username_textView);
            postTimeTextView = itemView.findViewById(R.id.posting_time_textView);
            description = itemView.findViewById(R.id.description_textView);
            imageSlider = itemView.findViewById(R.id.image_slider);
            likes = itemView.findViewById(R.id.likes_textView);

        }
    }
}