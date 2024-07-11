package com.example.prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392.DTO.ItemDTO;
import com.example.prm392.Helper.Service;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityAddProductBinding;
import com.example.prm392.databinding.ActivityCartBinding;
import com.example.prm392.databinding.ActivityMainBinding;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding binding;

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
        binding.confirmAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to run when the button is clicked
                // For example, you can launch a new activity, show a message, etc.
                ItemDTO dto = new ItemDTO(
                        binding.nameInput.getText().toString().trim(),
                        binding.descriptionInput.getText().toString().trim(),
                        binding.picInput.getText().toString().trim(),
                        Double.parseDouble(binding.priceInput.getText().toString().trim())
                );
                Service.addItem(dto);
                startActivity(new Intent(AddProduct.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "Product has been added!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



