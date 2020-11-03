package com.example.stockscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private EditText usernameInput;
    private EditText passwordInput;
    private Button register;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.userName);
        passwordInput = findViewById(R.id.userPass);
        register = findViewById(R.id.regBtn);
        login = findViewById(R.id.logBtn);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(view -> registerUser());
        login.setOnClickListener(view -> loginUser());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent intent = new Intent(this, InventoryScreen.class);
            startActivity(intent);
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
}