package com.example.sukrit.wedriveyou.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sukrit.wedriveyou.Models.User;
import com.example.sukrit.wedriveyou.R;
import com.example.sukrit.wedriveyou.Utils.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout registerName, registerEmail, registerPassword, registerPhone;
    Button btnRegister;
    String name,email,password,phone;

    DatabaseReference mDatabase,mUserDatabase;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(RegisterActivity.this);

        mDatabase=FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.appBarRegister);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        registerName = findViewById(R.id.register_name);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerPhone = findViewById(R.id.register_phone);

        btnRegister = findViewById(R.id.reg_create_btn);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = registerName.getEditText().getText().toString();
                email = registerEmail.getEditText().getText().toString();
                password = registerPassword.getEditText().getText().toString();
                phone = registerPhone.getEditText().getText().toString();

                Log.d("TAG", name+","+email+","+password+","+phone);
                progressDialog.setTitle("Registering User...");
                progressDialog.setMessage("Please wait while we create your account...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                registerUser(name,email,password,phone);
            }
        });

    }
    private void registerUser(final String name, final String email, final String password,final String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUserDatabase = mDatabase.child(Common.user_driver_tb2).child(firebaseUser.getUid());
                            mUserDatabase.keepSynced(true);

                            User user = new User(email,name,phone,password);

                            mUserDatabase.setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(RegisterActivity.this, "You have successfully registered", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                                progressDialog.dismiss();
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                        else {
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
