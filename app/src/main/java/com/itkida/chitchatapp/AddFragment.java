package com.itkida.chitchatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private UsersAdapter usersAdapter;
    private List<User> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.usersRecyclerView);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(userList, this::addFriend);
        recyclerView.setAdapter(usersAdapter);

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty())
                    searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if (!newText.isEmpty())
                    searchUsers(newText);
                return false;
            }
        });

        return view;
    }

    private void searchUsers(String query) {
        String queryLower = query.toLowerCase();
        db.collection("users")
                .whereGreaterThanOrEqualTo("nameLower", queryLower)
                .whereLessThan("nameLower", queryLower + "\uF8FF") // To ensure it captures all variations
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        usersAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to search users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addFriend(User user) {
        // Add user to the current user's friends list
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId == null) {
            Log.e("AddFriend", "Current user ID is null");
            Toast.makeText(getContext(), "Current user ID is null", Toast.LENGTH_SHORT).show();
            return; // Exit the method early if currentUserId is null
        }

        if (user.getUid() == null) {
            Log.e("AddFriend", "User UID is null");
            Toast.makeText(getContext(), "User UID is null", Toast.LENGTH_SHORT).show();
            return; // Exit the method early if user UID is null
        }
        try{
            if (currentUserId != null && user.getUid() != null) {
                db.collection("users").document(currentUserId)
                        .collection("Friends").document(user.getUid()).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Friend added", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("AddFriend", "Error adding friend: ", e);
                            Toast.makeText(getContext(), "Error adding friend", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e("AddFriend", "Current user ID or User UID is null");
                Toast.makeText(getContext(), "Unable to add friend. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
