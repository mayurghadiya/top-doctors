package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Rating;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
 * Created by root on 26/9/16.
 */
public class DoctorProfileFragment extends Fragment {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    TabFragment tabFragment = new TabFragment();
    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private Typeface typeface;
    private SupportMapFragment supportMapFragment;
    private double latitude, longitude;
    private LatLng doctorProfileLatlong;
    private TextView totalReviews, writeReviewButton;
    private List specialityIcon;
    private ImageView socialShare, photoOrVideo;
    private ShareActionProvider mShareActionProvider;
    private String doctorProfileEmail, doctorProfileName, doctorProfilePhone, doctorProfileMessage,
            doctorProfileAddress, doctorProfileRating, doctorId;

    private RatingBar reputationRating, clinicRatingbar, availabilityRatingbar, approachabilityRatingbar, technologyRatingbar;
    private TextView totalReputationReview, totalReviewClinicAccessibility, totalAvailibilityInEmergencies,
            totalReviewApproachability, totalReviewTechnologyAndEquipment, reviewsTitle, callText;
    LinearLayout writeYourReview;
    private String appLang = LocalInformation.getLocaleLang();
    private ImageView doctorShare, doctorBookmarks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doctorId = getArguments().getString("id");
        final String doctorName = getArguments().getString("name");

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");

        totalReviews = (TextView) getView().findViewById(R.id.total_reviews);
        writeReviewButton = (TextView) getView().findViewById(R.id.write_a_review_button);
        socialShare = (ImageView) getView().findViewById(R.id.social_share);
        photoOrVideo = (ImageView) getView().findViewById(R.id.photos_or_video);
        reputationRating = (RatingBar) getView().findViewById(R.id.reputation_rating);
        clinicRatingbar = (RatingBar) getView().findViewById(R.id.clinic_ratingbar);
        availabilityRatingbar = (RatingBar) getView().findViewById(R.id.availability_ratingbar);
        approachabilityRatingbar = (RatingBar) getView().findViewById(R.id.approachability_ratingbar);
        writeYourReview = (LinearLayout) getView().findViewById(R.id.write_your_review);
        doctorShare = (ImageView) getView().findViewById(R.id.doctorShare);
        doctorBookmarks = (ImageView) getView().findViewById(R.id.doctorBookmarks);

        totalReputationReview = (TextView) getView().findViewById(R.id.total_reputation_review);
        totalReviewClinicAccessibility = (TextView) getView().findViewById(R.id.total_review_clinic_accessibility);
        totalAvailibilityInEmergencies = (TextView) getView().findViewById(R.id.total_availibility_in_emergencies);
        totalReviewApproachability = (TextView) getView().findViewById(R.id.total_review_approachability);
        technologyRatingbar = (RatingBar) getView().findViewById(R.id.technology_ratingbar);
        totalReviewTechnologyAndEquipment = (TextView) getView().findViewById(R.id.total_review_technology_and_equipment);
        reviewsTitle = (TextView) getView().findViewById(R.id.reviewsTitle);
        callText = (TextView) getView().findViewById(R.id.call_text);
        reviewsTitle.setTypeface(typeface, typeface.BOLD);

        specialityIcon = new ArrayList();
        specialityIcon.addAll(SpecialityData.getmInstance().specialityListIcon);

