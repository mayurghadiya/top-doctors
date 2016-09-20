package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView tvForgotPasswordLabel, tvBackToLogin;
    EditText etEmail;
    String getEmail;
    Button btnSubmit;
    Boolean isValidate = true;
    Boolean resStatus;
    String resMessage = "";
    String webServiceUrl = AppConfig.getWebServiceUrl();

    String DataParseUrl = webServiceUrl + "user/forgot_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);

        etEmail = (EditText) findViewById(R.id.forgot_et_email);
        btnSubmit = (Button) findViewById(R.id.forgot_btn_submit);
        tvBackToLogin = (TextView) findViewById(R.id.forgot_tv_back_to_login);
        tvForgotPasswordLabel = (TextView) findViewById(R.id.forgot_textViewLogin);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ExoBlack.otf");
        tvBackToLogin.setTypeface(face);
        tvForgotPasswordLabel.setTypeface(face);

        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");
        etEmail.setTypeface(face1);
        tvBackToLogin.setTypeface(face1);

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

                if(isValidate) {
                    getUserData();
                    sendDataToServer(getEmail);
                }
            }
        });
    }

    public void getUserData() {
        getEmail = etEmail.getText().toString();
    }

    public void sendDataToServer(final String email) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String quickEmail = email;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", quickEmail));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(DataParseUrl);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpClient.execute(httpPost, responseHandler);
                    JSONObject json = new JSONObject(responseBody);
                    resMessage = json.getString("message");
                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                } catch(JSONException e) {

                }
                return "Reset password request succefully sent!";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(resMessage == "") {
                    resMessage = "Service is unavailable";
                }
                Toast.makeText(ForgotPasswordActivity.this, resMessage, Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email);
    }

    public void validateData() {
        isValidate = true;
        if(etEmail.getText().toString().length() == 0) {
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
    }
}
