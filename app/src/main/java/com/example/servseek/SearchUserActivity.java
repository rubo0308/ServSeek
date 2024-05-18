package com.example.servseek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import com.example.servseek.adapter.SearchUserRecyclerAdapter;
import com.example.servseek.utils.FirebaseUtil;
import com.example.servseek.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton, filterButton, backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        filterButton = findViewById(R.id.your_filter_button_id);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        setupSearchInput();
        setupFilterButton();
        setupBackButton();
    }

    private void setupSearchInput() {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch();
            }
            return false;
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(SearchUserActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(SearchUserActivity.this, view);
            popup.inflate(R.menu.filter_options_menu);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.filter_all) {
                    currentFilter = "All";
                } else if (itemId == R.id.filter_four_stars) {
                    currentFilter = "4Stars";
                } else if (itemId == R.id.filter_four_point_five_stars) {
                    currentFilter = "4.5Stars";
                }

                performSearch();
                return true;
            });
            popup.show();
        });
    }

    private void performSearch() {
        String searchTerm = searchInput.getText().toString();
        if (searchTerm.isEmpty()) {
            searchInput.setError("Invalid Username");
            return;
        }
        setupSearchRecyclerView(searchTerm);
    }

    void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtil.allUserCollectionReference();

        if ("4Stars".equals(currentFilter)) {
            query = query.whereGreaterThanOrEqualTo("averageRating", 4.0);
        } else if ("4.5Stars".equals(currentFilter)) {
            query = query.whereGreaterThanOrEqualTo("averageRating", 4.5);
        }

        query = query
                .whereEqualTo("toggleButtonState", false)
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThan("username", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new SearchUserRecyclerAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
