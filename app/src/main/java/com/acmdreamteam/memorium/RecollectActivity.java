package com.acmdreamteam.memorium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.acmdreamteam.memorium.Adapter.JournalAdapter;
import com.acmdreamteam.memorium.Model.Journal;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecollectActivity extends AppCompatActivity {

    TextView name,age,marsta,sibsta;

    Dialog dialog;

    ProgressDialog loadingBar;



    private static final int RC_PHOTO_PICKER =  105;

    StorageReference storageReference;


    CardView journal_card,logout_card;

    FirebaseFirestore firestore;

    FirebaseUser firebaseUser;

    FloatingActionButton edit_dp;

    CircleImageView profileImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recollect);


        journal_card = findViewById(R.id.journal_card);

        loadingBar = new ProgressDialog(this);


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        marsta = findViewById(R.id.marsta);
        sibsta = findViewById(R.id.sibsta);

        edit_dp = findViewById(R.id.edit_dp);

        profileImage = findViewById(R.id.profile_image_);

        logout_card = findViewById(R.id.logout_card);


        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username_ = document.getString("username");
                        String age_ = document.getString("age");
                        String marsta_ = document.getString("Married");
                        String spouse_name = document.getString("Spouse_Name");
                        String sibsta_ = document.getString("Have Siblings");
                        String imageUrl = document.getString("imageURL");

                        name.setText("Name: " + username_);
                        age.setText("Date of Birth: " + age_);

                        if(marsta_.equals("Married")){
                            marsta.setText("Marital Status: " + marsta_ +" to " + spouse_name);
                        }else {
                            marsta.setText("Marital Status: " + marsta_);
                        }

                        sibsta.setText("Has Siblings?: " + sibsta_);

                        if(Objects.equals(imageUrl, "user")){
                            Glide.with(getApplicationContext()).load(getApplicationContext().getDrawable(R.drawable.ic_baseline_account_circle_24)).into(profileImage);
                        }else {
                            Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
                        }


                    }
                }
            }
        });




        dialog = new Dialog(this);



        journal_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    journal_card.setCardBackgroundColor(getResources().getColor(R.color.onClick));

                }else {
                    journal_card.setCardBackgroundColor(getResources().getColor(R.color.white));

                }
                return false;
            }
        });

        journal_card.setOnClickListener(v -> {
            Intent intent = new Intent(RecollectActivity.this, JournalViewActivity.class);

            startActivity(intent);


        });

        storageReference = FirebaseStorage.getInstance().getReference("/ProfileImages/"+ firebaseUser.getUid());


//        edit_dp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onMediaSelect();
//            }
//        });


        logout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RecollectActivity.this,StartActivity.class));
            }
        });





    }


//    private void onMediaSelect(){
//
//
//        AppCompatButton gallery,camera;
//
//        CardView cancel;
//
//        dialog.setContentView(R.layout.add_image_pop_up);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        Window window = dialog.getWindow();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        lp.copyFrom(window.getAttributes());
//        //This makes the dialog take up the full width
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);
//
//
//        dialog.setCancelable(true);
//
//
//
//        gallery = dialog.findViewById(R.id.gallery);
//
//        camera = dialog.findViewById(R.id.camera);
//
//        cancel = dialog.findViewById(R.id.cancel);
//
//        gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
//                        RC_PHOTO_PICKER);
//                dialog.dismiss();
//            }
//        });
//
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Camera!",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//
//
//
//
//
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.show();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//
//        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            CropImage.activity(imageUri)
//                    .setGuidelines(CropImageView.Guidelines.OFF)
//                    .setAspectRatio(1, 1)
//                    .start(this);
//
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//                loadingBar.setTitle("Profile Image");
//                loadingBar.setMessage("Please wait, while we update your profile picture...");
//                loadingBar.show();
//                loadingBar.setCanceledOnTouchOutside(false);
//                assert result != null;
//                Uri resultUri = result.getUri();
//
//                StorageReference filepath = storageReference.child(System.currentTimeMillis()
//                        + "." + getFileExtension(resultUri));
//
//
//                StorageTask uploadTask = filepath.putFile(resultUri);
//                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return filepath.getDownloadUrl();
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//
//
//
//                            Uri downloadUri = task.getResult();
//                            assert downloadUri != null;
//                            String mUri = downloadUri.toString();
//
//
//                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put("imageURL", mUri);
//                            firestore.collection("users").document(firebaseUser.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    loadingBar.dismiss();
//                                }
//                            });
//
//                            loadingBar.dismiss();
//
//                            recreate();
//
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
//                loadingBar.dismiss();
//            }
//
//
//        }
//    }
//
//    private String getFileExtension(Uri uri){
//        ContentResolver contentResolver = RecollectActivity.this.getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(RecollectActivity.this, MainActivity.class));
    }
}