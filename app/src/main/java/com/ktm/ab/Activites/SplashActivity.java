package com.ktm.ab.Activites;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.ktm.ab.R;
import com.ktm.ab.Util.Constants;
import com.ktm.ab.Util.SharedDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.btnSignUp)
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        signUp.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(SharedDataUtils.getStringFields(getApplicationContext(), Constants.Pref.PHONE_NUMBER))) {
                    signUp.setVisibility(View.VISIBLE);
                    return;
                }
                startActivity(new Intent(getApplicationContext(), ManualSelectionActivity.class));
                finish();
            }
        }, Constants.SPLASH_TIME_OUT);
    }

    @OnClick(R.id.btnSignUp)
    public void SignUp() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}