        ImageView imageView = (ImageView) getView().findViewById(R.id.doctor_profile_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        writeYourReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", doctorId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //photo and video
        photoOrVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PhotoOrVideoActivity.class));
            }
        });

        //total review
        totalReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", doctorId);
                bundle.putString("name", doctorName);

                FragmentManager fragmentManager;
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = fragmentManager.beginTransaction();

                Reviews reviews = new Reviews();
                reviews.setArguments(bundle);

                fragmentTransaction.addToBackStack("hospital_profile");
                fragmentTransaction.replace(R.id.search_layout, reviews).commit();
            }
        });

        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", doctorId);
                bundle.putString("name", doctorName);

                Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //social share
        socialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(doctorProfileName, doctorProfilePhone, doctorProfileAddress, doctorProfileRating);
            }
        });

        doctorShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(doctorProfileName, doctorProfilePhone, doctorProfileAddress, doctorProfileRating);
            }
        });

        doctorBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorBookMarks(doctorId);
            }
        });
    }

    public void shareIt(final String name, final String phone, final String address, final String rating) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + "Phone: " + phone + "\n" + "Address: "
                + address + "\n" + "Rating: " + rating);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));

        /*ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Game Result Highscore")
                .setContentDescription("My new highscore is " + sum.getText() + "!!")
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=de.ginkoboy.flashcards"))

                //.setImageUrl(Uri.parse("android.resource://de.ginkoboy.flashcards/" + R.drawable.logo_flashcards_pro))
                .setImageUrl(Uri.parse("http://bagpiper-andy.de/bilder/dudelsack%20app.png"))
                .build();

        shareDialog.show(linkContent);*/

    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void doctorProfile(final String id) {

        class DoctorProfile extends AsyncTask<String, Void, String> {
            String URL = WEB_SERVICE_URL + "profile/doctorProfile";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickDcotorId = id;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("doctorId", quickDcotorId));
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
                Log.v("Doctor Profile", result);
                new DisplayDoctorProfile(result);
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

        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.execute(id);
    }

    /**
     * Display doctor profile
     */
    public class DisplayDoctorProfile {
        DisplayDoctorProfile(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                //Log.v("JSON", jsonObject.toString());
                JSONArray profileDataArray = jsonObject.getJSONArray("data");

                for(int i = 0; i < profileDataArray.length(); i++) {
                    final JSONObject profile = profileDataArray.getJSONObject(i);
                    Log.v("Name", profile.toString());
                    //name
                    TextView name = (TextView) getView().findViewById(R.id.profile_detail_name);
                    name.setText(profile.getString("Name"));
                    doctorProfileName = profile.getString("Name");
                    name.setTypeface(typeface, typeface.BOLD);

                    //speciality icon
                    ImageView doctorSpecialityIcon = (ImageView) getView().findViewById(R.id.speciality);
                    int id = getContext().getResources().getIdentifier(
                            specialityIcon.get(getCategoryPos(profile.getString("Mipmap"))).toString(),
                            "mipmap", getContext().getPackageName());
                    doctorSpecialityIcon.setImageResource(id);

                    //title
                    TextView title = (TextView) getView().findViewById(R.id.profile_detail_title);
                    title.setText(profile.getString("Name"));
                    title.setTypeface(typeface, typeface.BOLD);

                    //address
                    TextView address = (TextView) getView().findViewById(R.id.textView6);
                    address.setText(profile.getString("Address"));
                    address.setTypeface(typeface);
                    doctorProfileAddress = profile.getString("Address");

                    //phone
                    TextView doctorPhone = (TextView) getView().findViewById(R.id.textView8);
                    callText.setText(getResources().getString(R.string.call));
                    callText.setTypeface(typeface, typeface.BOLD);
                    doctorPhone.setText(" (" + profile.getString("Phone") + ")");
                    doctorPhone.setTypeface(typeface);
                    doctorProfilePhone = profile.getString("Phone");

                    //get direction
                    TextView getDirection = (TextView) getView().findViewById(R.id.textView7);
                    getDirection.setTypeface(typeface, typeface.BOLD);

                    //write a review
                    TextView writeReview = (TextView) getView().findViewById(R.id.write_a_review_button);
                    writeReview.setTypeface(typeface);

                    //claim profile
                    TextView claimProfile = (TextView) getView().findViewById(R.id.claim_profile_button);
                    claimProfile.setTypeface(typeface);
                    final String profileName = profile.getString("Name");
                    claimProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ClaimProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", profileName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    //rating
                    RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.doctor_user_rating);
                    if(!profile.getString("RatingAvg").equals("null") &&
                            !profile.getString("RatingAvg").isEmpty()) {
                        ratingBar.setRating(Float.valueOf(profile.getString("RatingAvg")));
                        doctorProfileRating = String.valueOf(profile.getString("RatingAvg"));
                    }

                    //review
                    TextView totalReview = (TextView) getView().findViewById(R.id.total_reviews);
                    if(!profile.getString("TotalReview").equals("null") &&
                            !profile.getString("TotalReview").isEmpty()) {
                        totalReview.setText(profile.getString("TotalReview") + " " + getResources().getString(R.string.reviews));
                    } else {
                        totalReview.setText(0 + getResources().getString(R.string.reviews));
                    }
                    totalReview.setTypeface(typeface);

                    final String callDialer = profile.getString("Phone");
                    RelativeLayout hospitalCall = (RelativeLayout) getView().findViewById(R.id.doctor_call);
                    hospitalCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + callDialer));
                            startActivity(intent);
                        }
                    });

                    if(!profile.getString("Latitude").equals("null") &&
                            !profile.getString("Latitude").isEmpty() &&
                            !profile.getString("Longitude").equals("null") &&
                            !profile.getString("Longitude").isEmpty()) {
                        latitude = profile.getDouble("Latitude");
                        longitude = profile.getDouble("Longitude");
                    }

                    //set latitude and longitude
                    doctorProfileLatlong = new LatLng(latitude, longitude);
                    final String doctorAddress = profile.getString("Name");
                    //google map
                    try {
                        FragmentManager fragmentManager = getChildFragmentManager();
                        supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.doctor_profile_map_layout);
                        if(supportMapFragment == null) {
                            supportMapFragment = SupportMapFragment.newInstance();
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap map) {
                                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorProfileLatlong, 16));
                                    map.addMarker(new MarkerOptions().position(doctorProfileLatlong).title(doctorAddress)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.doctor_location)));
                                    map.setTrafficEnabled(true);
                                    map.setIndoorEnabled(true);
                                    map.setBuildingsEnabled(true);
                                    map.getUiSettings().setZoomControlsEnabled(true);
                                }
                            });
                            fragmentManager.beginTransaction().replace(R.id.doctor_profile_map_layout, supportMapFragment).commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //get direction
                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.doctor_get_direction_layout);
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(String.valueOf(latitude).isEmpty() || String.valueOf(longitude).isEmpty()) {
                                Toast.makeText(getContext(), "Location is not available", Toast.LENGTH_LONG).show();
                            } else {
                                final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr=my location" +"&daddr=" + latitude + "," + longitude));
                                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                                startActivity(intent);
                            }

                        }
                    });

                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getCategoryPos(String category) {
        return specialityIcon.indexOf(category);
    }

    public void doctorAvgRating(final String doctorId) {

        class DoctorAvgRating extends AsyncTask<String, Void,String> {

            String quickDoctorId = doctorId;

            String URL = WEB_SERVICE_URL + "review/reviewRating";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

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
                new RenderAvgReview(result);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
        }

        DoctorAvgRating doctorAvgRating = new DoctorAvgRating();
        doctorAvgRating.execute(doctorId);

    }

    class RenderAvgReview {
        RenderAvgReview(String result) {
            //LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.)
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject ratingOnject = jsonObject.getJSONObject("data");

                //Reputation
                JSONObject reputationObject = ratingOnject.getJSONObject("reputation");
                reputationRating.setRating(Float.valueOf(reputationObject.getString("avg")));
                totalReputationReview.setText(reputationObject.getString("count"));
                Log.v("JSON Object", jsonObject.toString());

                //clinic
                JSONObject clinicObject = ratingOnject.getJSONObject("clinic");
                clinicRatingbar.setRating(Float.valueOf(clinicObject.getString("avg")));
                totalReviewClinicAccessibility.setText(clinicObject.getString("count"));

                //availability
                JSONObject availabilityObject = ratingOnject.getJSONObject("availability");
                availabilityRatingbar.setRating(Float.valueOf(availabilityObject.getString("avg")));
                totalAvailibilityInEmergencies.setText(availabilityObject.getString("count"));

                //approachability
                JSONObject approachabilityObject = ratingOnject.getJSONObject("approachability");
                approachabilityRatingbar.setRating(Float.valueOf(approachabilityObject.getString("avg")));
                totalReviewApproachability.setText(approachabilityObject.getString("count"));

                //technology
                JSONObject technologyObject = ratingOnject.getJSONObject("technology");
                technologyRatingbar.setRating(Float.valueOf(technologyObject.getString("avg")));
                totalReviewTechnologyAndEquipment.setText(technologyObject.getString("count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void doctorAllReviews(final String doctorId) {

        class DoctorAllReviews extends AsyncTask<String, Void, String> {

            String quickDoctorId = doctorId;
            String URL = WEB_SERVICE_URL + "review/ratingUserDetail";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

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
                new RenderDoctorAllReviews(result);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
        }

        DoctorAllReviews doctorAllReviews = new DoctorAllReviews();
        doctorAllReviews.execute(doctorId);

    }

    class RenderDoctorAllReviews {
        RenderDoctorAllReviews(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.allReviewList);
            linearLayout.removeAllViews();
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("false")) {
                    TextView textView = new TextView(getContext());
                    textView.setText(getResources().getString(R.string.no_data_found));
                    textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    textView.setTextColor(Color.parseColor("#010101"));
                    textView.setTextSize(20);
                    textView.setLayoutParams(textLayoutParam);

                    linearLayout.addView(textView);
                } else {
                    JSONArray reviewArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < reviewArray.length(); i++) {
                        final JSONObject filterData = reviewArray.getJSONObject(i);
                        View view;
                        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = layoutInflater.inflate(R.layout.doctor_review_list, null);

                        //user name
                        TextView textView1 = (TextView) view.findViewById(R.id.user_name);
                        textView1.setText(filterData.getString("name"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //date time
                        TextView commentTime = (TextView) view.findViewById(R.id.review_date_time);
                        commentTime.setText(filterData.getString("commentTime"));
                        commentTime.setTypeface(typeface);

                        //user rating
                        RatingBar userRating = (RatingBar) view.findViewById(R.id.user_review);
                        userRating.setRating(Float.valueOf(filterData.getString("avgRating")));

                        //user comment
                        TextView userComment = (TextView) view.findViewById(R.id.user_review_text);
                        userComment.setText(filterData.getString("comment"));
                        userComment.setTypeface(typeface);

                        //total photos and videos
                        TextView totalPhotoAndVideos = (TextView) view.findViewById(R.id.total_photo_video);
                        totalPhotoAndVideos.setTypeface(typeface);

                        //share review
                        TextView shareReview = (TextView) view.findViewById(R.id.share_review);
                        shareReview.setTypeface(typeface, typeface.BOLD);

                        linearLayout.addView(view);

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
        doctorProfile(doctorId);
        doctorAvgRating(doctorId);
        doctorAllReviews(doctorId);
    }

    public void doctorBookMarks(final String doctorId) {

        class DoctorBookmarksAddUpdate extends AsyncTask<String, Void, String> {

            String URL = WEB_SERVICE_URL + "bookmark/add";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickDoctorId = doctorId;
            String quickUserId = Preference.getValue(getContext(), "LOGIN_ID", "");

            private String resultStatus = "";

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("doctorId", quickDoctorId));
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
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
                Log.v("Bookmarks result", result);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Preference.setValue(getContext(), "BOOKMARKS_STATUS", jsonObject.getString("result"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            }
        }

        DoctorBookmarksAddUpdate doctorBookmarksAddUpdate = new DoctorBookmarksAddUpdate();
        doctorBookmarksAddUpdate.execute(doctorId);
    }
}
