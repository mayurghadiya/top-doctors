package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
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
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayur on 28/9/16.
 */

public class LabProfileFragment extends Fragment {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    TabFragment tabFragment = new TabFragment();
    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private Typeface typeface;
    TextView claimProfile;
    private double latitude, longitude;
    private LatLng labMap;
    private SupportMapFragment supportMapFragment;
    private String labMipmap;
    private ImageView socialShare, photosOrVideo;
    private String labName, labAddress, labPhone, labRating, labSpeciality;
    private String appLang = LocalInformation.getLocaleLang();
    private ImageView labShare;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lab_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String labId = getArguments().getString("id");
        labMipmap = getArguments().getString("mipmap");
        labSpeciality = getArguments().getString("speciality");

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");

        labProfile(labId);

        ImageView imageView = (ImageView) getView().findViewById(R.id.lab_profile_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //social share
        socialShare = (ImageView) getView().findViewById(R.id.lab_social_share);
        labShare = (ImageView) getView().findViewById(R.id.labShare);
        socialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(labName, labPhone, labAddress, labSpeciality, "");
            }
        });
        labShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(labName, labPhone, labAddress, labSpeciality, "");
            }
        });

        //photo or video
        photosOrVideo = (ImageView) getView().findViewById(R.id.photos_or_video);
        photosOrVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PhotoOrVideoActivity.class));
            }
        });
    }

    /**
     * Lab profile details
     * @param labId string
     */
    public void labProfile(final String labId) {
        class ClinicProfile extends AsyncTask<String, Void, String> {
            String URL = WEB_SERVICE_URL + "profile/labProfile";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickLabId = labId;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("labId", quickLabId));
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
                new LabProfileFragment.DisplayLabProfile(result);
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

        ClinicProfile clinicProfile = new ClinicProfile();
        clinicProfile.execute(labId);

    }

    /**
     * Display clinic profile
     */
    public class DisplayLabProfile {
        DisplayLabProfile(String result) {
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
                    labName = profile.getString("Name");

                    //specilality
                    int specialityId = getContext().getResources().getIdentifier(profile.getString("Mipmap"),
                            "mipmap",
                            getContext().getPackageName());
                    ImageView specialityImage = (ImageView) getView().findViewById(R.id.speciality);
                    specialityImage.setImageResource(specialityId);

                    //title
                    TextView title = (TextView) getView().findViewById(R.id.profile_detail_title);
                    title.setText(profile.getString("Name"));
                    title.setTypeface(typeface, typeface.BOLD);

                    //address
                    TextView address = (TextView) getView().findViewById(R.id.textView6);
                    address.setText(profile.getString("Address"));
                    address.setTypeface(typeface);
                    labAddress = profile.getString("Address");

                    //phone
                    TextView doctorPhone = (TextView) getView().findViewById(R.id.textView8);
                    doctorPhone.setText(getResources().getString(R.string.call) + " (" + profile.getString("Phone") + ")");
                    doctorPhone.setTypeface(typeface);
                    labPhone = profile.getString("Phone");

                    //get direction
                    TextView getDirection = (TextView) getView().findViewById(R.id.textView7);
                    getDirection.setTypeface(typeface);

                    //write a review
                    TextView writeReview = (TextView) getView().findViewById(R.id.write_a_review_button);
                    writeReview.setTypeface(typeface);

                    claimProfile = (TextView) getView().findViewById(R.id.claim_profile_button);
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

                    //claim profile
                    TextView claimProfile = (TextView) getView().findViewById(R.id.claim_profile_button);
                    claimProfile.setTypeface(typeface);

                    latitude = profile.getDouble("Latitude");
                    longitude = profile.getDouble("Longitude");

                    //set latitude and longitude
                    labMap = new LatLng(latitude, longitude);
                    final String labAddress = profile.getString("Address");
                    //google map
                    try {
                        FragmentManager fragmentManager = getChildFragmentManager();
                        supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.lab_map_layout);
                        if(supportMapFragment == null) {
                            supportMapFragment = SupportMapFragment.newInstance();
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap map) {
                                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(labMap, 16));
                                    map.addMarker(new MarkerOptions().position(labMap)
                                            .title(labAddress).icon(BitmapDescriptorFactory.fromResource(R.mipmap.add_a_lab)));
                                    map.setTrafficEnabled(true);
                                    map.setIndoorEnabled(true);
                                    map.setBuildingsEnabled(true);
                                    map.getUiSettings().setZoomControlsEnabled(true);
                                }
                            });
                            fragmentManager.beginTransaction().replace(R.id.lab_map_layout, supportMapFragment).commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //get direction
                    RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.lab_get_direction);
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(String.valueOf(latitude).isEmpty() || String.valueOf(longitude).isEmpty()) {
                                Toast.makeText(getContext(), "Location is not available", Toast.LENGTH_LONG).show();
                            } else {
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=my location" +"&daddr=" + latitude + "," + longitude));
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

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(getContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void shareIt(final String name, final String phone, final String address, final String speciality, final String rating) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + "Phone: " + phone + "\n" + "Address: "
                + address + "\n" + "Speciality: " + speciality + "\n" + "Rating: " + rating);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));
    }

}
