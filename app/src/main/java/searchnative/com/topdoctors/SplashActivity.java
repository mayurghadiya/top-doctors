package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //set lang
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
        String locale = ims.getLocale();
        LocalInformation.setLocalLang(locale);

        //check for Internet connection
        if(!isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.device_not_connected_to_internet))
                    .setTitle(getResources().getString(R.string.no_internet_connection))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SplashActivity.this.finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            if(!Preference.getValue(SplashActivity.this, "PREF_FNAME", "").equals("")) {
                Toast.makeText(this, "Welcome back, " + Preference.getValue(SplashActivity.this, "PREF_FNAME", ""),
                        Toast.LENGTH_SHORT).show();
            }

            SpecialityData.getmInstance();
            CountryData.getmInstance();
            LocationData.getmInstance();

            Thread thread = new Thread() {

                @Override
                public void run() {
                    try {
                        sleep(7000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            };
            thread.start();
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            clearPreference();
            String username = Preference.getValue(SplashActivity.this, "PREF_FNAME", "");
            if(username.equals("")) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(SplashActivity.this, BaseActivity.class);
                startActivity(intent);
            }
            finish();
        }
    };

    public void clearPreference() {
        Preference.setValue(SplashActivity.this, "GLOBAL_FILTER_TYPE", "");
        Preference.setValue(SplashActivity.this, "DOCTOR_LOCATION_SEARCH", "");
        Preference.setValue(SplashActivity.this, "DOCTOR_SPECIALITY_SEARCH", "");
        Preference.setValue(SplashActivity.this, "DOCTOR_GENDER_SEARCH", "");
        Preference.setValue(SplashActivity.this, "TOP_TEN_DOCTORS", "");
        Preference.setValue(SplashActivity.this, "SEARCH_REDIRECT", "");
        Preference.setValue(SplashActivity.this, "FILTER_NAME", "");

    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }
}
