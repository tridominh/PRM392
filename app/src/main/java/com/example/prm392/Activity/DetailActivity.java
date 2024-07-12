package com.example.prm392.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Adapter.SizeAdapter;
import com.example.prm392.Adapter.SliderAdapter;
import com.example.prm392.Domain.ItemsDomain;
import com.example.prm392.Domain.SliderItems;
import com.example.prm392.Fragment.DescriptionFragment;
import com.example.prm392.Fragment.ReviewFragment;
import com.example.prm392.Fragment.SoldFragment;
import com.example.prm392.Helper.ManagementCart;
import com.example.prm392.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private ItemsDomain object;
    private int numberOrder = 1;
    private String Id;
    private ManagementCart managementCart;
    private Handler slideHandler = new Handler();
    private boolean isAdmin = false; // Assume initially not admin


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);

        getBundles();
        initBanners();
        initSize();
        setupViewPager();

        isAdmin = checkAdminRole();

        if (isAdmin) {
            binding.btnUpdateProduct.setVisibility(View.VISIBLE);
            binding.btnDeleteProduct.setVisibility(View.VISIBLE);
        } else {
            binding.btnUpdateProduct.setVisibility(View.GONE);
            binding.btnDeleteProduct.setVisibility(View.GONE);
        }

        // Set click listeners for admin buttons

        binding.btnUpdateProduct.setOnClickListener(v -> {
            Toast.makeText(DetailActivity.this, "Update Product clicked", Toast.LENGTH_SHORT).show();
        });

        binding.btnDeleteProduct.setOnClickListener(v -> {
            Toast.makeText(DetailActivity.this, "Delete Product clicked", Toast.LENGTH_SHORT).show();
        });
    }


    private void initSize() {
        ArrayList<String> list = new ArrayList<>();
        list.add("S");
        list.add("M");
        list.add("L");
        list.add("XL");
        list.add("XXL");

        binding.recyclerSize.setAdapter(new SizeAdapter(list));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private boolean checkAdminRole() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if ("tw0DqWTdwNdfEmvn3CCiuwluZqr2".equals(userId)) {
            return true;
        } else {
            return false;
        }
    }

    private void initBanners() {
        ArrayList<SliderItems> sliderItems = new ArrayList<>();
        for(int i = 0; i < object.getPicUrl().size(); i++) {
            sliderItems.add(new SliderItems(object.getPicUrl().get(i)));
        }
        binding.viewpageSlider.setAdapter(new SliderAdapter(sliderItems, binding.viewpageSlider));
        binding.viewpageSlider.setClipToPadding(false);
        binding.viewpageSlider.setClipChildren(false);
        binding.viewpageSlider.setOffscreenPageLimit(3);
        binding.viewpageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void getBundles() {
        object = (ItemsDomain) getIntent().getSerializableExtra("object");
        Id = (String) getIntent().getSerializableExtra("id");
        System.out.println(Id);
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.ratingBar.setRating((float) object.getRating());
        binding.ratingTxt.setText(object.getRating() + " Rating");

        binding.addTocartBtn.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder);
            managementCart.insertItem(object);
        });
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        DescriptionFragment tab1 = new DescriptionFragment();
        ReviewFragment tab2 = new ReviewFragment();
        SoldFragment tab3 = new SoldFragment();

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = new Bundle();

        bundle1.putString("description", object.getDescription());

        tab1.setArguments(bundle1);
        tab2.setArguments(bundle2);
        tab3.setArguments(bundle3);

        adapter.addFrag(tab1, "Descriptions");
        adapter.addFrag(tab2, "Reviews");
        adapter.addFrag(tab3, "Sold");

        binding.viewpager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}