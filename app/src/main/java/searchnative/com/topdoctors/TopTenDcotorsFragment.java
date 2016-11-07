package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
 * Created by root on 13/10/16.
 */

public class TopTenDcotorsFragment extends Fragment{

    private String webServiceUrl = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    private LinearLayout linearLayout;
    private Typeface typeface;
    private List specialityIcon;
    TabFragment tabFragment = new TabFragment();
    private TextView topTenDoctorTitle;
    private ImageView topTenDoctorsMenu;
    private String appLang = LocalInformation.getLocaleLang();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_ten_doctors, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");

        topTenDoctorTitle = (TextView) getView().findViewById(R.id.top_ten_doctor_title);
        topTenDoctorsMenu = (ImageView) getView().findViewById(R.id.top_ten_doctors_menu);

        //open navigation drawer
        topTenDoctorsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        topTenDoctorTitle.setTypeface(typeface);

        specialityIcon = new ArrayList();
        specialityIcon.addAll(SpecialityData.getmInstance().specialityListIcon);

        linearLayout = (LinearLayout) getView().findViewById(R.id.search_result_layout);

        new TopTenDoctorList().execute();
    }

    class TopTenDoctorList extends AsyncTask<Void, Void, String> {

        final String URL = webServiceUrl + "review/topTenDoctor?lang=" + appLang;
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            //nameValuePairs.add(new BasicNameValuePair("lang", appLang));
            try {
                HttpGet httpGet = new HttpGet(URL);
                //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                return mClient.execute(httpGet, responseHandler);
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                mClient.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("Top ten doctor", result);
            new RenderTopTenDoctors(result);
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

    class RenderTopTenDoctors {
        RenderTopTenDoctors(String result) {
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

                    linearLayout.addView(textView);
                } else {
                    JSONArray searchResult = jsonObject.getJSONArray("result");
                    Log.v("ResultData: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

                        //speciality icon
                        ImageView doctorSpecialityIcon = (ImageView) view.findViewById(R.id.speciality);
                        int id = getContext().getResources().getIdentifier(
                                specialityIcon.get(getCategoryPos(filterData.getString("Mipmap"))).toString(),
                                "mipmap", getContext().getPackageName());
                        doctorSpecialityIcon.setImageResource(id);

                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.search_title_work_place);
                        textView1.setText(filterData.getString("firstname") + " " + filterData.getString("lastname"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.search_work_place_address);
                        textView2.setText(filterData.getString("address"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.search_work_place_phone);
                        textView3.setText(filterData.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.search_total_reviews);
                        if(!filterData.getString("totalReview").equals("null") &&
                                !filterData.getString("totalReview").isEmpty())
                            textView4.setText(filterData.getString("totalReview") + " " + getResources().getString(R.string.total_reviews));
                        else
                            textView4.setText(0 + " " + getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        //rating
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.doctor_user_rating);
                        if(!filterData.getString("ratingAvg").equals("null") &&
                                !filterData.getString("ratingAvg").isEmpty())
                            ratingBar.setRating(Float.valueOf(filterData.getString("ratingAvg")));
                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.search_phone_dialer);
                        //imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });


                        //doctor details
                        final String clinicId =  filterData.getString("doctorId");
                        final String doctorname =  filterData.getString("firstname") + " " + filterData.getString("lastname");
                        final String address = filterData.getString("address");
                        final String rating = String.valueOf(filterData.getString("ratingAvg"));

                        ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.generateViewId();
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.v("Name", doctorname);
                                shareIt(doctorname, callDialer, address, rating);
                            }
                        });


                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", clinicId);
                                args.putString("name", doctorname);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                DoctorProfileFragment clinicProfileFragment = new DoctorProfileFragment();
                                clinicProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("doctorProfile");
                                mFragmentTransaction.add(R.id.search_layout, clinicProfileFragment).commit();
                                tabFragment.tabLayout.getTabAt(1).select();
                            }
                        });


                        //bookmarks
                        /*final ImageView addToBookmarks = (ImageView) view.findViewById(R.id.add_to_bookmarks);
                        //saddToBookmarks.setId(getId() + i + 10000 + i);
                        addToBookmarks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doctorBookMarks(clinicId);
                                Log.v("Status", Preference.getValue(getContext(), "BOOKMARKS_STATUS", ""));

                                if(Preference.getValue(getContext(), "BOOKMARKS_STATUS", "").equals("Bookmarked")) {
                                    addToBookmarks.setImageResource(R.mipmap.add_a_lab);
                                    Toast.makeText(getContext(), doctorname + " is successfully added in your bookmark.", Toast.LENGTH_SHORT).show();
                                } else {
                                    addToBookmarks.setImageResource(R.mipmap.bookmarks);
                                    Toast.makeText(getContext(), doctorname + "is successfully removed from your bookmark.", Toast.LENGTH_SHORT).show();
                                }
                                Preference.setValue(getContext(), "BOOKMARKS_STATUS", "");
                            }
                        });*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void shareIt(final String name, final String phone, final String address, final String rating) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + "Phone: " + phone + "\n" + "Address: "
                + address + "\n" + "Rating: " + rating);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));
    }

    public void doctorBookMarks(final String doctorId) {

        class DoctorBookmarksAddUpdate extends AsyncTask<String, Void, String> {

            String URL = AppConfig.getWebServiceUrl() + "bookmark/add";

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickDoctorId = doctorId;
            String quickUserId = Preference.getValue(getContext(), "LOGIN_ID", "");

            private String resultStatus = "";

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("doctorId", quickDoctorId));
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));

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

    private int getCategoryPos(String category) {
        return specialityIcon.indexOf(category);
    }

    public void openPhoneDialer(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
