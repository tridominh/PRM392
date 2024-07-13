package com.example.prm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FirebaseUIActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
        createSignInIntent();
    }

    public void createSignInIntent() {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("com.example.prm392", true, null)
                .setHandleCodeInApp(true)
                .setUrl("https://google.com")
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.cat1)
                .setTheme(R.style.Base_Theme_PRM392)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public void checkIfUserIsNew(FirebaseUser user) {
        DatabaseReference userRef = userDatabaseReference.child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("FirebaseUIActivity", "User exists: " + snapshot.getValue());
                } else {
                    insertNewUserRole(user, "user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUIActivity", "Failed to read user data", error.toException());
            }
        });
    }

    public void insertNewUserRole(FirebaseUser user, String role) {
        // Tạo một tham chiếu đến biến đếm người dùng trong Firebase
        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("userCount");

        // Lấy giá trị hiện tại của biến đếm và cập nhật lên Firebase
        countRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Lấy giá trị hiện tại của biến đếm, nếu chưa có thì đặt giá trị là 0
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    currentValue = 0;
                }

                // Tăng giá trị biến đếm lên 1 để lấy ID cho người dùng mới
                int nextId = currentValue + 1;

                // Lưu giá trị biến đếm mới vào Firebase
                mutableData.setValue(nextId);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    // Biến đếm đã được cập nhật thành công, sử dụng giá trị mới để set ID cho người dùng
                    String userId = String.valueOf(dataSnapshot.getValue(Integer.class));

                    // Thực hiện thêm thông tin người dùng vào Firebase
                    userDatabaseReference.child(user.getUid()).child("role").setValue(role);
                    userDatabaseReference.child(user.getUid()).child("userName").setValue(user.getDisplayName());
                    userDatabaseReference.child(user.getUid()).child("userId").setValue(userId);
                    //Th
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);

                    Log.i("FirebaseUIActivity", "Role successfully added for new user with userId: " + userId);
                } else {
                    // Xảy ra lỗi khi cập nhật biến đếm
                    Log.e("FirebaseUIActivity", "Error updating user count", databaseError != null ? databaseError.toException() : null);
                }
            }
        });
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", user.getDisplayName());
                editor.putString("userEmail", user.getEmail());
                editor.putString("userUid", user.getUid());
                editor.apply();
                checkIfUserIsNew(user);
            }
            startActivity(new Intent(FirebaseUIActivity.this, MainActivity.class));
        } else {
            if (response != null) {
                Toast.makeText(FirebaseUIActivity.this, response.getError().getErrorCode(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FirebaseUIActivity.this, "Sign in cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
