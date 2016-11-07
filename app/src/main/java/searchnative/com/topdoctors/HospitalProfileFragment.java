package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by mayur on 28/9/16.
 */

public class HospitalProfileFragment extends Fragment {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    TabFragment tabFragment = new TabFragment();
    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private Typeface typeface;
    private double latitude, longitude;
    private LatLng hospitalMap;
    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;
    private LinearLayout linearLayout, hospitalAllReviews, hospitalSpecialityLayout;
    private String hospitalName, hospitalAddress, hospitalPhone;
    private TextView photoOrVideoText, writeReviewButton;
    private ImageView photoOrVideoImage, hospitalSocialShare;
    private String appLang = LocalInformation.getLocaleLang();
    private String hospitalId;
    private ImageView hospitalShare;
    //Bundle savedState;
    //MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hospital_profile, container, false);
        //supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.google_map_fragment);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hospitalId = getArguments().getString("id");
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");
        //GetSearchTitleMarginStart();
        writeReviewButton = (TextView) getView().findViewById(R.id.write_a_review_button);
        hospitalSocialShare = (ImageView) getView().findViewById(R.id.hospital_social_share);
        hospitalShare = (ImageView) getView().findViewById(R.id.hospitalShare);


        ImageView imageView = (ImageView) getView().findViewById(R.id.hospital_profile_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //hospital reviews
        hospitalAllReviews = (LinearLayout) getView().findViewById(R.id.hospital_all_reviews);
        hospitalAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", hospitalId);
                bundle.putString("name", hospitalName);
                bundle.putString("hospitalAddress", hospitalAddress);
                bundle.putString("hospitalPhone", hospitalPhone);

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
                Intent intent = new Intent(getContext(), WriteReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", hospitalId);
                bundle.putString("hospitalName", hospitalName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        photoOrVideoText = (TextView) getView().findViewById(R.id.textView3);
        photoOrVideoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhotoVideoActivity();
            }
        });

