package com.example.sustaintheglobe.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.ActivitySignUpBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.service.LoadingBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private DocumentReference userCollectionRef;

    LoadingBar loadingBar;


    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();


        binding.tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(this, SignInActivity.class));
        });
        binding.googleCV.setOnClickListener(view -> {
            googleSignIn();
        });
        binding.facebookCV.setOnClickListener(view -> {
                Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        });

        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        materialDateBuilder.setTitleText("SELECT A DATE");
        materialDateBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        binding.etRegDOB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!(materialDatePicker.getDialog() != null && materialDatePicker.getDialog().isShowing())) {
                            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                        }
                        materialDatePicker.addOnPositiveButtonClickListener(
                                new MaterialPickerOnPositiveButtonClickListener() {
                                    @Override
                                    public void onPositiveButtonClick(Object selection) {
                                        if (selection != null) {
                                            //show full date
                                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                            calendar.setTimeInMillis((Long) selection);
                                            String date = DateFormat.format("dd/MM/yyyy", calendar).toString();
                                            binding.etRegDOB.setText(date);
                                            binding.etRegDOB.setError(null);
                                        }
                                    }
                                });
                    }
                });

        binding.passwordShow.setOnClickListener(view -> {

            if(view.getId()== R.id.passwordShow){
                if(binding.etRegPass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    ((ImageView)(view)).setImageResource(R.drawable.ic_eye_off);
                    //Show Password
                    binding.etRegPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    ((ImageView)(view)).setImageResource(R.drawable.ic_eye_on);
                    //Hide Password
                    binding.etRegPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });

        binding.btnRegister.setOnClickListener(view ->{
            createUser();
        });



    }
    private void createUser(){
        if (!CheckNetwork.isInternetAvailable(this)){
            return;
        }
        String email = binding.etRegEmail.getText().toString();
        String password = binding.etRegPass.getText().toString();
        String displayName = binding.etRegName.getText().toString();
        String phone = binding.etRegMobileNumber.getText().toString();
        String dob = binding.etRegDOB.getText().toString();

        if(TextUtils.isEmpty(displayName)){
            binding.etRegName.setError("Display Name cannot be empty");
            binding.etRegName.requestFocus();
        }
        else if(TextUtils.isEmpty(phone)){
            binding.etRegMobileNumber.setError("Phone Number cannot be empty");
            binding.etRegMobileNumber.requestFocus();
        }else if(TextUtils.isEmpty(dob)){
            binding.etRegDOB.setError("Date Of Birth cannot be empty");
            binding.etRegDOB.requestFocus();
        }
        else if (!isValid(phone)){
            binding.etRegMobileNumber.setError("Invalid Format");
            binding.etRegMobileNumber.requestFocus();
        }
        else if (TextUtils.isEmpty(email)){
            binding.etRegEmail.setError("Email cannot be empty");
            binding.etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            binding.etRegPass.setError("Password cannot be empty");
            binding.etRegPass.requestFocus();

        }else{
            loadingBar = new LoadingBar(this, "Loading...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName).build();
                        user.updateProfile(changeRequest);

                        User userData = new User();
                        userData.setFullName(displayName);
                        userData.setEmail(email);
                        userData.setPhone(phone);
                        userData.setDob(dob);
                        userData.setProfileComplete(false);
                        userData.setUserID(mAuth.getCurrentUser().getUid());
                        userData.setProfileComplete(false);

                        userCollectionRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                        userCollectionRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingBar.dismiss();
                                Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, PersonalInfoActivity.class));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(SignUpActivity.this, "Data Set Error " + e, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }else{
                        loadingBar.dismiss();
                        Toast.makeText(SignUpActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void googleSignIn() {
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();

    }

    public static boolean isValid(String s)
    {

        Pattern p = Pattern.compile("^[6-9]\\d{9}$");

        Matcher m = p.matcher(s);

        return (m.matches());
    }

}