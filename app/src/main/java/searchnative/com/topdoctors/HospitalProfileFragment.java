package searchnative.com.topdoctors;

import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hospital_profile, container, false);

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
                    doctorPhone.setText("Call (" + profile.getString("Phone") + ")");
                    doctorPhone.setTypeface(typeface);

                    //get direction
                    TextView getDirection = (TextView) getView().findViewById(R.id.textView7);
                    getDirection.setTypeface(typeface);

                    //write a review
                    TextView writeReview = (TextView) getView().findViewById(R.id.write_a_review_button);
                    writeReview.setTypeface(typeface);

                    //total doctors
                    TextView totalDoctors = (TextView) getView().findViewById(R.id.total_doctor_button);
                    totalDoctors.setText(jsonObject.getString("totalDcotors") + " Doctors");
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
                            mFragmentTransaction.replace(R.id.containerView, hospitalDoctorListFragment).commit();
                        }
                    });
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}
