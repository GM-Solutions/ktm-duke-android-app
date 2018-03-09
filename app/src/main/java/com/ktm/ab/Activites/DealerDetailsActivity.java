package com.ktm.ab.Activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ktm.ab.R;
import com.ktm.ab.databaseModel.Dealer;


public class DealerDetailsActivity extends AppCompatActivity {

    private TextView tvName, tvPhone, tvAddress, tvEmail;
    private Dealer dealer = new Dealer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_details);

        tvName = (TextView) findViewById(R.id.tvName);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvEmail = (TextView) findViewById(R.id.tvEmail);

        dealer = (Dealer) getIntent().getSerializableExtra("dealer");

        tvName.setText(dealer.getName());
        tvAddress.setText(Html.fromHtml((TextUtils.isEmpty(dealer.getAddress()) ? "" : dealer.getAddress()) + ", ")
                + (TextUtils.isEmpty(dealer.getCity()) ? "" : dealer.getCity() + ", ")
                + (TextUtils.isEmpty(dealer.getState()) ? "" : dealer.getState() + ", ")
                + (TextUtils.isEmpty(dealer.getCountry()) ? "" : dealer.getCountry() + ", "));

        tvPhone.setText("Tel: " + (TextUtils.isEmpty(dealer.getTel()) ? "Not available" : dealer.getTel()));
        tvEmail.setText("Email: " + dealer.getEmail());

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + dealer.getLat() + "," + dealer.getLng());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationForMakeCall();
            }
        });

    }


    private void confirmationForMakeCall() {

        if (TextUtils.isEmpty(dealer.getTel())) {
            Toast.makeText(this, "Mobile number is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String message = "Do you want to Call " + dealer.getName() + " on this number " + dealer.getTel() + " ?";
        builder.setMessage(message);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeCall(dealer.getTel());
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
