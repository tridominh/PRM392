package com.example.prm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.prm392.Domain.ReviewDomain;
import com.example.prm392.databinding.ViewholderReviewBinding;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    ArrayList<ReviewDomain> items;
    Context context;

    public ReviewAdapter(ArrayList<ReviewDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderReviewBinding binding = ViewholderReviewBinding.inflate(LayoutInflater.from(context), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        holder.binding.nameTxt.setText(items.get(position).getName());
        holder.binding.descTxt.setText(items.get(position).getDescription());
        holder.binding.ratingTxt.setText("" + items.get(position).getRating());

        Glide.with(context)
                .load(items.get(position).getPicUrl())
                .transform(new GranularRoundedCorners(100, 100, 100, 100))
                .into(holder.binding.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderReviewBinding binding;

        public ViewHolder(ViewholderReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
