package com.example.sustaintheglobe.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sustaintheglobe.MainActivity;
import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.databinding.ActivitySignInBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.service.LoadingBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    EditText etLoginEmail;
    EditText etLoginPassword;
    TextView tvRegisterHere, tvForgotPass;
    Button btnLogin;

    FirebaseAuth mAuth;

    private ActivitySignInBinding binding;
    private LoadingBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        etLoginEmail = findViewById(R.id.editText);
        etLoginPassword = findViewById(R.id.editText2);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPass = findViewById(R.id.tvForgotPass);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });
        tvRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(this, SignUpActivity.class));
        });

        tvForgotPass.setOnClickListener(view -> {

            if (etLoginEmail.getText().toString().isEmpty()){
                etLoginEmail.setError("Email Can't Be Empty");
                etLoginEmail.requestFocus();
                Toast.makeText(this, "Please enter an email address to continue", Toast.LENGTH_SHORT).show();
            }else {
                forgetPass(etLoginEmail.getText().toString());
            }

        });


        binding.facebookCV.setOnClickListener(view -> {
            googleSignIn();
        });

        binding.googleCV.setOnClickListener(view -> {
            googleSignIn();
        });

        binding.passwordShow.setOnClickListener(view -> {

            if(view.getId()== R.id.passwordShow){
                if(binding.editText2.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    ((ImageView)(view)).setImageResource(R.drawable.ic_eye_off);
                    //Show Password
                    binding.editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    ((ImageView)(view)).setImageResource(R.drawable.ic_eye_on);
                    //Hide Password
                    binding.editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });

    }

    private void googleSignIn() {

        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();

    }



    private void loginUser(){

        if (!CheckNetwork.isInternetAvailable(this)){
            return;
        }
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        }else{

            loadingBar = new LoadingBar(this, "Signing In...");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        DocumentReference db = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());

                        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        boolean profileComplete = document.getBoolean("profileComplete");
                                        SharedPreferences prefs = getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
                                        prefs.edit().putBoolean("profileComplete",profileComplete).apply();

                                        if (profileComplete) {
                                            // Open MainActivity
                                            loadingBar.dismiss();
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish(); // Optional: finish SignInActivity
                                        } else {
                                            // Open PersonalInfoActivity
                                            loadingBar.dismiss();
                                            Intent intent = new Intent(SignInActivity.this, PersonalInfoActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish(); // Optional: finish SignInActivity
                                        }
                                    } else {
                                        loadingBar.dismiss();
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    loadingBar.dismiss();
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(SignInActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void forgetPass(String email){
        if (!CheckNetwork.isInternetAvailable(this)){
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "Email sent.");
                            Toast.makeText(SignInActivity.this, "Please check your email for further instructions.(Check Spam Too)", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}