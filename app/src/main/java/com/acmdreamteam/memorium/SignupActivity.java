package com.acmdreamteam.memorium;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.acmdreamteam.memorium.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {


    EditText username_,age_;


    AppCompatButton submit;

    RadioButton male,female;

    Dialog dialog;

    ProgressDialog loadingBar;

    String mUri;

    private static final int RC_PHOTO_PICKER =  105;

    StorageReference storageReference;


    String gender;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    User user;

    FloatingActionButton edit_dp;

    ImageView profile_image;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        username_ = findViewById(R.id.username);
        age_ = findViewById(R.id.age);

        submit = findViewById(R.id.submit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dialog = new Dialog(this);

        mUri = "user";


        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("Select the date");
        materialDateBuilder.setPositiveButtonText("Ok");
        materialDateBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        materialDateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

        CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
        calendarConstraintsBuilder.setValidator(DateValidatorPointBackward.now());

        materialDateBuilder.setCalendarConstraints(calendarConstraintsBuilder.build());
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();



        age_.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    materialDatePicker.show(getSupportFragmentManager(),"Date of birth");
                }
            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                long date = materialDatePicker.getSelection();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(date);

                age_.setText(formattedDate);
            }
        });

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        edit_dp = findViewById(R.id.edit_dp);

        profile_image = findViewById(R.id.profile_image_);
        storageReference = FirebaseStorage.getInstance().getReference("/ProfileImages/"+ firebaseUser.getUid());


        loadingBar = new ProgressDialog(this);


//        edit_dp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onMediaSelect();
//            }
//        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                submit_Data();
            }
        });





    }

    @SuppressLint("NonConstantResourceId")
    public void onGenderSelect(View view){
        switch (view.getId()) {

            case R.id.male:
                if (male.isChecked()) {

                    gender = "male";

                }
                break;

            case R.id.female:
                if (female.isChecked()) {

                    gender = "female";


                }
                break;



        }
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
//                            Uri downloadUri = task.getResult();
//                            assert downloadUri != null;
//                            mUri = downloadUri.toString();
//
//
//                            Glide.with(getApplicationContext()).load(mUri).into(profile_image);
//
//                            loadingBar.dismiss();
//
//
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

//    private String getFileExtension(Uri uri){
//        ContentResolver contentResolver = SignupActivity.this.getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }

    private void submit_Data() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> user = new HashMap<>();
        user.put("username", username_.getText().toString());
        user.put("age",age_.getText().toString());
        user.put("gender",gender);
//        user.put("imageURL",mUri);


        assert firebaseUser != null;
        db.collection("users").document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        startActivity(new Intent(SignupActivity.this,QuestionnaireActivity.class));
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
}