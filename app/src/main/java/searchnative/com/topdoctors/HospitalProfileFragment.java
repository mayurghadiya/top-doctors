package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
    private LinearLayout linearLayout;
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
        String hospitalId = getArguments().getString("id");

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");

        hospitalProfile(hospitalId);

        ImageView imageView = (ImageView) getView().findViewById(R.id.hospital_profile_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
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

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    return mClient.execute(httpPost, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                mClient.close();

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
                    name.setTypeface(typeface, typeface.BOLD);

                    //title
                    TextView title = (TextView) getView().findViewById(R.id.profile_detail_title);
                    title.setText(profile.getString("Name"));
                    title.setTypeface(typeface, typeface.BOLD);

                    //address
                    TextView address = (TextView) getView().findViewById(R.id.textView6);
                    address.setText(profile.getString("Address"));
                    address.setTypeface(typeface);

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
                    RelativeLayout hospitalCall = (RelativeLayout) getView().findViewById(R.id.hospital_call);
                    hospitalCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + callDialer));
                            startActivity(intent);
                        }
                    });

                    final String hospitalId = profile.getString("HospitalId");
                    final String hospitalName = profile.getString("Name");
                    final String hospitalAddress = profile.getString("Address");

                    totalDoctors.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle args = new Bundle();
                            args.putString("id", hospitalId);
                            args.putString("hospitalName", hospitalName);

                            FragmentManager mFragmentManager;
                            mFragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction mFragmentTransaction;
                            mFragmentTransaction = mFragmentManager.beginTransaction();

                            HospitalDoctorListFragment hospitalDoctorListFragment = new HospitalDoctorListFragment();
                            hospitalDoctorListFragment.setArguments(args);

                            mFragmentTransaction.addToBackStack("hospitalDoctors");
                            mFragmentTransaction.replace(R.id.search_layout, hospitalDoctorListFragment).commit();
                        }
                    });

                    latitude = profile.getDouble("Latitude");
                    longitude = profile.getDouble("Longitude");

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
                                    map.addMarker(new MarkerOptions().position(hospitalMap).title(hospitalAddress));
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
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
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

}
