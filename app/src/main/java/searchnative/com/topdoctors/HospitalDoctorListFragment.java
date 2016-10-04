package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayur on 28/9/16.
 */

public class HospitalDoctorListFragment extends Fragment {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    TabFragment tabFragment = new TabFragment();
    final String WEB_SERVICE_URL = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private Typeface typeface;
    private String hospitalId, hospitalName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hospital_doctor_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hospitalId = getArguments().getString("id");
        hospitalName = getArguments().getString("hospitalName");
        //Toast.makeText(getContext(), Preference.getValue(getContext(), "HOSPITAL_SEARCH", ""), Toast.LENGTH_SHORT).show();

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");

        doctorList(hospitalId);

        ImageView imageView = (ImageView) getView().findViewById(R.id.hospital_doctor_lists_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    public void doctorList(final String hospitalId) {

        class HospitalDoctorList extends AsyncTask<String, Void, String> {
            String URL = WEB_SERVICE_URL + "profile/hospitalDoctorList";

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
                //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                new RenderSearchResult(result);
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

        HospitalDoctorList hospitalProfile = new HospitalDoctorList();
        hospitalProfile.execute(hospitalId);
    }

    public class RenderSearchResult {
        RenderSearchResult(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.hospital_doctor_lists);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
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
                    //title name
                    TextView profile_detail_name = (TextView) getView().findViewById(R.id.profile_detail_name);
                    profile_detail_name.setTypeface(typeface, typeface.BOLD);
                    profile_detail_name.setText(hospitalName);

                    linearLayout.addView(textView);
                } else {
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

                        //title name
                        TextView profile_detail_name = (TextView) getView().findViewById(R.id.profile_detail_name);
                        profile_detail_name.setTypeface(typeface, typeface.BOLD);
                        profile_detail_name.setText(hospitalName);

                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.search_title_work_place);
                        textView1.setText(filterData.getString("Name"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.search_work_place_address);
                        textView2.setText(filterData.getString("Address"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.search_work_place_phone);
                        textView3.setText(filterData.getString("Phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.search_total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("Phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.search_phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + callDialer));
                                startActivity(intent);
                            }
                        });

                        //doctor details
                        final String detailsId =  filterData.getString("DoctorId");

                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", detailsId);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                DoctorProfileFragment doctorprofile = new DoctorProfileFragment();
                                doctorprofile.setArguments(args);

                                mFragmentTransaction.addToBackStack("profile");
                                mFragmentTransaction.replace(R.id.search_layout, doctorprofile).commit();
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
