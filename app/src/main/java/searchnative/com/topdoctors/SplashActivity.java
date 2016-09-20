package searchnative.com.topdoctors;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;

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
        if(!Preference.getValue(SplashActivity.this, "PREF_FNAME", "").equals("")) {
            Toast.makeText(this, "Welcome back, " + Preference.getValue(SplashActivity.this, "PREF_FNAME", ""),
                    Toast.LENGTH_SHORT).show();
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        };
        thread.start();
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            //check for internet

            String username = Preference.getValue(SplashActivity.this, "PREF_FNAME", "");
            if(username.equals("")) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(SplashActivity.this, BaseActivity.class);
                startActivity(intent);
                //intent = new Intent(SplashActivity.this, LoginActivity.class);
                //startActivity(intent);
            }
            finish();
        }
    };

}
