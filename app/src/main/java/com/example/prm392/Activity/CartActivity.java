package com.example.prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm392.Adapter.CartAdapter;
import com.example.prm392.Helper.ManagementCart;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityCartBinding;

import me.leolin.shortcutbadger.ShortcutBadger;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private double tax;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);

        // Khởi tạo danh sách giỏ hàng
        initCartList();

        // Xử lý sự kiện nút back
        binding.backBtn.setOnClickListener(v -> finish());
        setBottomNavigationListeners();
    }

    private void setBottomNavigationListeners() {
        findViewById(R.id.imageView31_profile).setOnClickListener(v -> {
             startActivity(new Intent(CartActivity.this, MainActivity.class));
         });
//
//        findViewById(R.id.imageView32_profile).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to Wishlist activity
//                startActivity(new Intent(ChatActivity.this, WishlistActivity.class));
//            }
//        });

        findViewById(R.id.cart_btn_profile).setOnClickListener(v -> startActivity(new Intent(CartActivity.this, CartActivity.class)));

        findViewById(R.id.profile_btn_profile).setOnClickListener(v -> startActivity(new Intent(CartActivity.this, ProfileActivity.class)));

        binding.chatBtn.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, ChatActivity.class)));

    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managementCart.getListCart(), this, () -> {
            // Callback để tính toán lại giỏ hàng khi có thay đổi
            calculatorCart();
            // Cập nhật badge count khi thay đổi số lượng mục trong giỏ hàng
            updateBadgeCount(managementCart.getListCart().size());
        }));

        // Tính toán các giá trị subtotal, tax, delivery và total của giỏ hàng
        calculatorCart();
    }

    // Phương thức tính toán lại giỏ hàng
    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;

        // Tính toán tax dựa trên tổng tiền của giỏ hàng
        tax = Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        // Tính toán total dựa trên subtotal, tax và delivery
        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;

        // Hiển thị các giá trị đã tính toán lên giao diện
        binding.totalFeeTxt.setText("$" + managementCart.getTotalFee());
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    // Phương thức cập nhật badge count
    private void updateBadgeCount(int count) {
        boolean success = ShortcutBadger.applyCount(this, count);
        if (!success) {
            // Hiển thị thông báo Toast nếu cập nhật badge count không thành công
            Toast.makeText(this, "Failed to apply badge count", Toast.LENGTH_SHORT).show();
            Log.e("Badge Count", "Failed to apply badge count");
        }
    }
}
