package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Rating;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 10/10/16.
 */

public class Reviews extends Fragment {

    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private Typeface typeface;
    private TextView profileTitleName, reviewText, writeReviewReputation, totalReputationReview, writeReviewClinicAccessibilityText,
            totalReviewClinicAccessibility, availabilityInEmergenciesText, totalAvailibilityInEmergencies, writeReviewApproachabilityText,
            totalReviewApprochability, writeReviewTechnologyAndEquipmentText, totalReviewTechnolotyAndEquipment,
            writeYourReviewText;
    private LinearLayout writeYourReview, userReviewList;
    private RatingBar reputationRatingBar, clinicRatingBar, availabilityRatingBar, approachabilityRatingBar,
            technologyRatingBar;
    private String id, profileName;
    private String appLang = LocalInformation.getLocaleLang();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //final String hospitalName = getArguments().getString("hospitalName");

        id = getArguments().getString("id");
        profileName = getArguments().getString("name");

        //fetch reviews
        fetchDoctorReview(id);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");

        ImageView imageView = (ImageView) getView().findViewById(R.id.reviews_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        profileTitleName = (TextView) getView().findViewById(R.id.reviews_profile_title);
        reviewText = (TextView) getView().findViewById(R.id.reviews_text);
        writeReviewReputation = (TextView) getView().findViewById(R.id.write_review_reputation);
        totalReputationReview = (TextView) getView().findViewById(R.id.total_reputation_review);
        writeReviewClinicAccessibilityText = (TextView) getView().findViewById(R.id.write_review_clinic_accessibility);
        totalReviewClinicAccessibility = (TextView) getView().findViewById(R.id.total_review_clinic_accessibility);
        availabilityInEmergenciesText = (TextView) getView().findViewById(R.id.write_review_availability_in_emergencies);
        totalAvailibilityInEmergencies = (TextView) getView().findViewById(R.id.total_availibility_in_emergencies);
        writeReviewApproachabilityText = (TextView) getView().findViewById(R.id.write_review_approachability);
        totalReviewApprochability = (TextView) getView().findViewById(R.id.total_review_approachability);
        writeReviewTechnologyAndEquipmentText = (TextView) getView().findViewById(R.id.write_review_technology_and_equipment);
        totalReviewTechnolotyAndEquipment = (TextView) getView().findViewById(R.id.total_review_technology_and_equipment);
        //totalReviews = (TextView) getView().findViewById(R.id.total_reviews);
        writeYourReviewText = (TextView) getView().findViewById(R.id.write_your_review_text);
        reputationRatingBar = (RatingBar) getView().findViewById(R.id.reputation_rating);
        clinicRatingBar = (RatingBar) getView().findViewById(R.id.clinic_ratingbar);
        availabilityRatingBar = (RatingBar) getView().findViewById(R.id.availability_ratingbar);
        approachabilityRatingBar = (RatingBar) getView().findViewById(R.id.approachability_ratingbar);
        technologyRatingBar = (RatingBar) getView().findViewById(R.id.technology_ratingbar);

        profileTitleName.setText(profileName);

        /*Toast.makeText(getContext(), hospitalName, Toast.LENGTH_LONG).show();*/
        profileTitleName.setTypeface(typeface);
        reviewText.setTypeface(typeface);
        writeReviewReputation.setTypeface(typeface);
        totalReputationReview.setTypeface(typeface);
        writeReviewClinicAccessibilityText.setTypeface(typeface);
        totalReviewClinicAccessibility.setTypeface(typeface);
        availabilityInEmergenciesText.setTypeface(typeface);
        totalAvailibilityInEmergencies.setTypeface(typeface);
        writeReviewApproachabilityText.setTypeface(typeface);
        totalReviewApprochability.setTypeface(typeface);
        writeReviewTechnologyAndEquipmentText.setTypeface(typeface);
        totalReviewTechnolotyAndEquipment.setTypeface(typeface);
        //totalReviews.setTypeface(typeface);
        writeYourReviewText.setTypeface(typeface);

        //write your review
        writeYourReview = (LinearLayout) getView().findViewById(R.id.write_your_review);
        writeYourReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //review list
        userReviewList = (LinearLayout) getView().findViewById(R.id.doctor_review_list_layout);
    }

