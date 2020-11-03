package com.example.stockscan.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stockscan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFrag extends Fragment {

    private final String TAG = "LoginFrag";

    private Context context;
    private FirebaseAuth mAuth;

    private EditText usernameInput;
    private EditText passwordInput;
    private Button register;
    private Button login;

    public LoginFrag() {
        // Required empty public constructor
    }

    public static LoginFrag newInstance() {
        return new LoginFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameInput = view.findViewById(R.id.userName);
        passwordInput = view.findViewById(R.id.userPass);
        register = view.findViewById(R.id.regBtn);
        login = view.findViewById(R.id.logBtn);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(l -> registerUser());
        login.setOnClickListener(l -> loginUser());
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){

        }
    }



    private void registerUser(){
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }else{
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    private void loginUser(){
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }else{
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}