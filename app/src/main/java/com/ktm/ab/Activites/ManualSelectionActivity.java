package com.ktm.ab.Activites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ktm.ab.API.RetrofitAPI;
import com.ktm.ab.R;
import com.ktm.ab.Util.Constants;
import com.ktm.ab.Util.SharedDataUtils;
import com.ktm.ab.Util.UIUtil;
import com.ktm.ab.dialog.DialogType;
import com.ktm.ab.dialog.IQDialogListener;
import com.ktm.ab.dialog.IQTextMessageDialog;
import com.ktm.ab.model.UserInfo;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.ktm.ab.Activites.LoginActivity.INTERNET_CONNECTION_ERROR;
import static com.ktm.ab.Activites.LoginActivity.INVALID_ERROR;
import static com.ktm.ab.Activites.LoginActivity.INVALID_NUMBER_ERROR;

public class ManualSelectionActivity extends AppCompatActivity {

    @BindView(R.id.btnOwnerManual200)
    LinearLayout btnOwnerManual200;

    @BindView(R.id.btnOwnerManual250)
    LinearLayout btnOwnerManual250;

    @BindView(R.id.btnOwnerManual390)
    LinearLayout btnOwnerManual390;

    private Dialog dialog;
    private CircularProgressBar circularProgressBar;
    private String zipFileName = "";

