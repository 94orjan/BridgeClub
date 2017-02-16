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
import android.widget.TextView;
import android.widget.Toast;

import com.example.orjaneide.bridgeclub.model.Club;
import com.example.orjaneide.bridgeclub.model.ClubDao;
import com.example.orjaneide.bridgeclub.model.ClubDaoImpl;

public class ClubActivity extends AppCompatActivity {
    public static final String CLUB_NUMBER = "CLUB_NUMBER";

    private ImageButton phonecallButton;
    private ImageButton emailButton;
    private TextView mClubNameTextView;
    private TextView mAddressTextView;
    private TextView mPlaceTextView;
    private TextView mContactPersonTextView;

    private ClubDao mClubDao = new ClubDaoImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        // Get intent
        Intent intent = getIntent();
        int clubNumber = intent.getIntExtra(CLUB_NUMBER, -1);
        final Club club = mClubDao.findClubById(clubNumber);

        // Initialize views
        phonecallButton = (ImageButton) findViewById(R.id.phone_call_imageButton);
        emailButton = (ImageButton) findViewById(R.id.email_imageButton);
        mClubNameTextView = (TextView) findViewById(R.id.club_name_textView);
        mAddressTextView = (TextView) findViewById(R.id.address_textView);
        mPlaceTextView = (TextView) findViewById(R.id.place_textView);
        mContactPersonTextView = (TextView) findViewById(R.id.contact_person_textView);

        if(club != null) {
            mClubNameTextView.setText(club.getName());
            mAddressTextView.setText(club.getAddress());
            mPlaceTextView.setText(club.getPlace());
            mContactPersonTextView.setText(club.getContactPerson());
        } else {
            Toast.makeText(this, "Didn't find any club with id " + clubNumber, Toast.LENGTH_SHORT).show();
        }

        phonecallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCallIntent(club.getPhone());
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailIntent(club.getEmail());
            }
        });
    }

    private void makePhoneCallIntent(String phoneNumer) {
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
