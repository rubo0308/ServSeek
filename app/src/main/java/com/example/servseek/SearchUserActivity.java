package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.servseek.adapter.SearchUserRecyclerAdapter;
import com.example.servseek.utils.FirebaseUtil;
import com.example.servseek.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchTerm = searchInput.getText().toString();
                    if(searchTerm.isEmpty() ){
                        searchInput.setError("Invalid Username");
                    }
                    setupSearchRecyclerView(searchTerm);
                }
                return false;
            }
        });

        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) setupSearchRecyclerView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        backButton.setOnClickListener(v -> {
            startActivity(new Intent(SearchUserActivity.this, MainActivity.class));
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new HomeFragment()).commit();
        });



        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() ){
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }

    void setupSearchRecyclerView(String searchTerm) {
        String searchTermEnd = searchTerm + '\uf8ff';

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThan("username", searchTermEnd);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
}
