package com.example.prm392.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Adapter.ChatAdapter;
import com.example.prm392.Domain.ChatMessageDomain;
import com.example.prm392.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessageDomain> chatMessages;
    private String currentUserId;
    private String chatUserId;
    private DatabaseReference chatDatabaseReference;
    private DatabaseReference userDatabaseReference;

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
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, FirebaseUIActivity.class));
            finish(); // Close ChatActivity if user is not logged in
            return;
        }

        // Check if current user is admin
        checkIfAdmin();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void checkIfAdmin() {
        userDatabaseReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("admin".equals(role)) {
                        // If user is admin, display a dialog to choose a user to chat with
                        displayUserListDialog();
                    } else {
                        // If user is not admin, set chatUserId to the fixed admin ID
                        chatUserId = "tw0DqWTdwNdfEmvn3CCiuwluZqr2";
                        // Load existing messages from Firebase
                        loadMessages();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error checking role", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserListDialog() {
        // Fetch all users from database and display in a dialog
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userIds = new ArrayList<>();
                List<String> userNames = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("userName").getValue(String.class);
                    if (!userId.equals(currentUserId) && !"admin".equals(userSnapshot.child("role").getValue(String.class))) {
                        userIds.add(userId);
                        userNames.add(userName);
                    }
                }

                if (userNames.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "No users available to chat with.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show dialog with userNames, handle user selection
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select a user to chat with");
                builder.setItems(userNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set chatUserId to the selected user's ID
                        chatUserId = userIds.get(which);
                        // Load existing messages from Firebase
                        loadMessages();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void scrollToBottom() {
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void loadMessages() {
        chatDatabaseReference.child(currentUserId).child(chatUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatMessageDomain message = dataSnapshot.getValue(ChatMessageDomain.class);
                chatMessages.add(message);
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                scrollToBottom();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle child changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle child removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle child moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        String messageId = chatDatabaseReference.push().getKey();
        ChatMessageDomain message = new ChatMessageDomain(messageId, currentUserId, messageText, System.currentTimeMillis());

        chatDatabaseReference.child(currentUserId).child(chatUserId).child(messageId)
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        chatDatabaseReference.child(chatUserId).child(currentUserId).child(messageId).setValue(message);
                        messageEditText.setText("");
                        scrollToBottom();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
