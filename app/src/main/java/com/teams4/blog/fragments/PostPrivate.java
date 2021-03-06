package com.teams4.blog.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teams4.blog.Databases.Upload;
import com.teams4.blog.R;
import com.teams4.blog.UserDashboard;

import java.util.ArrayList;
import java.util.List;

public class PostPrivate extends AppCompatActivity
        implements ImageAdapterPublic.OnItemClickListener {

    private RecyclerView mrecyclerView;
    private ImageAdapterPublic mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_private);

        //set User wecome message
        TextView tv = findViewById(R.id.txt_signIn);
        tv.setText(String.format("Welcome %s", UserDashboard.NAME));

        //Hooking up Views
        progressBar = findViewById(R.id.progressBar);
        mrecyclerView = findViewById(R.id.recycler_view_private);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapterPublic(PostPrivate.this, mUploads);
        mrecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(PostPrivate.this);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(UserDashboard.PHONENO).child("offUploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapShot.getValue(Upload.class);
                    upload.setKey(postSnapShot.getKey());
                    mUploads.add(upload);
                }


                mAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Sync done", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onItemClick(int position) {

//       Toast.makeText(this, "Normal Click " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDeleteClick(int position) {

        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(PostPrivate.this, "Post Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
