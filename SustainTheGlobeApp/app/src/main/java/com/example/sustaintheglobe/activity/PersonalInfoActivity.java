package com.example.sustaintheglobe.activity;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.sustaintheglobe.MainActivity;
import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.ActivityPersonalInfoBinding;
import com.example.sustaintheglobe.databinding.ActivitySignUpBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.service.LoadingBar;
import com.example.sustaintheglobe.utils.TaskHelper;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity implements LocationListener {

    // TODO: Add Profile picture/edit

    private ActivityPersonalInfoBinding binding;
    private DocumentReference userCollectionRef;
    private CollectionReference userRef;
    LoadingBar loadingBar;

    private String gender="male";

    LocationManager locationManager;

    FirebaseAuth mAuth;

    String cityName,stateName,countryName;

    GeoPoint locationGeoPoint;
    String geoHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        binding.btnSignOut.setOnClickListener(view -> {
            if (!CheckNetwork.isInternetAvailable(this)){
                return;
            }

            mAuth.signOut();

            startActivity(new Intent(this, SignUpActivity.class));


        });

        binding.btnContinue.setOnClickListener(view -> {
            addUserDetails();


        });


        // Click listener for maleCV
        binding.maleImageView.setOnClickListener(view -> {
            Toast.makeText(this,"Male",Toast.LENGTH_SHORT).show();
            binding.maleCV.setStrokeColor(getResources().getColor(R.color.accent));
            binding.maleImageView.setBackgroundColor(getResources().getColor(R.color.primary));

            binding.femaleCV.setStrokeColor(getResources().getColor(R.color.grey_subtext));
            binding.femaleImageView.setBackgroundColor(getResources().getColor(R.color.secondary));

            binding.otherCV.setStrokeColor(getResources().getColor(R.color.grey_subtext));
            binding.otherImageView.setBackgroundColor(getResources().getColor(R.color.secondary));

            gender="male";
        });

        // Click listener for femaleCV
        binding.femaleImageView.setOnClickListener(view -> {
            Toast.makeText(this,"Female",Toast.LENGTH_SHORT).show();

            binding.femaleCV.setStrokeColor(getResources().getColor(R.color.accent));
            binding.femaleImageView.setBackgroundColor(getResources().getColor(R.color.primary));

            binding.maleCV.setStrokeColor(getResources().getColor(R.color.grey_subtext));
            binding.maleImageView.setBackgroundColor(getResources().getColor(R.color.secondary));

            binding.otherCV.setStrokeColor(getResources().getColor(R.color.grey_subtext));
            binding.otherImageView.setBackgroundColor(getResources().getColor(R.color.secondary));

            gender="female";
        });

        // Click listener for otherCV
        binding.otherImageView.setOnClickListener(view -> {
            Toast.makeText(this,"Other",Toast.LENGTH_SHORT).show();
            binding.otherCV.setStrokeColor(getColor(R.color.accent));
            binding.otherImageView.setBackgroundColor(getColor(R.color.primary));

            binding.maleCV.setStrokeColor(getColor(R.color.grey_subtext));
            binding.maleImageView.setBackgroundColor(getColor(R.color.secondary));

            binding.femaleCV.setStrokeColor(getColor(R.color.grey_subtext));
            binding.femaleImageView.setBackgroundColor(getColor(R.color.secondary));
            gender= "other";
        });


        binding.locationTV.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            } else {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationEnabled();
                getLocation();

            }

        });



    }



    private void checkUsername(String username, UsernameCheckCallback callback) {
        // Query Firestore to check if the username exists
        userRef.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If there are any documents with the given username, invoke the callback with true
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Username exists
                            callback.onUsernameChecked(true);
                            return;
                        }
                        // Username doesn't exist
                        callback.onUsernameChecked(false);
                    } else {
                        // Error occurred while fetching documents
                        // Handle the error accordingly
                        callback.onUsernameChecked(false);
                    }
                });
    }

    // Callback interface for username check
    public interface UsernameCheckCallback {
        void onUsernameChecked(boolean usernameExists);
    }

    private void addUserDetails() {
        if (!CheckNetwork.isInternetAvailable(this)) {
            return;
        }

        userRef = FirebaseFirestore.getInstance().collection("Users");

        String userName = binding.etUserName.getText().toString();
        String bio = binding.bio.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            binding.etUserName.setError("Username  cannot be empty");
            binding.etUserName.requestFocus();
            loadingBar.dismiss();

        } else if (TextUtils.isEmpty(bio)) {
            binding.bio.setError("Bio cannot be empty");
            binding.bio.requestFocus();
            loadingBar.dismiss();

        }else if(locationGeoPoint==null){
            Toast.makeText(this, "Please select your location", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }else if(cityName==null || countryName==null){
            Toast.makeText(this, "Please select your location", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }else {
            loadingBar = new LoadingBar(this, "Loading...");
            loadingBar.show();
            checkUsername(userName, new UsernameCheckCallback() {
                @Override
                public void onUsernameChecked(boolean usernameExists) {
                    if (!usernameExists) {
                        // Username doesn't exist, proceed with adding user details

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("gender", gender);
                        updates.put("bio", bio);
                        updates.put("location", locationGeoPoint);
                        updates.put("cityName", cityName);
                        updates.put("countryName", countryName);
                        updates.put("username",userName);
                        updates.put("profileComplete",true);

                        // Now, you need to append the user data to the Firestore document
                        // Assuming `userRef` is your reference to the Firestore collection
                        userRef.document(mAuth.getCurrentUser().getUid()) // Assuming userID is already defined somewhere
                                .update(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data successfully written
                                        // You may want to do something here like showing a success message

                                        SharedPreferences prefs = getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
                                        prefs.edit().clear().apply();

                                        prefs.edit().putBoolean("profileComplete",true).apply();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(PersonalInfoActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();



                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors
                                        loadingBar.dismiss();

                                        Toast.makeText(PersonalInfoActivity.this, "Failed to add user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Rest of your code to update user details
                    } else {
                        loadingBar.dismiss();
                        // Username already exists, show an error message or take appropriate action
                        binding.etUserName.setError("Username already exists");
                        binding.etUserName.requestFocus();
                        Toast.makeText(PersonalInfoActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    private void locationEnabled() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();
            countryName = addresses.get(0).getCountryName();

            binding.locationTV.setText(cityName + ", " + stateName + ", " + countryName);

            // Create GeoPoint from latitude and longitude
            locationGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(locationGeoPoint.getLatitude(), locationGeoPoint.getLongitude()));


            // Now you can use `geoPoint` to set the location in Firestore or wherever you need it
            // For example, if you want to update a user's location in Firestore:
            // userRef.document(userID).update("location", geoPoint);

            Toast.makeText(this, "Location Updated: " + cityName + ", " + stateName + ", " + countryName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle any exceptions
        }
    }




}