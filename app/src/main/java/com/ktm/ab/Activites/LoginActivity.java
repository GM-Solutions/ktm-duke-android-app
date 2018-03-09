package com.ktm.ab.Activites;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.ktm.ab.API.RetrofitAPI;
import com.ktm.ab.R;
import com.ktm.ab.model.UserInfo;
import com.ktm.ab.Util.Constants;
import com.ktm.ab.Util.SharedDataUtils;
import com.ktm.ab.Util.UIUtil;
import com.ktm.ab.dialog.DialogType;
import com.ktm.ab.dialog.IQDialogListener;
import com.ktm.ab.dialog.IQTextMessageDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity implements IQDialogListener {

    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.imageButton)
    ImageView submitButton;
    @BindView(R.id.et_phone_no)
    EditText phoneNumber;

    @BindView(R.id.number_layout)
    RelativeLayout numberLayout;

    public static final String INTERNET_CONNECTION_ERROR = "Please check your internet connection and try again";
    public static final String INVALID_NUMBER_ERROR = "Invalid Number please give the number given to the KTM or contact our customer care to change the number";
    public static final String INVALID_ERROR = "Sorry, this request could not be processed. Please try again later.";
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.imageButton)
    public void login() {

        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phoneNumber.setError("Enter mobile number..");
            return;
        }
        if (phoneNumber.getText().toString().length() < 9) {
            phoneNumber.setError("Invalid mobile number");
            return;
        }

        if (!UIUtil.isInternetAvailable(this)) {
            promptInternetError(getString(R.string.network_problem));
            return;
        }
        UIUtil.startProgressDialog(this, "Please wait login...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone_number", phoneNumber.getText().toString());

        RetrofitAPI.getInstance().getApi().signIn(Constants.TOKEN, jsonObject, new Callback<UserInfo>() {
            @Override
            public void success(UserInfo userInfo, Response response) {
                UIUtil.stopProgressDialog(getApplicationContext());

                Log.e("userInfo", userInfo.toString());
                if (userInfo.getStatus() == 0) {
                    Log.e("userInfo", userInfo.toString());
                    promptInternetError(getString(R.string.invalid_number));
                    return;
                }

                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.PHONE_NUMBER, phoneNumber.getText().toString());
                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.CUSTOMER_NAME, userInfo.getUserName());

                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_200_URL, userInfo.getDuke200());
                SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_200_URL_VERSION, userInfo.getDuke200Version());

                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_250_URL, userInfo.getDuke250());
                SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_250_URL_VERSION, userInfo.getDuke250Version());

                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_390_URL, userInfo.getDuke390());
                SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_390_URL_VERSION, userInfo.getDuke390Version());

                startActivity(new Intent(getApplicationContext(), ManualSelectionActivity.class));
                finish();

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e("userInfo", error.toString());
                UIUtil.stopProgressDialog(getApplicationContext());
                promptInternetError(getString(R.string.invalid_number));
            }
        });

    }

    @OnClick(R.id.back_button)
    public void goBack() {
        finish();
    }


    public void promptInternetError(String string) {

        error = string;
        if (error.equalsIgnoreCase(getString(R.string.network_problem))) {
            IQTextMessageDialog dialog = new IQTextMessageDialog();
            dialog.init(DialogType.PROMPT_LOGIN_REGISTER, INTERNET_CONNECTION_ERROR, false, "Goto Settings", true, "Cancel");
            dialog.setiQDialogListener(this);
            dialog.show(getSupportFragmentManager(), "");
        } else if (error.equalsIgnoreCase(getString(R.string.invalid_number))) {
            IQTextMessageDialog dialog = new IQTextMessageDialog();
            dialog.init(DialogType.PROMPT_LOGIN_REGISTER, INVALID_NUMBER_ERROR, false, "Call Customer Care", true, "Cancel");
            dialog.setiQDialogListener(this);
            dialog.show(getSupportFragmentManager(), "");
        } else if (error.equalsIgnoreCase(getString(R.string.error_number))) {
            IQTextMessageDialog dialog = new IQTextMessageDialog();
            dialog.init(DialogType.PROMPT_LOGIN_REGISTER, INVALID_ERROR, false, "Call Customer Care", true, "Cancel");
            dialog.setiQDialogListener(this);
            dialog.show(getSupportFragmentManager(), "");
        }
    }


    @Override
    public void onIQDialogSelection(int mDialogType, Object mDialogData) {
        Log.e("onIQDialogSelection", "onIQDialogSelection --");
    }

    @Override
    public void onIQDialogOk(int mDialogType, Object mDialogData) {
        Log.e("onIQDialogOk", "onIQDialogOk --");
        if (error.equalsIgnoreCase(getString(R.string.network_problem))) {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else if (error.equalsIgnoreCase(getString(R.string.invalid_number))) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constants.CUSTOMER_CARE_NUMBER));
            startActivity(intent);
        }
    }

    @Override
    public void onIQDialogCancel(int mDialogType, boolean onBack) {
        Log.e("onIQDialogCancel", "onIQDialogCancel --");
    }


}
