package com.example.prm392.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Domain.ChatMessageDomain;
import com.example.prm392.R;

public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
    private TextView messageTextView;

    public ReceivedMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.messageTextView);
    }

    public void bind(ChatMessageDomain message) {
        messageTextView.setText(message.getMessage());
        itemView.setBackgroundResource(R.drawable.bg_message_received);
    }
}