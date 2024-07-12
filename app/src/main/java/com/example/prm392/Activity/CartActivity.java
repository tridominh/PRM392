package com.example.prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm392.Adapter.CartAdapter;
import com.example.prm392.Api.CreateOrder;
import com.example.prm392.Helper.ManagementCart;
import com.example.prm392.databinding.ActivityCartBinding;

import org.json.JSONObject;

import java.math.BigDecimal;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private double tax;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        managementCart = new ManagementCart(this);

        // Khởi tạo danh sách giỏ hàng
        initCartList();

        binding.backBtn.setOnClickListener(v -> finish());

        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();

                double delivery = 10;
                double total = Math.round((managementCart.getTotalFee() + tax + delivery)*100)/100;
                BigDecimal convertedTotalPrice = new BigDecimal(total).multiply(BigDecimal.valueOf(25000));

                try {
                    JSONObject data = orderApi.createOrder(convertedTotalPrice.toPlainString());
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(CartActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                managementCart.clearItems();

                                Intent intent = new Intent(CartActivity.this, ResultActivity.class);
                                intent.putExtra("result", "Pay successfully");
                                startActivity(intent);

                                finish();
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent = new Intent(CartActivity.this, ResultActivity.class);
                                intent.putExtra("result", "Payment is cancelled");
                                startActivity(intent);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent = new Intent(CartActivity.this, ResultActivity.class);
                                intent.putExtra("result", "Payment Error");
                                startActivity(intent);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
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
//            updateBadgeCount(managementCart.getListCart().size());
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
//    private void updateBadgeCount(int count) {
//        boolean success = ShortcutBadger.applyCount(this, count);
//        if (!success) {
//            // Hiển thị thông báo Toast nếu cập nhật badge count không thành công
//            Toast.makeText(this, "Failed to apply badge count", Toast.LENGTH_SHORT).show();
//            Log.e("Badge Count", "Failed to apply badge count");
//        }
//    }
}
