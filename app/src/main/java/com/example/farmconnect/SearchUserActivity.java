package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.farmconnect.Adapters.SearchUserRecyclerAdapter;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchBtn;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchInput = findViewById(R.id.search_username_btn);
        searchBtn = findViewById(R.id.search_user_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        searchBtn.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty()||searchTerm.length()<3){
                searchInput.setError("Invalid username");
                return;
            }
            setupSearchRecyclerView(searchTerm);

        });
    }

    void  setupSearchRecyclerView(String searchTerm){

        try {
            Query query = FirebaseUtil.allUserCollectionReference().
                    whereGreaterThanOrEqualTo("userName",searchTerm)
                    .whereLessThanOrEqualTo("userName",searchTerm+'\uf8ff');

            FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                    .setQuery(query,UserModel.class).build();

            adapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        } catch (Exception e) {
            AndroidUtil.showToast(getApplicationContext(),e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adapter!=null)
            adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}