package com.example.farmconnect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmconnect.Adapters.RecentChatRecyclerAdapter;
import com.example.farmconnect.Models.ChatRoomModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MessagesFragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView();

        return view;
    }
    void  setupRecyclerView(){

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimeStamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query,ChatRoomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
//        AndroidUtil.showToast(getContext(),"Setting Up Chats Fragment");
    }

    @Override
    public void onStart() {
        super.onStart();

        if(adapter!=null)
            adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}