    private TextView downloadTitle;
    private TextView tvNewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_selection);
        ButterKnife.bind(this);

        initUI();
        checkForVersionChange();

    }

    private void initUI() {
        btnOwnerManual200.setVisibility(TextUtils.isEmpty(SharedDataUtils.getStringFields(this,
                Constants.Pref.DUKE_200_URL)) ? View.GONE : View.VISIBLE);

        btnOwnerManual250.setVisibility(TextUtils.isEmpty(SharedDataUtils.getStringFields(this,
                Constants.Pref.DUKE_250_URL)) ? View.GONE : View.VISIBLE);

        btnOwnerManual390.setVisibility(TextUtils.isEmpty(SharedDataUtils.getStringFields(this,
                Constants.Pref.DUKE_390_URL)) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.btn200)
    public void KTMManual200() {
        zipFileName = "DUKE_200";
        SharedDataUtils.addStringFields(this, Constants.Pref.CURRENT_PATH, zipFileName);
        Log.e("DUKE_200_IS_DOWNLOAD", "DUKE_200_IS_DOWNLOAD ---------- " + SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_200_IS_DOWNLOAD));
        if (!SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_200_IS_DOWNLOAD) ||
                SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_200_NEW_VERSION)) {
            if (UIUtil.isInternetAvailable(this)) {
                openDownloadDialog();
                tvNewVersion.setVisibility(SharedDataUtils.getIntFields(this, Constants.Pref.DUKE_200_URL_VERSION) > 1 ? View.VISIBLE : View.GONE);
                new DownloadFileFromURL().execute(SharedDataUtils.getStringFields(this, Constants.Pref.DUKE_200_URL));
            } else {
                Toast.makeText(this, "Please connect to internet to continue!", Toast.LENGTH_SHORT).show();
            }
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    @OnClick(R.id.btn250)
    public void KTMManual250() {
        zipFileName = "DUKE_250";
        Log.e("DUKE_250_IS_DOWNLOAD", "DUKE_250_IS_DOWNLOAD ---------- " + SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_250_IS_DOWNLOAD));

        SharedDataUtils.addStringFields(this, Constants.Pref.CURRENT_PATH, zipFileName);
        if (!SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_250_IS_DOWNLOAD) ||
                SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_250_NEW_VERSION)) {
            if (UIUtil.isInternetAvailable(this)) {
                openDownloadDialog();
                tvNewVersion.setVisibility(SharedDataUtils.getIntFields(this, Constants.Pref.DUKE_250_URL_VERSION) > 1 ? View.VISIBLE : View.GONE);
                new DownloadFileFromURL().execute(SharedDataUtils.getStringFields(this, Constants.Pref.DUKE_250_URL));
            } else {
                Toast.makeText(this, "Please connect to internet to continue!", Toast.LENGTH_SHORT).show();
            }
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    @OnClick(R.id.btn390)
    public void KTMManual390() {
        zipFileName = "DUKE_390";
        Log.e("DUKE_390_IS_DOWNLOAD", "DUKE_390_IS_DOWNLOAD ---------- " + SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_390_IS_DOWNLOAD));

        SharedDataUtils.addStringFields(this, Constants.Pref.CURRENT_PATH, zipFileName);

        if (!SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_390_IS_DOWNLOAD) ||
                SharedDataUtils.getBooleanFields(this, Constants.Pref.DUKE_390_NEW_VERSION)) {
            if (UIUtil.isInternetAvailable(this)) {
                openDownloadDialog();
                tvNewVersion.setVisibility(SharedDataUtils.getIntFields(this, Constants.Pref.DUKE_390_URL_VERSION) > 1 ? View.VISIBLE : View.GONE);
                new DownloadFileFromURL().execute(SharedDataUtils.getStringFields(this, Constants.Pref.DUKE_390_URL));
            } else {
                Toast.makeText(this, "Please connect to internet to continue!", Toast.LENGTH_SHORT).show();
            }
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    private void openDownloadDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_download);
        ButterKnife.bind(dialog);
        circularProgressBar = (CircularProgressBar) dialog.findViewById(R.id.progressBar);
        downloadTitle = (TextView) dialog.findViewById(R.id.tvDownloadTitle);
        tvNewVersion = (TextView) dialog.findViewById(R.id.tvNewVersion);

        dialog.show();
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadTitle.setText("Connecting...");
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                String oldFile = ManualSelectionActivity.this.getFilesDir() + "/" + zipFileName;
                deleteRecursive(new File(oldFile));

                Log.e("URL", "URl :: " + f_url[0]);
                URL url = new URL(f_url[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                Log.e("URL", "lengthOfFile :: " + lengthOfFile);
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream
                OutputStream output = openFileOutput(zipFileName + "ZIP.zip", Context.MODE_PRIVATE);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                String zipFilePath = ManualSelectionActivity.this.getFilesDir() + "/" + zipFileName + "ZIP.zip";
                String filePath = ManualSelectionActivity.this.getFilesDir() + "/" + zipFileName;

                try {
                    unzip(new File(zipFilePath), new File(filePath));
                    Log.e("File", "unzip -------------- complete");
//                    Log.e("File", "delete -------------- delete : " + new File(zipFilePath).delete());

                    if (zipFileName.equalsIgnoreCase("DUKE_200")) {
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_IS_DOWNLOAD, true);
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_NEW_VERSION, false);
                    } else if (zipFileName.equalsIgnoreCase("DUKE_250")) {
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_IS_DOWNLOAD, true);
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_NEW_VERSION, false);
                    } else if (zipFileName.equalsIgnoreCase("DUKE_390")) {
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_IS_DOWNLOAD, true);
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_NEW_VERSION, false);
                    }


                    Log.e("DUKE_200_IS_DOWNLOAD", "DUKE_200_IS_DOWNLOAD ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_IS_DOWNLOAD));

                    Log.e("DUKE_200_NEW_VERSION", "DUKE_200_NEW_VERSION ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_NEW_VERSION));


                    Log.e("DUKE_250_IS_DOWNLOAD", "DUKE_250_IS_DOWNLOAD ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_IS_DOWNLOAD));

                    Log.e("DUKE_250_NEW_VERSION", "DUKE_250_NEW_VERSION ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_NEW_VERSION));


                    Log.e("DUKE_390_IS_DOWNLOAD", "DUKE_390_IS_DOWNLOAD ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_IS_DOWNLOAD));

                    Log.e("DUKE_390_NEW_VERSION", "DUKE_390_NEW_VERSION ---------- " +
                            SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_NEW_VERSION));


                    return "true";
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        protected void onProgressUpdate(String... progress) {
            downloadTitle.setText(getString(R.string.download) + Integer.parseInt(progress[0]) + "%");
            circularProgressBar.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String file_url) {
            if ("true".equalsIgnoreCase(file_url)) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
            dialog.dismiss();
        }

    }


    public void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    private void checkForVersionChange() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone_number", SharedDataUtils.getStringFields(this, Constants.Pref.PHONE_NUMBER));

        RetrofitAPI.getInstance().getApi().signIn(Constants.TOKEN, jsonObject, new Callback<UserInfo>() {
            @Override
            public void success(UserInfo userInfo, Response response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                if (userInfo.getStatus() == 0) {
                    SharedDataUtils.clearData(getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                    return;
                }

                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_200_URL, userInfo.getDuke200());
                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_250_URL, userInfo.getDuke250());
                SharedDataUtils.addStringFields(getApplicationContext(), Constants.Pref.DUKE_390_URL, userInfo.getDuke390());
                int ver200 = SharedDataUtils.getIntFields(getApplicationContext(), Constants.Pref.DUKE_200_URL_VERSION);
                if (userInfo.getDuke200Version() > ver200) {
                    if (ver200 != 0)
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_NEW_VERSION, true);
                    SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_200_URL_VERSION, userInfo.getDuke200Version());
                }

                int ver250 = SharedDataUtils.getIntFields(getApplicationContext(), Constants.Pref.DUKE_250_URL_VERSION);
                if (userInfo.getDuke250Version() > ver250) {
                    if (ver250 != 0)
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_NEW_VERSION, true);
                    SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_250_URL_VERSION, userInfo.getDuke250Version());
                }

                int ver390 = SharedDataUtils.getIntFields(getApplicationContext(), Constants.Pref.DUKE_390_URL_VERSION);
                if (userInfo.getDuke390Version() > ver390) {
                    if (ver390 != 0)
                        SharedDataUtils.addBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_NEW_VERSION, true);
                    SharedDataUtils.addIntFields(getApplicationContext(), Constants.Pref.DUKE_390_URL_VERSION, userInfo.getDuke390Version());
                }

                Log.e("DUKE_200_IS_DOWNLOAD", "DUKE_200_IS_DOWNLOAD ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_IS_DOWNLOAD));

                Log.e("DUKE_200_NEW_VERSION", "DUKE_200_NEW_VERSION ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_200_NEW_VERSION));


                Log.e("DUKE_250_IS_DOWNLOAD", "DUKE_250_IS_DOWNLOAD ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_IS_DOWNLOAD));

                Log.e("DUKE_250_NEW_VERSION", "DUKE_250_NEW_VERSION ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_250_NEW_VERSION));


                Log.e("DUKE_390_IS_DOWNLOAD", "DUKE_390_IS_DOWNLOAD ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_IS_DOWNLOAD));

                Log.e("DUKE_390_NEW_VERSION", "DUKE_390_NEW_VERSION ---------- " +
                        SharedDataUtils.getBooleanFields(getApplicationContext(), Constants.Pref.DUKE_390_NEW_VERSION));

                initUI();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }


}
