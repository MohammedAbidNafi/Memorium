package com.acmdreamteam.memorium;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.EventListener;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class JournalReadActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;

    TextView title,date,things;

    String journalID,Title = "Loading...";

    Intent intent;



    ShortcutManager shortcutManager;


    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_read);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();

        intent = getIntent();
        journalID = intent.getStringExtra("journalID");

        JournalReadActivity.this.setTitle(Title);

        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        things = findViewById(R.id.things);

        toolbar = findViewById(R.id.toolbar);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
        }


        LoadData();

    }


    private void LoadData(){

        firestore.collection("user_data").document(firebaseUser.getUid()).collection("journal").document(journalID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String Title_ = document.getString("title");
                        String Date_ = document.getString("date");
                        String Things = document.getString("things");
                        Title = Title_;




                        title.setText(Title_);
                        date.setText(Date_);
                        things.setText(Things);


                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.create_shortcut:


                Shortcuts(journalID,Title,getDrawable(R.drawable.journal));


                break;

            case R.id.edit:

                    InitiateEdit(journalID);

                break;

            case R.id.Delete:



                /*
                iOSDialog.Builder
                        .with(this)
                        .setTitle("Delete Journal")
                        .setMessage("Are you sure you want to delete this journal?")
                        .setPositiveText("Yes")
                        .setPostiveTextColor(getResources().getColor(com.margsapp.iosdialog.R.color.red))
                        .setNegativeText("No")
                        .setNegativeTextColor(getResources().getColor(com.margsapp.iosdialog.R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                DeleteJournal(journalID);
                            }
                        })
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do Nothing
                            }
                        })
                        .isCancellable(true)
                        .build()
                        .show();

                 */

                break;

        }

        return false;
    }

    private void InitiateEdit(String JournalID) {

        Intent intent = new Intent(JournalReadActivity.this,JournalEditActivity.class);
        intent.putExtra("JournalID",JournalID);
        startActivity(intent);

    }

    private void DeleteJournal(String journalID) {

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("user_data").document(firebaseUser.getUid()).collection("journal").document(journalID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        });

    }

    private void Shortcuts(String journalID, String username, Drawable imageUrl){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Bitmap bitmap = ((BitmapDrawable)imageUrl).getBitmap();
            if(shortcutManager.isRequestPinShortcutSupported()){
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(this, username)
                                .setShortLabel(username)
                                .setLongLabel(username)
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntents(
                                        new Intent[]{
                                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, JournalReadActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("journalID",journalID),
                                        })
                                .build();


                Intent pinnedShortcutCallbackIntent =
                        shortcutManager.createShortcutResultIntent(pinShortcutInfo);

                PendingIntent successCallback = PendingIntent.getBroadcast(this, /* request code */ 1,
                        pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE);

                shortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }else {
                Toast.makeText(this,"Your current Android Version dosent support Shortcuts.",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"Your current Android Version dosent support Shortcuts.",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        LoadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}