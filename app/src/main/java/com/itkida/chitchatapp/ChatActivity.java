package com.itkida.chitchatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;
    private String chatId;
    private TextView nameTv,emailTv,profileTv;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        recyclerViewMessages = findViewById(R.id.chatRecyclerView);
        editTextMessage = findViewById(R.id.messageEditText);
        buttonSend = findViewById(R.id.sendButton);
        profileTv = findViewById(R.id.profileText);
        nameTv = findViewById(R.id.nameTextView);
        emailTv = findViewById(R.id.emailTextView);

        messageList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        // Assuming chatId is passed through an Intent or generated
        chatId = getIntent().getStringExtra("chatId");
        String firstChar = getIntent().getStringExtra("firstChar");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        profileTv.setText(firstChar);
        nameTv.setText(name);
        emailTv.setText(email);

        chatAdapter = new ChatAdapter(messageList, currentUserId);
        recyclerViewMessages.setAdapter(chatAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Load chat messages
        loadMessages();

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buttonSend.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    buttonSend.setVisibility(View.VISIBLE);
                else
                    buttonSend.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("ChatActivity", "Error loading messages", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            ChatMessage message = snapshot.toObject(ChatMessage.class);
                            messageList.add(message);
                        }
//                        chatAdapter = new ChatAdapter(messageList, currentUserId);
//                        recyclerViewMessages.setAdapter(chatAdapter);
                        chatAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString();
        if (!messageText.isEmpty()) {
            ChatMessage message = new ChatMessage(messageText, currentUserId, System.currentTimeMillis());

            db.collection("chats").document(chatId)
                    .collection("messages")
                    .add(message)
                    .addOnSuccessListener(aVoid -> editTextMessage.setText(""))
                    .addOnFailureListener(e -> Log.e("ChatActivity", "Error sending message", e));
        }
    }

}