        photoOrVideoImage = (ImageView) getView().findViewById(R.id.photo_or_video_image);
        photoOrVideoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhotoVideoActivity();
            }
        });

        //hospital speciality list
        hospitalSpecialityLayout = (LinearLayout) getView().findViewById(R.id.hospital_speciality);

        //social share
        hospitalSocialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(hospitalName, hospitalPhone, hospitalAddress, "");
            }
        });

        hospitalShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(hospitalName, hospitalPhone, hospitalAddress, "");
            }
        });
    }

    /**
     * Share it on social
     * @param name string
     * @param phone string
     * @param address string
     */
    public void shareIt(final String name, final String phone, final String address, final String rating) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + "Phone: " + phone + "\n" + "Address: "
                + address + "\n" + "Rating: " + rating);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));

    }

    public void startPhotoVideoActivity() {
        startActivity(new Intent(getContext(), PhotoOrVideoActivity.class));
    }

    /**
     * Hospital profile details
     * @param hospitalId string
     */
    public void hospitalProfile(final String hospitalId) {

        class HospitalProfile extends AsyncTask<String, Void, String> {
            String URL = WEB_SERVICE_URL + "profile/hospitalProfile";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickHospitalId = hospitalId;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("hospitalId", quickHospitalId));
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
                new HospitalProfileFragment.DisplayHospitalProfile(result);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
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

        HospitalProfile hospitalProfile = new HospitalProfile();
        hospitalProfile.execute(hospitalId);
    }

    /**
     * Display hospital profile
     */
    public class DisplayHospitalProfile {
        DisplayHospitalProfile(String result) {
            Log.v("Hospital result", result);
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
                    hospitalName = profile.getString("Name");
                    name.setTypeface(typeface, typeface.BOLD);

                    //title
                    TextView title = (TextView) getView().findViewById(R.id.profile_detail_title);
                    title.setText(profile.getString("Name"));
                    title.setTypeface(typeface, typeface.BOLD);

                    //address
                    TextView address = (TextView) getView().findViewById(R.id.textView6);
                    address.setText(profile.getString("Address"));
                    address.setTypeface(typeface);
                    hospitalAddress = profile.getString("Address");

                    //phone
                    TextView doctorPhone = (TextView) getView().findViewById(R.id.textView8);
                    doctorPhone.setText(getResources().getString(R.string.call) +" (" + profile.getString("Phone") + ")");
                    doctorPhone.setTypeface(typeface);

                    //get direction
                    TextView getDirection = (TextView) getView().findViewById(R.id.textView7);
                    getDirection.setTypeface(typeface);

                    //write a review
                    TextView writeReview = (TextView) getView().findViewById(R.id.write_a_review_button);
                    writeReview.setTypeface(typeface);

                    //total doctors
                    TextView totalDoctors = (TextView) getView().findViewById(R.id.total_doctor_button);
                    totalDoctors.setText(jsonObject.getString("totalDcotors") + " " + getResources().getString(R.string.search_doctor_label));
                    totalDoctors.setTypeface(typeface);

                    final String callDialer = profile.getString("Phone");
                    hospitalPhone = callDialer;
                    RelativeLayout hospitalCall = (RelativeLayout) getView().findViewById(R.id.hospital_call);
                    hospitalCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + callDialer));
                            startActivity(intent);
                        }
                    });

                    final String doctorCounter = jsonObject.getString("totalDcotors");
                    final String hospitalId = profile.getString("HospitalId");
                    final String hospitalName = profile.getString("Name");
                    final String hospitalAddress = profile.getString("Address");

                    totalDoctors.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getContext(), doctorCounter, Toast.LENGTH_LONG).show();
                            //Preference.setValue(getContext(), "GLOBAL_FILTER_TYPE", "");
                            if(doctorCounter.equals("0")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Hospital doctors")
                                        .setMessage("No doctors are in this hospital")
                                        .setCancelable(false)
                                        .setNegativeButton(getResources().getString(R.string.close),new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                Bundle args = new Bundle();
                                args.putString("id", hospitalId);
                                args.putString("hospitalName", hospitalName);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                HospitalDoctorListFragment hospitalDoctorListFragment = new HospitalDoctorListFragment();
                                hospitalDoctorListFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("hospitalDoctorsList");
                                mFragmentTransaction.replace(R.id.search_layout, hospitalDoctorListFragment).commit();
                            }
                        }
                    });

                    //check for lat and long
                    if(!profile.getString("Latitude").equals("null") &&
                            !profile.getString("Latitude").isEmpty() &&
                            !profile.getString("Longitude").equals("null") &&
                            !profile.getString("Longitude").isEmpty()) {
                        latitude = Double.valueOf(profile.getString("Latitude"));
                        longitude = Double.valueOf(profile.getString("Longitude"));
                    }


                    //set latitude and longitude
                    hospitalMap = new LatLng(latitude, longitude);
                    //google map
                    try {
                        FragmentManager fragmentManager = getChildFragmentManager();
                        supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.hospital_google_map_layout);
                        if(supportMapFragment == null) {
                            supportMapFragment = SupportMapFragment.newInstance();
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap map) {
                                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalMap, 16));
                                    map.addMarker(new MarkerOptions().position(hospitalMap)
                                            .title(hospitalAddress).icon(BitmapDescriptorFactory.fromResource(R.mipmap.hospitals)));
                                    map.setTrafficEnabled(true);
                                    map.setIndoorEnabled(true);
                                    map.setBuildingsEnabled(true);
                                    map.getUiSettings().setZoomControlsEnabled(true);
                                }
                            });
                            fragmentManager.beginTransaction().replace(R.id.hospital_google_map_layout, supportMapFragment).commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //get direction
                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.get_direction_layout);
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
                JSONArray hospitalSpec = jsonObject.getJSONArray("hospitalSpecialities");

                //hospital spec
                for (int i = 0; i < hospitalSpec.length(); i++) {
                    final JSONObject hospitalSpeciality = hospitalSpec.getJSONObject(i);
                    ImageView spec = new ImageView(getContext());

                    int mipmapSize = GetSearchTitleMarginStart();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mipmapSize, mipmapSize);
                    spec.setLayoutParams(layoutParams);

                    if(hospitalSpeciality.getString("Mipmap").isEmpty()) {
                        spec.setImageResource(R.mipmap.teath);
                        spec.setVisibility(View.INVISIBLE);
                    } else {
                        int mipmapId = getContext().getResources().getIdentifier(hospitalSpeciality.getString("Mipmap"), "mipmap", getContext().getPackageName());
                        spec.setImageResource(mipmapId);
                    }
                    //int dp = (int) (get.getResources().getDimension(R.dimen.add_doctor_input_font) / getContext().getResources().getDisplayMetrics().density);

                    //spec.setMinimumHeight(100);
                    //spec.setMaxHeight(50);
                    spec.requestLayout();
                    //spec.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                    hospitalSpecialityLayout.addView(spec);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int GetSearchTitleMarginStart() {
        float density = getResources().getDisplayMetrics().density;
        //Toast.makeText(getContext(), "density: " + density, Toast.LENGTH_LONG).show();
        int margin = 0;
        //Toast.makeText(getContext(), "density: " + density, Toast.LENGTH_LONG).show();
        if(density >= 0.75 && density < 1.0) {

        } else if(density >= 1.0 && density < 1.5) {
            margin = 25;
        } else if(density >= 1.5 && density < 2.0) {
            margin = 40;
        } else if (density >= 2.0 && density <= 2.5) {
            margin = 80;
        } else if(density > 2.5 && density < 3.0) {
            margin = 100;
        } else if(density >= 3.0 && density < 3.5) {
            margin = 110;
        } else if(density >= 3.5 && density <= 4.0) {
            margin = 135;
        } else if(density > 4.0) {

        } else {

        }
        return margin;
    }

    @Override
    public void onDestroyView() {
        //mContainer.removeAllViews();
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.search_layout);
        //mContainer.removeAllViews();
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initilizeMap() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.addMarker(new MarkerOptions().position(hospitalMap).title("Ahmedabad"));
                map.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
                map.setTrafficEnabled(true);
                map.setIndoorEnabled(true);
                map.setBuildingsEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v("Resume from: ", "hospital");
        hospitalProfile(hospitalId);
    }

}
