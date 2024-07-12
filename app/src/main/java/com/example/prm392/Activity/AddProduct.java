package com.example.prm392.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.prm392.DTO.ItemDTO;
import com.example.prm392.Helper.Service;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityAddProductBinding;
import com.example.prm392.databinding.ActivityCartBinding;
import com.example.prm392.databinding.ActivityMainBinding;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding binding;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_READ_PERMISSION = 2;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize the ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        System.out.println(selectedImageUri.toString());
                        if (selectedImageUri != null) {
                            // Display the selected image in an ImageView
//                                    ImageView imageView = findViewById(R.id.imageView);
//                                    imageView.setImageURI(selectedImageUri);
                            Service.uploadImageToFirebase(selectedImageUri)
                                    .thenAccept(url -> {
                                        imageUrl = url;
                                        Glide.with(this).load(url).into(binding.imagePreview);
                                    })
                                    .exceptionally(exception -> {
                                        // Handle exceptions
                                        Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                        return null;
                                    });
                        }
                    }
                }
        );
        binding.confirmAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to run when the button is clicked
                // For example, you can launch a new activity, show a message, etc.
                if(imageUrl == null){
                    Toast.makeText(getApplicationContext(), "Need image!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.nameInput.getText().toString().trim()=="" ||
                    binding.descriptionInput.getText().toString().trim()=="" ||
                        binding.quantityInput.getText().toString().trim()=="" ||
                        binding.priceInput.getText().toString().trim()==""){
                    Toast.makeText(getApplicationContext(), "Please input full!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ItemDTO dto = new ItemDTO(
                        binding.nameInput.getText().toString().trim(),
                        binding.descriptionInput.getText().toString().trim(),
                        Integer.parseInt(binding.quantityInput.getText().toString().trim()),
                        //binding.picInput.getText().toString().trim(),
                        Double.parseDouble(binding.priceInput.getText().toString().trim()),
                        imageUrl
                );
                Service.addItem(dto);
                startActivity(new Intent(AddProduct.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Product has been added!!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your task here
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to read media", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



