package com.example.sustaintheglobe.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.dao.PostData;
import com.example.sustaintheglobe.databinding.ActivityCreatePostBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.service.LoadingBar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreatePostActivity extends AppCompatActivity {

    public String uid = FirebaseAuth.getInstance().getUid();
    private ActivityCreatePostBinding binding;
    ActivityResultLauncher<Intent> eventBannerImageActivityResultLauncher;

    private ImageView eventBannerImageView;
    private Bitmap eventBannerBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventBannerImageView = binding.postLogoImageView;


        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });


        eventBannerImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getData() != null) {
                                // Get the image URI from the data
                                Uri selectedImage = data.getData();


                                try {
                                    // Load the image into a bitmap
                                    eventBannerBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                    // Resize the bitmap
                                    //eventBannerBitmap = getResizedBitmap(eventBannerBitmap, 500); // Change 500 to the desired size in pixels
                                    // Set the bitmap to the ImageView
                                    eventBannerImageView.setImageBitmap(eventBannerBitmap);
                                    binding.postLogoPlaceholderImageView.setVisibility(View.GONE);
                                    eventBannerImageView.setVisibility(View.VISIBLE);
                                    binding.postLogoImageViewContainer.setVisibility(View.VISIBLE);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (data.getExtras() != null) {
                                // Get the image from the camera intent
                                eventBannerBitmap = (Bitmap) data.getExtras().get("data");
                                // Resize the bitmap
                                //eventBannerBitmap = getResizedBitmap(eventBannerBitmap, 500); // Change 500 to the desired size in pixels
                                // Set the bitmap to the ImageView
                                eventBannerImageView.setImageBitmap(eventBannerBitmap);
                                binding.postLogoPlaceholderImageView.setVisibility(View.GONE);
                                eventBannerImageView.setVisibility(View.VISIBLE);
                                binding.postLogoImageViewContainer.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
        binding.postLogoCV.setOnClickListener(view -> {
            //take gallery permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 or above: Request READ_MEDIA_IMAGES permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                    return;
                }
            } else {
                // Android 12 or below: Request READ_EXTERNAL_STORAGE permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
            }
            chooseFromGallery(0);
        });

        binding.btnPost.setOnClickListener(view -> {
            makePost();
        });
    }

    public void makePost(){
        if (!CheckNetwork.isInternetAvailable(this)){
            return;
        }
        if (eventBannerBitmap == null) {
            Toast.makeText(this, "Please select an image for the post", Toast.LENGTH_SHORT).show();
            binding.postLogoOuterCV.getParent().requestChildFocus(binding.postLogoOuterCV, binding.postLogoOuterCV);
            return;
        }else if(TextUtils.isEmpty(binding.postCaption.getText().toString())){
            binding.postCaption.setError("Caption cannot be empty");
            binding.postCaption.requestFocus();
            return;
        }else{

            LoadingBar loadingBar = new LoadingBar(this,"Creating Post..");
            loadingBar.show();
            //make post
            String taskID = getIntent().getStringExtra("taskID");
            int pointsToIncrement = getIntent().getIntExtra("pointsToIncrement",0);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new post
            PostData postData = new PostData();
            postData.setCaption(binding.postCaption.getText().toString());
            String postID = db.collection("Posts").document().getId();
            postData.setPostID(postID);
            postData.setUserID(uid);
            postData.setPostTime(Timestamp.now());
            postData.setTaskID(taskID);
            db.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    GeoPoint currentUserGeopoint = new GeoPoint(0,0);
                    currentUserGeopoint = task.getResult().getGeoPoint("location");
                    //get the city and country of the user
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                currentUserGeopoint.getLatitude(),
                                currentUserGeopoint.getLongitude(),
                                1);

                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String city = address.getLocality();
                            String country = address.getCountryName();
                            postData.setCityName(city);
                            postData.setCountryName(country);

                            // Now you have the city and country
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("postMedia/" + postID + "/");
                    // Upload the image to Firebase Storage
                    Bitmap bitmap2 = eventBannerBitmap;
                    if (bitmap2 != null) {

                        String filename2 = "image1.png";
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos2);
                        byte[] byteArray2 = baos2.toByteArray();
                        imageRef.child(filename2).putBytes(byteArray2).addOnCompleteListener(task5 -> {
                            if (task5.isSuccessful()) {
                                // If the image is uploaded successfully, get the download URL
                                imageRef.child(filename2).getDownloadUrl().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // If the download URL is received, set the image link to the PostData object
                                        postData.setImageLink(task1.getResult().toString());
                                        // Add the post to the database
                                        db.collection("Posts").document(postID).set(postData).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                // If the post is added successfully, finish the activity

                                                db.collection("Users/"+uid+"/allUserTasks").document(taskID).update("postID",postID).addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()) {
                                                        //append task completedTasks array in the user
                                                        DocumentReference userRef = db.collection("Users").document(uid);


                                                        userRef.update("points", FieldValue.increment(pointsToIncrement)).addOnCompleteListener(task4 -> {
                                                            if (task4.isSuccessful()) {
                                                                // Notify the listener that the operation was successful
                                                                userRef.update("completedTasks", FieldValue.arrayUnion(taskID))
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            // Notify the listener that the operation was successful
                                                                            loadingBar.dismiss();
                                                                            binding.postParent.setVisibility(View.GONE);
                                                                            binding.completedPostParent.setVisibility(View.VISIBLE);
                                                                            binding.animView.playAnimation();
                                                                            binding.animView.addAnimatorListener(new Animator.AnimatorListener() {
                                                                                @Override
                                                                                public void onAnimationStart(Animator animator) {

                                                                                }

                                                                                @Override
                                                                                public void onAnimationEnd(Animator animator) {
                                                                                    finish();
                                                                                }

                                                                                @Override
                                                                                public void onAnimationCancel(Animator animator) {

                                                                                }

                                                                                @Override
                                                                                public void onAnimationRepeat(Animator animator) {

                                                                                }
                                                                            });
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            loadingBar.dismiss();
                                                                            // Notify the listener about the failure
                                                                        });
                                                            }
                                                        });
                                                        // Update the document


                                                    }
                                                });
                                            }
                                            loadingBar.dismiss();
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });




        }
    }
    public void chooseFromGallery(int isOrganiserLogo) {
        if (isOrganiserLogo == 1) {
            // Code for selecting image for organizer logo
        } else {
            // Create an intent to select image from gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // Create an intent to capture image from camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Check if there is a camera app available to handle the camera intent
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // If camera app is available, create a chooser with both gallery and camera intents
                Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
                eventBannerImageActivityResultLauncher.launch(chooserIntent);
            } else {
                // If no camera app is available, launch only the gallery intent
                eventBannerImageActivityResultLauncher.launch(galleryIntent);
            }
        }
    }


}