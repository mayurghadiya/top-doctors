package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
    private String loginId;

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private ProgressDialog mProgressDialog;

    //fb login
    private String userId, token, email, name, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

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

        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        //set read permission
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends", "user_location"));

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // insert in db with isSocial flag and token
                token = loginResult.getAccessToken().getToken();
                userId = loginResult.getAccessToken().getUserId();

                /*info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );*/

                //App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    email = object.getString("email");
                                    name = object.getString("name");
                                    location = object.getJSONObject("location").toString();
                                    loginButton.setText(getResources().getString(R.string.logout));
                                    //insert db
                                    facebookLogin(userId, name, token, email, location);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login attempt failed.");
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });


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
        loginButton.setTypeface(face1);

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

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                    loginId = json.getString("id");
                    Log.v("Login Id", loginId);
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                } catch(JSONException e) {

                } finally {
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
                            Preference.setValue(LoginActivity.this, "LOGIN_ID", loginId);
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
            etEmail.setError(getResources().getString(R.string.valid_email_error));
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

    public void facebookLogin(final String userId, final String name, final String token,
                              final String email, final String location) {

        class FacebookLogin extends AsyncTask<String, Void, String> {
            final String URL = AppConfig.getWebServiceUrl() + "user/facebookLogin";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickUserId = userId;
            String quickName = name;
            String quickToken = token;
            String quickEmail = email;
            String quickLocation = location;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("name", quickName));
                nameValuePairs.add(new BasicNameValuePair("token", quickToken));
                nameValuePairs.add(new BasicNameValuePair("email", quickEmail));
                nameValuePairs.add(new BasicNameValuePair("location", quickLocation));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                Toast.makeText(LoginActivity.this, "You have successfully loggedin", Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.v("Fb Login response", result);

                    //store in sharedpreference
                    Preference.setValue(LoginActivity.this, "PREF_FNAME", jsonObject.getString("name"));
                    Preference.setValue(LoginActivity.this, "PREF_ADDRESS", "India");
                    Preference.setValue(LoginActivity.this, "LOGIN_ID", jsonObject.getString("loginId"));
                    Preference.setValue(LoginActivity.this, "PREF_ISLOGIN", "true");

                    //start new activity
                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("userName", jsonObject.getString("name"));
                    extras.putString("userAddress", "India");
                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        FacebookLogin facebookLogin = new FacebookLogin();
        facebookLogin.execute(userId, name, token, email, location);
    }
}
