package com.itkida.chitchatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> implements Filterable {

    private List<Friend> friendList;         // Full list of friends
    private List<Friend> filteredFriendList; // Filtered list of friends
    private OnChatFriendClickListener onChatFriendClickListener;

    public interface OnChatFriendClickListener {
        void onChatFriendClick(Friend friend);
    }
    public FriendsAdapter(List<Friend> friendList, OnChatFriendClickListener onChatFriendClickListener) {
        this.friendList = friendList;
        this.filteredFriendList = new ArrayList<>(friendList); // Initially, show all friends
        this.onChatFriendClickListener = onChatFriendClickListener;

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = filteredFriendList.get(position);
        holder.friendNameTextView.setText(friend.getName());
        holder.friendEmailTextView.setText(friend.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChatFriendClickListener.onChatFriendClick(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredFriendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        TextView friendEmailTextView;
        ImageView userProfile;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.profileImage);
            int randomColor = getRandomColor();
            userProfile.setColorFilter(randomColor, PorterDuff.Mode.SRC_IN);
            friendNameTextView = itemView.findViewById(R.id.friendNameTextView);
            friendEmailTextView = itemView.findViewById(R.id.friendEmailTextView);
        }
    }
    private static int getRandomColor() {
        Random random = new Random();
        // Generate a random color
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Friend> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(friendList); // No filter applied, show all friends
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Friend friend : friendList) {
                        // Check if the friend's name or email contains the search query
                        if (friend.getName().toLowerCase().contains(filterPattern) ||
                                friend.getEmail().toLowerCase().contains(filterPattern)) {
                            filteredList.add(friend);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredFriendList.clear();
                filteredFriendList.addAll((List) results.values);
                notifyDataSetChanged(); // Refresh the list
            }
        };
    }
}

