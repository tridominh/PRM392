package com.example.prm392.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm392.Adapter.CartAdapter;
import com.example.prm392.Helper.ChangeNumberItemsListener;
import com.example.prm392.Helper.ManagementCart;
import com.example.prm392.R;
import com.example.prm392.databinding.ActivityCartBinding;

import me.leolin.shortcutbadger.ShortcutBadger;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private double tax;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);

        calculatorCart();
        setVariable();
        initCartList();
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
            calculatorCart();
            updateBadgeCount(managementCart.getListCart().size());
        }));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void updateBadgeCount(int count) {
        boolean success = ShortcutBadger.applyCount(this, count);
        if (!success) {
            Toast.makeText(this, "Failed to apply badge count", Toast.LENGTH_SHORT).show();
            Log.e("Badge Count", "Failed to apply badge count");
        }
    }
}
