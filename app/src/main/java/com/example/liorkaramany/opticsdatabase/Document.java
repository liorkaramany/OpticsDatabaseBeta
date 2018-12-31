package com.example.liorkaramany.opticsdatabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Document extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    ImageView img;
    Button back, upload;

    StorageReference r;
    DatabaseReference ref;
    DatabaseReference imgRef;

    ProgressBar progressBar;

    Uri uri = null;

    String mCurrentPhotoPath;

    StorageTask uploadTask;

    String url, idFromIntent;

    String fname, lname, customerID, address, city, phone, mobile;
    int typeID, sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        img = (ImageView) findViewById(R.id.img);

        r = FirebaseStorage.getInstance().getReference("customers");
        ref = FirebaseDatabase.getInstance().getReference("customers");
        imgRef = FirebaseDatabase.getInstance().getReference("images");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        upload = (Button) findViewById(R.id.upload);
        back = (Button) findViewById(R.id.back);

        Intent gt = getIntent();

        fname = gt.getStringExtra("fname");
        lname = gt.getStringExtra("lname");
        customerID = gt.getStringExtra("customerID");
        address = gt.getStringExtra("address");
        city = gt.getStringExtra("city");
        phone = gt.getStringExtra("phone");
        mobile = gt.getStringExtra("mobile");
        typeID = gt.getIntExtra("typeID", -1);

        sign = gt.getIntExtra("sign", 0);

        if (sign == 1)
        {
            upload.setText("Save");
            back.setText("Cancel");
            url = gt.getStringExtra("url");
            idFromIntent = gt.getStringExtra("id");
            Picasso.get().load(url).fit().centerInside().into(img);
        }
    }

    public void capture(View view) {

        if (uploadTask != null)
            Toast.makeText(this, "Image is currently being uploaded", Toast.LENGTH_LONG).show();
        else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    uri = FileProvider.getUriForFile(this,
                            "com.example.liorkaramany.opticsdatabase.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {

                img.setImageURI(uri);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void upload(View view) {
        if (uploadTask != null)
            Toast.makeText(this, "Image is currently being uploaded", Toast.LENGTH_LONG).show();
        else {
            if (uri != null) {

                final String id;

                StorageReference tmpRef;
                if (sign == 0) {
                    id = ref.push().getKey();
                    tmpRef = r.child(id);
                }
                else {
                    id = idFromIntent;
                    tmpRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                }

                uploadTask = tmpRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                if (sign == 0)
                                    ref.child(id).setValue(new Customer(id, fname, lname, customerID, address, city, phone, mobile, typeID));
                                imgRef.child(id).setValue(new Image(id, url));
                            }
                        });

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 500);

                        if (sign == 0) {
                            Toast.makeText(Document.this, "Customer has been uploaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Document.this,
                                    Main.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Document.this, "Customer has been edited", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setProgress(0);
                        Toast.makeText(Document.this, "Push failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
            } else Toast.makeText(this, "You didn't capture a photo", Toast.LENGTH_LONG).show();
        }
    }

    public void back(View view) {
        if (uploadTask != null)
            Toast.makeText(this, "Image is currently being uploaded", Toast.LENGTH_LONG).show();
        else
            finish();
    }

}
