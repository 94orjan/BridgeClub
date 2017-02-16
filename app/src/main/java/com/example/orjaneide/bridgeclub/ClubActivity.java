package com.example.orjaneide.bridgeclub;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class ClubActivity extends AppCompatActivity {

    private ImageButton phonecallButton;
    private ImageButton emailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        final int phoneNumber = 99442211;

        final String email = "melk@gmail.com";

        phonecallButton = (ImageButton) findViewById(R.id.phone_call_imageButton);
        emailButton = (ImageButton) findViewById(R.id.email_imageButton);

        phonecallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCallIntent(phoneNumber);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailIntent(email);
            }
        });
    }

    private void makePhoneCallIntent(int phoneNumer) {
        Intent phoneCallIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumer));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(phoneCallIntent);
    }

    private void sendEmailIntent(String email){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ClubActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}
