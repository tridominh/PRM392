package com.example.prm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.prm392.Domain.ItemsDomain;
import com.example.prm392.Helper.ChangeNumberItemsListener;
import com.example.prm392.Helper.ManagementCart;
import com.example.prm392.databinding.ViewholderCartBinding;

import me.leolin.shortcutbadger.ShortcutBadger;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    private ArrayList<ItemsDomain> listItemSelected;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private Context context;

    public CartAdapter(ArrayList<ItemsDomain> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.binding.titleTxt.setText(listItemSelected.get(position).getTitle());
        holder.binding.feeEachItem.setText("$" + listItemSelected.get(position).getPrice());
        holder.binding.totalEachItem.setText("$" + Math.round((listItemSelected.get(position).getNumberInCart() * listItemSelected.get(position).getPrice())));
        holder.binding.numberItemTxt.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop());

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.binding.pic);

        holder.binding.plusCartBtn.setOnClickListener(v -> {
            managementCart.plusItem(listItemSelected, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.changed();
                updateBadgeCount();
            });
        });

        holder.binding.minusCartBtn.setOnClickListener(v -> {
            managementCart.minusItem(listItemSelected, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.changed();
                updateBadgeCount();
            });
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    private void updateBadgeCount() {
        int count = managementCart.getListCart().size();
        boolean success = ShortcutBadger.applyCount(context, count);
        if (!success) {
            // Handle failure to apply badge count if necessary
        }
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
