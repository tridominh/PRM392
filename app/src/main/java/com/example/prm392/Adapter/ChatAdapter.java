package com.example.prm392.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392.Domain.ChatMessageDomain;
import com.example.prm392.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessageDomain> chatMessages;
    private String currentUserId;

    public ChatAdapter(List<ChatMessageDomain> chatMessages, String currentUserId) {
        this.chatMessages = chatMessages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageDomain chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_SENT:
                view = inflater.inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_RECEIVED:
                view = inflater.inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageDomain chatMessage = chatMessages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENT:
                ((SentMessageViewHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind(chatMessage);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

}
