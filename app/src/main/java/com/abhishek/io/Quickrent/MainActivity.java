package com.abhishek.io.Quickrent;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.abhishek.io.Quickrent.firebase.FirebaseHandler;
import com.abhishek.io.Quickrent.model.User;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignIn;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        // firebase
        String USER_TABLE = "User";
        userDatabase = firebaseHandler.getFirebaseConnection(USER_TABLE);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        TextView tvSignUp;
        tvSignUp = findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName;
                userName = etUsername.getText().toString().trim();
                final String password;
                password = etPassword.getText().toString().trim();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your Username and Password", Toast.LENGTH_SHORT).show();
                } else {
                    userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userName).exists()) {
                                User user;
                                user = dataSnapshot.child(userName).getValue(User.class);
                                assert user != null;
                                user.setUserName(dataSnapshot.child(userName).getKey());
                                if (user.getPassword().equals(password)) {
                                    Toast.makeText(MainActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                    Intent dashboardIntent = new Intent(MainActivity.this, Dashboard.class);
                                    Availablity.currentUser = user;
                                    startActivity(dashboardIntent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "User not exists!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
