package com.example.prm392.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Adapter.ChatAdapter;
import com.example.prm392.Domain.ChatMessageDomain;
import com.example.prm392.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.Toast;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessageDomain> chatMessages;
    private String currentUserId;
    private String adminUserId = "adminUserId"; // Set the admin user ID here

    private DatabaseReference chatDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdapter);

        // Initialize Firebase Database reference
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("chats");

        // Load existing messages from Firebase
        loadMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    long timestamp = System.currentTimeMillis();
                    ChatMessageDomain chatMessage = new ChatMessageDomain(message, currentUserId, timestamp);
                    chatDatabaseReference.push().setValue(chatMessage);
                    messageEditText.setText("");
                    scrollToBottom();
                }
            }
        });
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void loadMessages() {
        chatDatabaseReference.child(currentUserId).child(adminUserId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ChatMessageDomain chatMessage = dataSnapshot.getValue(ChatMessageDomain.class);
                        if (chatMessage != null) {
                            chatMessages.add(chatMessage);
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatMessages.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // Not used in this example
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Not used in this example
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // Not used in this example
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Not used in this example
                    }
                });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            long timestamp = System.currentTimeMillis();
            ChatMessageDomain chatMessage = new ChatMessageDomain(messageText, currentUserId, timestamp);

            // Save message to Firebase
            chatDatabaseReference.child(currentUserId).child(adminUserId)
                    .push()
                    .setValue(chatMessage)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            messageEditText.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
        }
    }
}
