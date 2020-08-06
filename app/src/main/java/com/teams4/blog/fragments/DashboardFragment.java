package com.teams4.blog.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teams4.blog.Databases.Upload;
import com.teams4.blog.R;
import com.teams4.blog.UserDashboard;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    Button btn, btn1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        btn = v.findViewById(R.id.btn);
        btn1 = v.findViewById(R.id.btn1);

        //creating user welcome message
        TextView tv = v.findViewById(R.id.txt_signIn);
        tv.setText(String.format("Welcome %s", UserDashboard.NAME));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PostPublic.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostPrivate.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        return v;
    }

}
