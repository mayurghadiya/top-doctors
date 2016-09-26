package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static String loginUserName;
    private static String loginAddress;

    TextView tvTitle, tvForgotPassword;
    EditText etEmail, etPassword;
    String getEmail, getPassword;
    Button btnLogin, btnRegister, btnGuest, btnFacebook;
    Boolean isValidate = true;
    String resMessage = "";
    String resStatus;
    String webServiceUrl = AppConfig.getWebServiceUrl();
    String DataParseUrl = webServiceUrl + "user/login";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        loginUserName = "";
        loginAddress = "";

        tvTitle = (TextView) findViewById(R.id.textViewLogin);
        tvForgotPassword = (TextView) this.findViewById(R.id.textViewForgotPassword);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnGuest = (Button) findViewById(R.id.enter_as_a_guest);
        btnFacebook = (Button) findViewById(R.id.login_with_facebook);

        //set the font style
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ExoBlack.otf");
        tvTitle.setTypeface(face);
        btnLogin.setTypeface(face);

        //set font style ExoMedium
        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");
        tvForgotPassword.setTypeface(face1);
        etEmail.setTypeface(face1);
        etPassword.setTypeface(face1);
        btnRegister.setTypeface(face1);
        btnGuest.setTypeface(face1);
        btnFacebook.setTypeface(face1);

        //text transform method
        btnRegister.setTransformationMethod(null);
        btnGuest.setTransformationMethod(null);
        btnFacebook.setTransformationMethod(null);

        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    builder.setTitle(getResources().getString(R.string.guest_login))
                            .setMessage(getResources().getString(R.string.coming_soon))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(R.string.close),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
            }
        });

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    //Toast.makeText(getApplicationContext(), "unfocus", Toast.LENGTH_LONG).show();
                    etEmail.setGravity(Gravity.LEFT);
                    if(etEmail.length() != 0) {
                        etEmail.setSelection(0);
                    }
                }
            }
        });

        //register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide keyborad
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //check for validation
                checkValidation();
                if(isValidate) {
                    getData();
                    sendDataToServer(getEmail, getPassword);
                }

            }
        });
    }

    public static String getLoginUserName() {
        return loginUserName;
    }

    public static void setLoginUserName(String loginUserName1){
        LoginActivity.loginUserName = loginUserName1;
    }

    public static String getLoginAddress() {
        return loginAddress;
    }

    public static void setLoginAddress(String loginAddress1) {
        LoginActivity.loginAddress = loginAddress1;
    }

    public void getData() {
        getEmail = etEmail.getText().toString();
        getPassword = etPassword.getText().toString();
    }

    public void sendDataToServer(final String email, final String password) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String quickEmail = email;
                String quickPassword = password;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", quickEmail));
                nameValuePairs.add(new BasicNameValuePair("password", quickPassword));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(DataParseUrl);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpClient.execute(httpPost, responseHandler);
                    JSONObject json = new JSONObject(responseBody);
                    resMessage = json.getString("message");
                    resStatus = json.getString("status");
                    LoginActivity.setLoginUserName(json.getString("first_name") + " " + json.getString("last_name").substring(0,1));
                    LoginActivity.setLoginAddress(json.getString("country"));
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                } catch(JSONException e) {

                }
                return "Login request successfully serve!";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(resMessage == "") {
                    resMessage = "Service is unavailable";
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_LONG).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(resStatus == "true") {
                            Intent intent = new Intent(getBaseContext(), BaseActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("userName", LoginActivity.getLoginUserName());
                            extras.putString("userAddress", LoginActivity.getLoginAddress());

                            //store in sharedpreference
                            Preference.setValue(LoginActivity.this, "PREF_FNAME", LoginActivity.getLoginUserName());
                            Preference.setValue(LoginActivity.this, "PREF_ADDRESS", LoginActivity.getLoginAddress());
                            Preference.setValue(LoginActivity.this, "PREF_ISLOGIN", "true");

                            intent.putExtras(extras);
                            finish();
                            startActivity(intent);
                        }
                    }
                }, 2000);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(LoginActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email, password);
    }

    public void checkValidation() {
        isValidate = true;
        if(etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError(getResources().getString(R.string.email_error));
            isValidate = false;
        }
        if(etPassword.getText().toString().trim().length() == 0) {
            etPassword.setError(getResources().getString(R.string.password_error));
            isValidate = false;
        }
        //check for valid email
        Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
        Matcher matcher1 = pattern1.matcher(etEmail.getText().toString());
        if (!matcher1.matches()) {
            etEmail.setError(getResources().getString(R.string.enter_valid_password));
            isValidate = false;
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
