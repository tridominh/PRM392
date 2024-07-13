package com.example.prm392.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392.Domain.UserDomain;
import com.example.prm392.Helper.Util;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityProfileBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.welcomeMessage, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeTextView = binding.welcomeMessage;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDomain user = dataSnapshot.getValue(UserDomain.class);
                    if (user != null) {
                        binding.welcomeMessage.setText("Welcome " + user.getUserName());
                        binding.usernameTextView.setText(user.getUserName());
                        binding.addressTextView.setText(user.getAddress());
                        binding.phoneNumberTextView.setText(user.getPhoneNumber());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        boolean isAdmin = Util.checkAdminRole();
        if (isAdmin) {
            binding.mainActivity.setVisibility(View.VISIBLE);
            binding.userManagement.setVisibility(View.VISIBLE);
            binding.cartBtnProfile.setVisibility(View.GONE);
            binding.orderListBtn.setVisibility(View.VISIBLE);
            binding.profileBtnProfile.setVisibility(View.VISIBLE);
            binding.chatBtnProfile.setVisibility(View.VISIBLE);
        } else {
            binding.userManagement.setVisibility(View.GONE);
            binding.mainActivity.setVisibility(View.VISIBLE);
            binding.cartBtnProfile.setVisibility(View.VISIBLE);
            binding.orderListBtn.setVisibility(View.GONE);
            binding.profileBtnProfile.setVisibility(View.VISIBLE);
            binding.chatBtnProfile.setVisibility(View.VISIBLE);
        }

        binding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        Button signOutButton = binding.signoutButton;
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        bottomNavigation();
    }

    private void showEditProfileDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);

        EditText editUsername = dialog.findViewById(R.id.editUsername);
        EditText editAddress = dialog.findViewById(R.id.editAddress);
        EditText editPhoneNumber = dialog.findViewById(R.id.editPhoneNumber);
        Button updateButton = dialog.findViewById(R.id.updateButton);

        // Pre-fill the fields with current user info
        editUsername.setText(binding.usernameTextView.getText());
        editAddress.setText(binding.addressTextView.getText());
        editPhoneNumber.setText(binding.phoneNumberTextView.getText());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editUsername.getText().toString();
                String newAddress = editAddress.getText().toString();
                String newPhoneNumber = editPhoneNumber.getText().toString();

                // Update the user info in Firebase
                updateUserProfile(newUsername, newAddress, newPhoneNumber);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateUserProfile(String newUsername, String newAddress, String newPhoneNumber) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.child("userName").setValue(newUsername);
            userRef.child("address").setValue(newAddress);
            userRef.child("phoneNumber").setValue(newPhoneNumber);

            // Update local views
            binding.usernameTextView.setText(newUsername);
            binding.addressTextView.setText(newAddress);
            binding.phoneNumberTextView.setText(newPhoneNumber);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }


    private void bottomNavigation() {
        findViewById(R.id.imageView31_profile).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        });
        binding.cartBtnProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, CartActivity.class)));
        binding.profileBtnProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ProfileActivity.class)));
        binding.chatBtnProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ChatActivity.class)));
        binding.userManagement.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, UserManagementActivity.class)));
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // Handle the sign out logic here, such as navigating to the login screen
                        Intent intent = new Intent(ProfileActivity.this, FirebaseUIActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
