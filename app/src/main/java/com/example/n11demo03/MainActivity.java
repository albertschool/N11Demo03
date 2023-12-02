package com.example.n11demo03;

import static android.app.PendingIntent.getActivity;
import static com.example.n11demo03.FBRef.refFull;
import static com.example.n11demo03.FBRef.refGallery;
import static com.example.n11demo03.FBRef.refStamp;

import static java.lang.Integer.getInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The Main activity
 * <p>
 * This activity use to demonstrate:
 * - take photos from camera / gallery & upload them to firebase storage
 * - download images from firebase storage & display them
 * </p>
 *
 * @author Levy Albert albert.school2015@gmail.com
 * @version 2.0
 * @since 01/12/2023
 */
public class MainActivity extends AppCompatActivity {
    private ImageView iV;
    private String lastStamp, lastFull, lastGallery;
    private String currentPath;
    private StorageReference refImg;
    private File localFile;
    private static final int REQUEST_STAMP_CAPTURE = 201;
    private static final int REQUEST_FULL_IMAGE_CAPTURE = 202;
    private static final int REQUEST_PICK_IMAGE = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iV = findViewById(R.id.iV);
    }

    /**
     * takeStamp method
     * <p> Taking a photo by camera to upload to Firebase Storage
     * </p>
     *
     * @param view the view that triggered the method
     */
    public void takeStamp(View view) {
        Intent takePicIntent = new Intent();
        takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_STAMP_CAPTURE);
        }
    }

    /**
     * takeFull method
     * <p> Taking a full resolution photo by camera to upload to Firebase Storage
     * </p>
     *
     * @param view the view that triggered the method
     */
    public void takeFull(View view) {
        // creating local temporary file to store the full resolution photo
        String filename = "tempfile";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imgFile = File.createTempFile(filename,".jpg",storageDir);
            currentPath = imgFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(MainActivity.this,"com.example.n11demo03.fileprovider",imgFile);
            Intent takePicIntent = new Intent();
            takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageUri);
            if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePicIntent, REQUEST_FULL_IMAGE_CAPTURE);
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,"Failed to create temporary file",Toast.LENGTH_LONG);
            throw new RuntimeException(e);
        }
    }

    /**
     * takeFull method
     * <p> Selecting image file from gallery to upload to Firebase Storage
     * </p>
     *
     * @param view
     */
    public void gallery(View view) {
        Intent si = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(si, REQUEST_PICK_IMAGE);
    }

    /**
     * Uploading selected image file to Firebase Storage
     * <p>
     *
     * @param requestCode   The call sign of the intent that requested the result
     * @param resultCode    A code that symbols the status of the result of the activity
     * @param data_back     The data returned
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);

        if (resultCode == Activity.RESULT_OK) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            final ProgressDialog pd;
            switch (requestCode) {
                // Upload camera thumbnail image file
                case REQUEST_STAMP_CAPTURE:
                    Bundle extras = data_back.getExtras();
                    if (extras != null) {
                        pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                        lastStamp = dateFormat.format(date);
                        refImg = refStamp.child(lastStamp+".png");
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();
                        refImg.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    break;
                // Upload camera full resolution image file
                case REQUEST_FULL_IMAGE_CAPTURE:
                    pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                    lastFull = dateFormat.format(date);
                    refImg = refFull.child(lastFull+".jpg");
                    Bitmap imageBitmap = BitmapFactory.decodeFile(currentPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    refImg.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    pd.dismiss();
                                    Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    pd.dismiss();
                                    Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                                }
                            });
                    break;
                // Upload gallery image file
                case REQUEST_PICK_IMAGE:
                    Uri file = data_back.getData();
                    if (file != null) {
                        pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                        lastGallery = dateFormat.format(date);
                        refImg = refGallery.child(lastGallery+".jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "No Image was selected", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    /**
     * Downloading selected image file from Firebase Storage
     * <p>
     *
     * @param view
     */
    public void readImage(View view) {
        int id = view.getId();
        if (id == R.id.readStamp) {
            refImg = refStamp.child(lastStamp+".png");
            try {
                localFile = File.createTempFile(lastStamp,"png");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (id == R.id.readFull) {
            refImg = refFull.child(lastFull+".jpg");
            try {
                localFile = File.createTempFile(lastFull,"jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (id == R.id.readGallery) {
            refImg = refGallery.child(lastGallery+".jpg");
            try {
                localFile = File.createTempFile(lastGallery,"jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Download the image file and  display it
        final ProgressDialog pd=ProgressDialog.show(this,"Image download","downloading...",true);
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Image download success", Toast.LENGTH_LONG).show();
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                iV.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}