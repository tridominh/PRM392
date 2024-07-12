package com.example.prm392.Helper;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.prm392.DTO.ItemDTO;
import com.example.prm392.Domain.ItemsDomain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class Service {
    // Function to add object to Realtime Database
    // Get instance of FirebaseDatabase
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    // Assuming 'users' is the node where you want to store the objects
    private static DatabaseReference itemsRef = database.getReference("Items");
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageRef = storage.getReference();
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
            dto.quantity,
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
                updatedDto.quantity,
                pics,
                updatedDto.price,
                100,
                10,
                10
        );

        itemsRef.child(itemId).setValue(updatedItem);
    }

    public static void deleteItem(String id) {
        if (!Util.checkAdminRole()) {
            return;
        }
        itemsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle data change
                System.out.println(dataSnapshot.getKey());
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the snapshot of the data at the specified location
                    ItemsDomain item = dataSnapshot.getValue(ItemsDomain.class);
                    if (item != null) {
                        // Parse the URL using Uri class
                        Uri uri = Uri.parse(item.getPicUrl().get(0));

                        // Get the last path segment which is the filename
                        String imageId = uri.getLastPathSegment();
                        System.out.println(imageId);
                        //Delete from firebase
                        StorageReference fileRef = storageRef.child(imageId);
                        fileRef.delete();
                        System.out.println("Image deleted");
                        //Delete object
                        itemsRef.child(id).removeValue();
                    }
                } else {
                    // Handle case where data does not exist
                    System.out.println("Item not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                System.out.println("Error");
                //Toast.makeText(MainActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static CompletableFuture<String> uploadImageToFirebase(Uri imageUri) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String id = UUID.randomUUID().toString();
        StorageReference imagesRef = storageRef.child(id);

        UploadTask uploadTask = imagesRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                future.complete(imageUrl);
            }).addOnFailureListener(exception -> {
                future.completeExceptionally(exception);
            });
        }).addOnFailureListener(exception -> {
            future.completeExceptionally(exception);
        });

        return future;
    }
}


