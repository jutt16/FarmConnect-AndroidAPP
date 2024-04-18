package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmconnect.Adapters.ChatRecyclerAdapter;
import com.example.farmconnect.Models.ChatMessageModel;
import com.example.farmconnect.Models.ChatRoomModel;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatroomId;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_nessage_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        
        otherUsername.setText(otherUser.getUserName());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel==null){
                    chatRoomModel = new ChatRoomModel(chatroomId, Arrays.asList(FirebaseUtil.currentUserId(),
                            otherUser.getUserId()), Timestamp.now(),"");
                };
                FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
            }
        });
    }
    void sendMessageToUser(String message){

        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");

                            sendNotification(message);

                        }
                    }
                });


    }
    void setupChatRecyclerView(){

        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUserName());
                    notificationObj.put("body",message);

                    JSONObject dataobj = new JSONObject();
                    dataobj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataobj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);
                }
                catch(Exception e){

                }
            }

        });

    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";

        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", "Bearer AAAA1sAnL6M:APA91bE5X8yF3-6wuNxrFwE2Yxqk498MY-Y8fQV6LTzZFIU1Rz8-2ZQ6vQvUW0eZg8q19FeA72cduPMlTyBoxy890FWC4-P9rjq5F0qz1pOffJ3im-GI3cFtkWc2gk6ol1CYN8cxq-NY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}