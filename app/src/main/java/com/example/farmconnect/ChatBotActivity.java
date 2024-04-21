package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Adapters.BotMessageAdapter;
import com.example.farmconnect.Adapters.ChatRecyclerAdapter;
import com.example.farmconnect.Models.BotMessageModal;
import com.example.farmconnect.Models.ChatRoomModel;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatBotActivity extends AppCompatActivity {
    ImageButton backBtn;
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    private ArrayList<BotMessageModal> messageModalArrayList;
    private BotMessageAdapter messageRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        backBtn =findViewById(R.id.back_btn);
        chatsRV = findViewById(R.id.chat_recycler_view);
        sendMsgIB = findViewById(R.id.message_send_btn);
        userMsgEdt = findViewById(R.id.chat_nessage_input);

        messageModalArrayList = new ArrayList<>();

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });

        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    AndroidUtil.showToast(getApplicationContext(),"Please enter your message..");
                    return;
                }

                sendMessage(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });

        messageRVAdapter = new BotMessageAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);
    }
    private void sendMessage(String userMsg) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://api.brainshop.ai/get?bid=180414&key=JRrrQGWYraNpC9Os&uid=abc&msg=" + userMsg;
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Add user message to the beginning of the list
        messageModalArrayList.add(0, new BotMessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyItemInserted(0);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("ResponseData", responseData); // Log the response data
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String botResponse = jsonResponse.getString("cnt");
                    // Add bot response to the beginning of the list
                    messageModalArrayList.add(0, new BotMessageModal(botResponse, BOT_KEY));
                    Log.d("BotResponse", botResponse); // Log the bot's response
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "Error parsing JSON response: " + e.getMessage()); // Log JSON parsing error
                    // Add error message to the beginning of the list
                    messageModalArrayList.add(0, new BotMessageModal("No response", BOT_KEY));
                }
                runOnUiThread(() -> {
                    messageRVAdapter.notifyItemInserted(0);
                    // Scroll to the top of the RecyclerView
                    chatsRV.scrollToPosition(0);
                    Log.d("UIUpdate", "RecyclerView updated"); // Log RecyclerView update
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("OkHttpFailure", "Error executing request: " + e.getMessage()); // Log OkHttp request failure
                // Add error message to the beginning of the list
                messageModalArrayList.add(0, new BotMessageModal("Sorry, no response found", BOT_KEY));
                runOnUiThread(() -> {
                    messageRVAdapter.notifyItemInserted(0);
                    // Scroll to the top of the RecyclerView
                    chatsRV.scrollToPosition(0);
                    Log.d("UIUpdate", "RecyclerView updated"); // Log RecyclerView update
                });
            }
        });
    }

}