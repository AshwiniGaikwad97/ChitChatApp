package com.itkida.chitchatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView noFriendsText,nameTv,emailTv,profileTv;
    private Button addFriendButton;
    private FirebaseFirestore db;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        searchView = view.findViewById(R.id.searchView);
        profileTv = view.findViewById(R.id.profileText);
        nameTv = view.findViewById(R.id.nameTextView);
        emailTv = view.findViewById(R.id.emailTextView);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        noFriendsText = view.findViewById(R.id.noFriendsText);
        addFriendButton = view.findViewById(R.id.addFriendButton);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        String userId = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        //profileTv.setText(name.toUpperCase().toCharArray()[0]);
        emailTv.setText(email);

        db = FirebaseFirestore.getInstance();

        db.collection("users") // Access the "users" collection
                .document(userId) // Access the document corresponding to the current user's UID
                .get() // Get the document
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, extract the user name (or other fields)
                        String userName = documentSnapshot.getString("name"); // Assuming the field is called "name"
                        nameTv.setText(userName);
                        if (userName != null && !userName.isEmpty()) {
                            // Get the first character of the name
                            char firstChar = userName.toUpperCase().charAt(0);
                            profileTv.setText(""+firstChar);
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(friendList,this::chatClicked);
        recyclerView.setAdapter(friendsAdapter);

        loadFriends();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter the friends list based on search query
                friendsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Live search
                friendsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        addFriendButton.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.nav_add);
        });

        return view;
    }

    private void chatClicked(Friend friend) {
        Log.e("ashu","chatClicked:"+friend.getUid());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String chatId = generateChatId(userId, friend.getUid());

        Intent intent = new Intent(getContext(), ChatActivity.class);

        if (friend.getName() != null && !friend.getName().isEmpty()) {
            // Get the first character of the name
            char firstChar = friend.getName().toUpperCase().charAt(0);
            intent.putExtra("firstChar",""+firstChar);
        }
        intent.putExtra("chatId", chatId);  // Pass the chatId to the ChatActivity
        intent.putExtra("name", friend.getName());  // Pass the chatId to the ChatActivity
        intent.putExtra("email", friend.getEmail());  // Pass the chatId to the ChatActivity
        requireContext().startActivity(intent);
    }
    public String generateChatId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }
    private void loadFriends() {
        // Assuming currentUser's UID is obtained via FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("Friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Friend friend = document.toObject(Friend.class);
                            friendList.add(friend);
                        }
                        if (friendList.isEmpty()) {
                            noFriendsText.setVisibility(View.VISIBLE);
                            addFriendButton.setVisibility(View.VISIBLE);
                        } else {
                            noFriendsText.setVisibility(View.GONE);
                            addFriendButton.setVisibility(View.GONE);
                        }
                        friendsAdapter = new FriendsAdapter(friendList,this::chatClicked);
                        recyclerView.setAdapter(friendsAdapter);
                        friendsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
