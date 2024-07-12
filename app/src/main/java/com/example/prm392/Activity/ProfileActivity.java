package com.example.prm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392.Helper.Util;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityProfileBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        System.out.println("User Id Profile: "+userId);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.welcomeMessage, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeTextView = binding.welcomeMessage; // Use binding for TextView

        // Retrieve the current user and set the welcome message
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            welcomeTextView.setText("Welcome " + userName);
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

        // Set up sign out button
        Button signOutButton = binding.signoutButton; // Use binding for Button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        bottomNavigation(); // Set up bottom navigation buttons
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
        // [START auth_fui_signout]
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
        // [END auth_fui_signout]
    }
}
