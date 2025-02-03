package com.acmdreamteam.memorium;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.andrognito.flashbar.Flashbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class JournalAddActivity extends AppCompatActivity {


    EditText title,things;
    CardView date_card;
    TextView date_txt;

    Button submit;

    FirebaseFirestore firestore;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_add);


        title = findViewById(R.id.title);
        things = findViewById(R.id.things);
        submit = findViewById(R.id.submit);

        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();





        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("Select the date");
        materialDateBuilder.setPositiveButtonText("Ok");
        materialDateBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        materialDateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

        CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
        calendarConstraintsBuilder.setValidator(DateValidatorPointBackward.now());

        materialDateBuilder.setCalendarConstraints(calendarConstraintsBuilder.build());
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();





        date_card = findViewById(R.id.date_card);
        date_txt = findViewById(R.id.date_txt);


        date_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String strDate = dateFormat.format(selection);
                date_txt.setText(strDate);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });




    }

    private void submitData() {
        String JournalID = Randomizer(12);
        Map<String, Object> journal = new HashMap<>();
        journal.put("title",title.getText().toString());
        journal.put("things", things.getText().toString());
        journal.put("date", date_txt.getText().toString());
        journal.put("journalID",JournalID);





        firestore.collection("user_data").document(firebaseUser.getUid()).collection("journal").document(JournalID)
                .set(journal)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                        finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


    private String Randomizer(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890" + "abcdefghijklmnopqrstuvxyz";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string

        for(int i = 0; i < n; i++) {

            // generate random index number
            int index = random.nextInt(AlphaNumericString.length());

            // get character specified by index
            // from the string
            char randomChar = AlphaNumericString.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }


        return sb.toString();
    }
}