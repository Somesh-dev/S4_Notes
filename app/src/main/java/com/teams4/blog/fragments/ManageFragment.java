package com.teams4.blog.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.teams4.blog.Databases.Upload;
import com.teams4.blog.Databases.UserHelperClass;
import com.teams4.blog.R;
import com.teams4.blog.UserDashboard;

import java.io.IOException;

public class ManageFragment extends Fragment {

    private Button btnChoose, btnUpload;
    private ImageView imageView;
    private TextInputLayout notes_layout, id_layout;
    private EditText et_notes, et_id;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private View v;
    private String val_name, val_id, val_url;
    private Uri uri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_manage, container, false);

        //Initialize Views
        btnChoose = (Button) v.findViewById(R.id.btnChoose);
        btnUpload = (Button) v.findViewById(R.id.btnUpload);
        imageView = (ImageView) v.findViewById(R.id.imgView);
        id_layout = v.findViewById(R.id.etLayout_id);
        checkBox = v.findViewById(R.id.checkBox);
        notes_layout = v.findViewById(R.id.etLayout_notes);
        progressBar = v.findViewById(R.id.progressBar);
        et_notes = v.findViewById(R.id.notes_editText);
        et_id = v.findViewById(R.id.id_editText);



        mStorageRef = FirebaseStorage.getInstance().getReference("onUploads");




        v.findViewById(R.id.btnChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                } else {
                    chooseImage();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateId()){
                    return;
                }
               uploadImage();
            }
        });

        return v;
    }


    //Function to choose image
    private void chooseImage() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }


    //validations
    private Boolean validateId() {
        val_id = id_layout.getEditText().getText().toString().trim();
        val_name = notes_layout.getEditText().getText().toString().trim();


        if (val_id.isEmpty()) {
            id_layout.setError("Field cannot be empty");
            return false;
        } else {
            id_layout.setError(null);
            id_layout.setErrorEnabled(false);
            return true;
        }

    }




    //Function to upload image to the firebase
    public void uploadImage() {

        progressBar.setVisibility(View.VISIBLE);



        if (uri != null){

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
            +"." + getFileExtension(uri));

            fileReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        Upload upload = new Upload(val_name, downloadUri.toString(), val_id, UserDashboard.NAME);
                        if (checkBox.isChecked()){

                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child("onUploads");
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"Upload successful as public", Toast.LENGTH_SHORT).show();

                            uri = null;

                        }else{

                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(UserDashboard.PHONENO).child("offUploads");
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"Upload successful as private", Toast.LENGTH_SHORT).show();

                            uri = null;
                        }
                        et_id.setText("");
                        et_notes.setText("");
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.upload_logo));

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{

            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();

        }

    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    //On getting photos from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                imageView.setImageBitmap(bitmapImage);
                Picasso.with(getContext()).load(returnUri).into(imageView);
                uri = returnUri;

            }
        }
    }


}
