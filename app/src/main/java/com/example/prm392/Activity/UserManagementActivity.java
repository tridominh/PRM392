package com.example.prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Adapter.UserAdapter;
import com.example.prm392.Adapter.UserAdapters;
import com.example.prm392.Domain.UserDomain;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityUserManagementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {
    private ActivityUserManagementBinding binding;
    private ArrayList<UserDomain> userList;
    private UserAdapters userAdapter;

    private TableLayout tableLayoutUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userList = new ArrayList<>();
        userAdapter = new UserAdapters(userList);

        tableLayoutUsers = findViewById(R.id.tableLayoutUsers);
        bottomNavigation();
        fetchUsers();

    }

    private void bottomNavigation() {
        binding.mainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserManagementActivity.this, MainActivity.class));
            }
        });
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserManagementActivity.this, ProfileActivity.class));
            }
        });

        binding.chatBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserManagementActivity.this, ChatActivity.class));
            }
        });

        binding.userManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserManagementActivity.this, UserManagementActivity.class));
            }
        });
    }


    private void addUserToTable(UserDomain user) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        TextView idTextView = new TextView(this);
        idTextView.setText(user.getUserId());
        idTextView.setPadding(8, 8, 8, 8);
        idTextView.setGravity(Gravity.CENTER);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(user.getUserName());
        nameTextView.setPadding(8, 8, 8, 8);
        nameTextView.setGravity(Gravity.START);

        row.addView(idTextView);
        row.addView(nameTextView);

        tableLayoutUsers.addView(row);
    }

    private void fetchUsers() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        UserDomain user = userSnapshot.getValue(UserDomain.class);
                        userList.add(user);
                        addUserToTable(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserManagementActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
