package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView tvRegister, termsStart, termsEnd, termsNote;
    EditText etFirstName, etLastName, etPassword, etConfirmPassword, etMobile, etEmail, etAddress;
    String getFirstName, getLastName, getPassword, getMobile, getEmail, getAddress;
    CheckBox chkNewsletter, chkTermsAndCondition;
    AutoCompleteTextView actvCountry;
    String getCountry;
    Button btnRegister, btnLoginFacebook;
    private String[] country;
    Boolean isValidate = true;
    String resMessage = "";
    String resStatus;
    private List<String> countryList;
    String webServiceUrl = AppConfig.getWebServiceUrl();
    Spinner countrySpinner;
    ArrayAdapter<String> countryArrayAdapter;
    List<String> countryDataList;
    Typeface face1;

    String DataParseUrl = webServiceUrl + "user/register";

    //String DataParseUrl = "http://192.168.1.28/top-doctors/api/user/register";
    private ProgressDialog mProgressDialog;

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    //fb login
    private String userId, token, email, name, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        tvRegister = (TextView) findViewById(R.id.tv_register);
        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        //actvCountry = (AutoCompleteTextView) findViewById(R.id.actv_country);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etEmail = (EditText) findViewById(R.id.et_email);
        etAddress = (EditText) findViewById(R.id.et_address);
        chkNewsletter = (CheckBox) findViewById(R.id.chk_newsletter);
        chkTermsAndCondition = (CheckBox) findViewById(R.id.chk_terms_and_condition);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLoginFacebook = (Button) findViewById(R.id.login_with_facebook);
        termsNote = (TextView) findViewById(R.id.terms_note);
        termsStart = (TextView) findViewById(R.id.terms_start);
        termsEnd = (TextView) findViewById(R.id.terms_end);

        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button_register);

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

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ExoBlack.otf");
        btnRegister.setTypeface(face);
        tvRegister.setTypeface(face);

        //set font style ExoMedium
        face1 = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");
        etFirstName.setTypeface(face1);
        etLastName.setTypeface(face1);
        etPassword.setTypeface(face1);
        etConfirmPassword.setTypeface(face1);
        etMobile.setTypeface(face1);
        etEmail.setTypeface(face1);
        etAddress.setTypeface(face1);
        chkNewsletter.setTypeface(face1);
        chkTermsAndCondition.setTypeface(face1);
        btnLoginFacebook.setTypeface(face1);
        termsNote.setTypeface(face1);
        termsStart.setTypeface(face1);
        termsEnd.setTypeface(face1);
        loginButton.setTypeface(face1);

        //set tranform font
        btnLoginFacebook.setTransformationMethod(null);

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    //Toast.makeText(getApplicationContext(), "unfocus", Toast.LENGTH_LONG).show();
                    etEmail.setGravity(Gravity.LEFT);
                    etEmail.setSelection(0);
                }
            }
        });

        //register
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

                if(isValidate) {
                    GetUserData();
                    SendDataToServe(getFirstName, getLastName, getEmail, getPassword, getMobile, getAddress, getCountry);
                }
            }
        });

        //country spinner configuration
        countrySpinner = (Spinner) findViewById(R.id.register_country_id);
        countryDataList = new ArrayList<String>();
        countryDataList.add("Country");
        countryArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, countryDataList) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                //View view  = super.getDropDownView(position, convertView, parent);
                if(position == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                    textView.setTextColor(getResources().getColor(R.color.textHighlightColor));
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTypeface(face1);
                ((TextView) view).setSingleLine(true);
                if(position == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textHighlightColor));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.black));
                }
                //float density = getResources().getDisplayMetrics().density;
                int dp = (int) (getResources().getDimension(R.dimen.input_text_size) / getResources().getDisplayMetrics().density);
                //Toast.makeText(RegisterActivity.this, "Response: " + dp, Toast.LENGTH_LONG).show();
                ((TextView) view).setTextSize(dp);
                return view;
            }
        };
        countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        new GetJSONCountry().execute();
        countryArrayAdapter.setNotifyOnChange(true);
        countrySpinner.setAdapter(countryArrayAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCountry = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    public void validateData() {
        isValidate = true;
        if(etFirstName.getText().toString().trim().length() == 0) {
            etFirstName.setError(getResources().getString(R.string.first_name_error));
            isValidate = false;
        }

        if(etLastName.getText().toString().trim().length() == 0) {
            etLastName.setError(getResources().getString(R.string.last_name_error));
            isValidate = false;
        }

        if(etEmail.getText().toString().trim().length() == 0) {
            etEmail.setError(getResources().getString(R.string.email_error));
            isValidate = false;
        }

        //check for valid email
        Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
        Matcher matcher1 = pattern1.matcher(etEmail.getText().toString());
        if (!matcher1.matches()) {
            etEmail.setError(getResources().getString(R.string.enter_valid_password));
            isValidate = false;
        }

        if(etMobile.getText().toString().trim().length() == 0) {
            etMobile.setError(getResources().getString(R.string.mobile_error));
            isValidate = false;
        }

        if(etPassword.getText().toString().trim().length() == 0) {
            etPassword.setError(getResources().getString(R.string.password_error));
            isValidate = false;
        }

        if(etConfirmPassword.getText().toString().trim().length() == 0) {
            etConfirmPassword.setError(getResources().getString(R.string.confirm_password_error));
            isValidate = false;
        }

        if(!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            etConfirmPassword.setError(getResources().getString(R.string.password_mismatched_error));
            isValidate = false;
        }

        if(!chkTermsAndCondition.isChecked()){
            chkTermsAndCondition.setError(getResources().getString(R.string.please_accept_terms));
            isValidate = false;
        } else {
            chkTermsAndCondition.setError(null);
        }
    }

    public void GetUserData() {
        getFirstName = etFirstName.getText().toString();
        getLastName = etLastName.getText().toString();
        getEmail = etEmail.getText().toString();
        getPassword = etPassword.getText().toString();
        getMobile = etMobile.getText().toString();
        getAddress = etAddress.getText().toString();
    }

    public void SendDataToServe(final String first_name, final String last_name, final String email,
                                final String password, final String mobile, final String address, final String country_name) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>  {
            @Override
            protected String doInBackground(String... params) {
                String QuickFirstName = first_name;
                String QuickLastName = last_name;
                String QuickEmail = email;
                String QuickPassword = password;
                String QuickMobile = mobile;
                String QuickAddress = address;
                String QuickCountry = country_name;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("first_name", QuickFirstName));
                nameValuePairs.add(new BasicNameValuePair("last_name", QuickLastName));
                nameValuePairs.add(new BasicNameValuePair("email", QuickEmail));
                nameValuePairs.add(new BasicNameValuePair("password", QuickPassword));
                nameValuePairs.add(new BasicNameValuePair("mobile", QuickMobile));
                nameValuePairs.add(new BasicNameValuePair("address", QuickAddress));
                nameValuePairs.add(new BasicNameValuePair("country_name", QuickCountry));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(DataParseUrl);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpClient.execute(httpPost, responseHandler);
                    JSONObject json = new JSONObject(responseBody);
                    resMessage = json.getString("message");
                    resStatus = json.getString("status");
                    userId = json.getString("loginId");
                    //Toast.makeText(RegisterActivity.this, "Response: " + resResult, Toast.LENGTH_LONG).show();
                    //HttpResponse response = httpClient.execute(httpPost);
                    //HttpEntity entity = response.getEntity();
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                } catch(JSONException e) {

                }
                return "You have successfully registered";
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
                Toast.makeText(RegisterActivity.this, resMessage, Toast.LENGTH_LONG).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(resStatus == "true") {
                            Intent intent = new Intent(getBaseContext(), BaseActivity.class);
                            Bundle extras = new Bundle();
                            Preference.setValue(RegisterActivity.this, "PREF_FNAME", getFirstName + " " + getLastName.substring(0,1));
                            Preference.setValue(RegisterActivity.this, "PREF_ADDRESS", getCountry);
                            Preference.setValue(RegisterActivity.this, "PREF_ISLOGIN", "true");
                            Preference.setValue(RegisterActivity.this, "LOGIN_ID", userId);
                            //extras.putString("userName", getFirstName + " " + getLastName.substring(0,1));
                            //extras.putString("userAddress", getCountry);
                            //intent.putExtras(extras);
                            finish();
                            startActivity(intent);
                        }
                    }
                }, 2000);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(RegisterActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(first_name, last_name, email, password, mobile, address, country_name);
    }

    /**
     * GetJSONCountry
     * Get country json data
     */
    public class GetJSONCountry extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "country";
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(Void... params) {
            HttpGet httpGet = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {
                return mClient.execute(httpGet, responseHandler);
            } catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            try {
                List<String> countryList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject country = jsonArray.getJSONObject(i);
                    countryArrayAdapter.add(country.getString("country_name"));
                    Log.v("Country: ", country.getString("country_name"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                Toast.makeText(RegisterActivity.this, "You have successfully loggedin", Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.v("Fb Login response", result);

                    //store in sharedpreference
                    Preference.setValue(RegisterActivity.this, "PREF_FNAME", jsonObject.getString("name"));
                    Preference.setValue(RegisterActivity.this, "PREF_ADDRESS", "India");
                    Preference.setValue(RegisterActivity.this, "LOGIN_ID", jsonObject.getString("loginId"));
                    Preference.setValue(RegisterActivity.this, "PREF_ISLOGIN", "true");

                    //start new activity
                    Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
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
                mProgressDialog = new ProgressDialog(RegisterActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        FacebookLogin facebookLogin = new FacebookLogin();
        facebookLogin.execute(userId, name, token, email, location);
    }
}
