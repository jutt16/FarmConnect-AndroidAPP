package com.example.farmconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Models.BotMessageModal;
import com.example.farmconnect.R;

import java.util.ArrayList;

public class BotMessageAdapter extends RecyclerView.Adapter<BotMessageAdapter.ChatBotModelViewHolder> {

    // variable for our array list and context.
    private ArrayList<BotMessageModal> messageModalArrayList;
    private Context context;

    // constructor class.
    public BotMessageAdapter(ArrayList<BotMessageModal> messageModalArrayList, Context context) {
        this.messageModalArrayList = messageModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatBotModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row_for_bot,parent,false);
        return new ChatBotModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotModelViewHolder holder, int position) {
        BotMessageModal modal = messageModalArrayList.get(position);
        switch (modal.getSender()) {
            case "user":
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatTextView.setText(modal.getMessage());
                break;
            case "bot":
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTextView.setText(modal.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        // return the size of array list
        return messageModalArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // below line of code is to set position.
        switch (messageModalArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    public static class ChatBotModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextView,rightChatTextView;
        public ChatBotModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}

