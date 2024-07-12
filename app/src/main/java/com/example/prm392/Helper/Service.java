package com.example.prm392.Helper;

import com.example.prm392.DTO.ItemDTO;
import com.example.prm392.Domain.ItemsDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Service {
    // Function to add object to Realtime Database
    // Get instance of FirebaseDatabase
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    // Assuming 'users' is the node where you want to store the objects
    private static DatabaseReference itemsRef = database.getReference("Items");

    public static void addItem(ItemDTO dto) {
        // Check if current user is admin
        if (!Util.checkAdminRole()) {
            // Handle case where user is not authorized
            return;
        }
        ArrayList<String> pics = new ArrayList<>();
        pics.add(dto.picUrl);
        // Create a new User object
        ItemsDomain newItem = new ItemsDomain(
            dto.title,
            dto.description,
            pics,
            dto.price,
            100,
            10,
            10
        );

        itemsRef.push().setValue(newItem);
    }

    public static void updateItem(String itemId, ItemDTO updatedDto) {
        if (!Util.checkAdminRole()) {
            return;
        }

        ArrayList<String> pics = new ArrayList<>();
        pics.add(updatedDto.picUrl);

        ItemsDomain updatedItem = new ItemsDomain(
                updatedDto.title,
                updatedDto.description,
                pics,
                updatedDto.price,
                100,
                10,
                10
        );

        itemsRef.child(itemId).setValue(updatedItem);
    }

    public static void deleteItem(String itemId) {
        if (!Util.checkAdminRole()) {
            return;
        }

        itemsRef.child(itemId).removeValue();
    }
}


