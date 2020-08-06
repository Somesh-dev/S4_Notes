package com.teams4.blog.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teams4.blog.Databases.SessionManager;
import com.teams4.blog.LoginActivity;
import com.teams4.blog.R;
import com.teams4.blog.UserDashboard;


public class ProfileFragment extends Fragment {

    private TextView tv_name, tv_phoneNo, mLink;
    private Button btn_go;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and storing in Variable
        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        //Initialize the views
        tv_name = v.findViewById(R.id.tv_name);
        tv_phoneNo = v.findViewById(R.id.tv_phoneNo);
        btn_go = v.findViewById(R.id.button_go);
        mLink = v.findViewById(R.id.link);


        tv_name.setText(UserDashboard.NAME);
        tv_phoneNo.setText(UserDashboard.PHONENO);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME);
                sessionManager.logoutUserFromSession();
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        //Link (setMovement method)
        if (mLink != null) {
            mLink.setMovementMethod(LinkMovementMethod.getInstance());
        }


        return v;
    }
}