    /**
     * Fetch doctor reviews
     * @param doctorId string
     */
    public void fetchDoctorReview(final String doctorId) {

        class DoctorReview extends AsyncTask<String, Void, String> {

            String URL = WEB_SERVICE_URL + "review/reviewRating";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickDoctorId = doctorId;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("doctorId", quickDoctorId));
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));

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
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setRatings(result);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

        }

        DoctorReview doctorReview = new DoctorReview();
        doctorReview.execute(doctorId);

    }

    public void setRatings(String result) {
        try {
            //main rating object
            JSONObject mainRatingObject = new JSONObject(result);
            JSONObject ratingOnject = mainRatingObject.getJSONObject("data");

            //Reputation
            JSONObject reputationObject = ratingOnject.getJSONObject("reputation");
            reputationRatingBar.setRating(Float.valueOf(reputationObject.getString("avg")));
            totalReputationReview.setText(reputationObject.getString("count"));

            //clinic
            JSONObject clinicObject = ratingOnject.getJSONObject("clinic");
            clinicRatingBar.setRating(Float.valueOf(clinicObject.getString("avg")));
            totalReviewClinicAccessibility.setText(clinicObject.getString("count"));

            //availability
            JSONObject availabilityObject = ratingOnject.getJSONObject("availability");
            availabilityRatingBar.setRating(Float.valueOf(availabilityObject.getString("avg")));
            totalAvailibilityInEmergencies.setText(availabilityObject.getString("count"));

            //approachability
            JSONObject approachabilityObject = ratingOnject.getJSONObject("approachability");
            approachabilityRatingBar.setRating(Float.valueOf(approachabilityObject.getString("avg")));
            totalReviewApprochability.setText(approachabilityObject.getString("count"));

            //technology
            JSONObject technologyObject = ratingOnject.getJSONObject("technology");
            technologyRatingBar.setRating(Float.valueOf(technologyObject.getString("avg")));
            totalReviewTechnolotyAndEquipment.setText(technologyObject.getString("count"));

            //grab all reviews
            getAllUserReview(id);
            Log.v("doctor id", String.valueOf(id));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllUserReview(final String userId) {

        class UserDoctorReviews extends AsyncTask<String, Void, String> {

            final String URL = AppConfig.getWebServiceUrl() + "review/ratingUserDetail";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
            String quickUserId = userId;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("doctorId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));

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
                Log.v("Result review", result);
                new RenderAllReviews(result);
                //new SearchFragment.RenderLabSearchResult(result);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

        }

        UserDoctorReviews userDoctorReviews = new UserDoctorReviews();
        userDoctorReviews.execute(userId);
    }

    class RenderAllReviews {
        RenderAllReviews(String result) {
            userReviewList.removeAllViews();
            userReviewList.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("false")) {
                    TextView textView = new TextView(getContext());
                    textView.setText(getResources().getString(R.string.no_data_found));
                    textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    textView.setTextColor(Color.parseColor("#010101"));
                    textView.setTextSize(20);
                    textView.setLayoutParams(textLayoutParam);

                    userReviewList.addView(textView);
                } else {
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.doctor_review_list, null);

                        //user name
                        TextView textView1 = (TextView) view.findViewById(R.id.user_name);
                        textView1.setText(filterData.getString("name"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //date time
                        TextView commentTime = (TextView) view.findViewById(R.id.review_date_time);
                        commentTime.setText(filterData.getString("commentTime"));

                        //user rating
                        RatingBar userRating = (RatingBar) view.findViewById(R.id.user_review);
                        userRating.setRating(Float.valueOf(filterData.getString("avgRating")));

                        //user comment
                        TextView userComment = (TextView) view.findViewById(R.id.user_review_text);
                        userComment.setText(filterData.getString("comment"));


                        userReviewList.addView(view);


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v("Review", "resume");
    }
}
