package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageView closeReview;
    private Typeface typeface;
    private TextView reviewTitle, reputationText, clinicAccessibilityText, availabilityInEmergenciedText,
            approachabilityText, technologyAndEquipmentText, chooseIconForHospitalText;
    private EditText writeReviewEditText;
    private Button submitButton;
    private RatingBar reputationRatingBar, clinicRatingBar, availabilityRatingbar, approachabilityRatingBar,
            technologyRatingBar;
    private String doctorId, userReview, ipAddress, userId,
            reputationRating, clinicRating, availabilityRating, approachabilityRating, technologyRating;
    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private RelativeLayout search_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_write_review);

        Intent sender = getIntent();
        Bundle extras = sender.getExtras();

        search_layout = (RelativeLayout) findViewById(R.id.write_a_review_layout);
        //search_layout.setBackgroundColor(Color.WHITE);


        reviewTitle = (TextView) findViewById(R.id.reviews_title);
        reputationText = (TextView) findViewById(R.id.reputation_text);
        clinicAccessibilityText = (TextView) findViewById(R.id.clinic_accessibility_text);
        availabilityInEmergenciedText = (TextView) findViewById(R.id.availability_in_emergencies_text);
        approachabilityText = (TextView) findViewById(R.id.approachability_text);
        technologyAndEquipmentText = (TextView) findViewById(R.id.technology_and_equipment_text);
        chooseIconForHospitalText = (TextView) findViewById(R.id.choose_icon_for_hospital_text);
        writeReviewEditText = (EditText) findViewById(R.id.write_review_edit_text);
        submitButton = (Button) findViewById(R.id.submit_button);
        reputationRatingBar = (RatingBar) findViewById(R.id.reputation_rating);
        clinicRatingBar = (RatingBar) findViewById(R.id.clinic_rating);
        availabilityRatingbar = (RatingBar) findViewById(R.id.availability_rating);
        approachabilityRatingBar = (RatingBar) findViewById(R.id.approachability_rating);
        technologyRatingBar = (RatingBar) findViewById(R.id.technology_rating);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");

        reviewTitle.setTypeface(typeface);
        reputationText.setTypeface(typeface);
        clinicAccessibilityText.setTypeface(typeface);
        availabilityInEmergenciedText.setTypeface(typeface);
        approachabilityText.setTypeface(typeface);
        technologyAndEquipmentText.setTypeface(typeface);
        chooseIconForHospitalText.setTypeface(typeface);
        writeReviewEditText.setTypeface(typeface);
        submitButton.setTypeface(typeface);

        //close activity
        closeReview = (ImageView) findViewById(R.id.close_write_review);
        closeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //assign data to variable
        userId = Preference.getValue(WriteReviewActivity.this, "LOGIN_ID", "");
        Log.v("User ID", userId);
        ipAddress = getLocalIpAddress();
        doctorId = extras.getString("id");
        userReview = writeReviewEditText.getText().toString();

        //submit review
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reputationRating = String.valueOf(reputationRatingBar.getRating());
                clinicRating = String.valueOf(clinicRatingBar.getRating());
                availabilityRating = String.valueOf(availabilityRatingbar.getRating());
                approachabilityRating = String.valueOf(approachabilityRatingBar.getRating());
                technologyRating = String.valueOf(technologyRatingBar.getRating());
                userReview = writeReviewEditText.getText().toString().trim();
                /*Log.v("Reputation", reputationRating);
                Log.v("Clinic", clinicRating);
                Log.v("Availability", availabilityRating);
                Log.v("Approachability", approachabilityRating);
                Log.v("Technology", technologyRating);
                Log.v("Hospital", doctorId);*/

                //post rating
                postRating(reputationRating, clinicRating, availabilityRating, approachabilityRating, technologyRating,
                        userReview, userId, ipAddress, doctorId);
                //search_layout.setBackgroundColor(Color.GRAY);
                //startActivity(new Intent(WriteReviewActivity.this, SubmitReviewActivity.class));
            }
        });
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "IP Address" + ex.toString(), Toast.LENGTH_LONG).show();
            Log.v("IP Address", ex.toString());
        }
        return null;
    }

    public void postRating(final String reputation, final String clinic, final String availability, final String approachability,
                           final String technology, final String customerReview, final String userId, final String ipAddress,
                           final String doctorId) {

        class PostDoctorRating extends AsyncTask<String, Void, String> {
            String URL = WEB_SERVICE_URL + "review/add";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickReputation = reputation;
            String quickClinic = clinic;
            String quickAvailability = availability;
            String quickApproachability = approachability;
            String quickTechnology = technology;
            String quickCustomerReview = customerReview;
            String quickUserId = userId;
            String quickIpaddress = ipAddress;
            String quickDoctorId = doctorId;

            @Override
            protected String doInBackground(String... params) {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("doctorId", quickDoctorId));
                    nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                    nameValuePairs.add(new BasicNameValuePair("userIp", quickIpaddress));
                    nameValuePairs.add(new BasicNameValuePair("reputation", quickReputation));
                    nameValuePairs.add(new BasicNameValuePair("clinic", quickClinic));
                    nameValuePairs.add(new BasicNameValuePair("availability", quickAvailability));
                    nameValuePairs.add(new BasicNameValuePair("approachability", quickApproachability));
                    nameValuePairs.add(new BasicNameValuePair("technology", quickTechnology));
                    nameValuePairs.add(new BasicNameValuePair("comment", quickCustomerReview));

                    Log.v("Name Value pair: ", nameValuePairs.toString());

                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.v("JSON Object", jsonObject.toString());
                    Toast.makeText(getBaseContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(WriteReviewActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        PostDoctorRating postDoctorRating = new PostDoctorRating();
        postDoctorRating.execute(reputation, clinic, availability, approachability, technology, customerReview, userId, ipAddress, doctorId);
    }
}
