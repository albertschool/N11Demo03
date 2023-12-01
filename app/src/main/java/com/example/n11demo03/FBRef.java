package com.example.n11demo03;

import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBRef {
    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refST = FBST.getReference();
    public static StorageReference refStamp = refST.child("Stamps");
    public static StorageReference refFull = refST.child("Full");
    public static StorageReference refGallery = refST.child("Gallery");
}
