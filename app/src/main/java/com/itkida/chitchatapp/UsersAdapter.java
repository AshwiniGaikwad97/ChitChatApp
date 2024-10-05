package com.itkida.chitchatapp;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList;
    private OnAddFriendClickListener onAddFriendClickListener;

    public UsersAdapter(List<User> userList, OnAddFriendClickListener onAddFriendClickListener) {
        this.userList = userList;
        this.onAddFriendClickListener = onAddFriendClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());

        // Handle add friend button click
        holder.addFriendButton.setOnClickListener(v -> {
            onAddFriendClickListener.onAddFriendClick(user);
        });
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (Objects.equals(user.getUid(), userId))
            holder.addFriendButton.setVisibility(View.GONE);
        else
            holder.addFriendButton.setVisibility(View.VISIBLE);
        Log.e("ashu","listUID:"+user.getUid()+" & userId:"+userId);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfile;
        TextView usernameTextView;
        TextView emailTextView;
        Button addFriendButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.profileImage);
            int randomColor = getRandomColor();
            userProfile.setColorFilter(randomColor, PorterDuff.Mode.SRC_IN);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            addFriendButton = itemView.findViewById(R.id.addFriendButton);
        }
    }

    private static int getRandomColor() {
        Random random = new Random();
        // Generate a random color
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // Interface for handling add friend button click
    public interface OnAddFriendClickListener {
        void onAddFriendClick(User user);
    }
}

