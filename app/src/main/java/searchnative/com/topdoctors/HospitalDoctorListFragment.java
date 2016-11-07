package searchnative.com.topdoctors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ScrollView;
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
    private String hospitalId, hospitalName, hospitalAddress, hospitalPhone;
    private LinearLayout linearLayout;
    private List specialityIcon;
    private ImageView hospitalSocialShare;
    private String appLang = LocalInformation.getLocaleLang();

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
        hospitalAddress = getArguments().getString("hospitalAddress");
        hospitalPhone = getArguments().getString("hospitalPhone");
        //linearLayout = (LinearLayout) getView().findViewById(R.id.hospital_doctor_lists);
        //linearLayout.removeAllViews();
        //Toast.makeText(getContext(), hospitalName, Toast.LENGTH_SHORT).show();
        TextView profile_detail_name = (TextView) getView().findViewById(R.id.hospital_doctor_list_profile_detail_name);
        profile_detail_name.setTypeface(typeface, typeface.BOLD);
        profile_detail_name.setText(hospitalName);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");

        doctorList(hospitalId);

        specialityIcon = new ArrayList();
        specialityIcon.addAll(SpecialityData.getmInstance().specialityListIcon);

        ImageView imageView = (ImageView) getView().findViewById(R.id.hospital_doctor_lists_swipe_menu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        hospitalSocialShare = (ImageView) getView().findViewById(R.id.hospital_social_share);
        hospitalSocialShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(hospitalName, hospitalPhone, hospitalAddress, "");
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
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            }
        }

        HospitalDoctorList hospitalProfile = new HospitalDoctorList();
        hospitalProfile.execute(hospitalId);
    }

    public class RenderSearchResult {
        RenderSearchResult(String result) {
            linearLayout = (LinearLayout) getView().findViewById(R.id.hospital_doctor_lists);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
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

                    linearLayout.addView(textView);
                }
                else {
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
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
                        textView1.setText(filterData.getString("Name"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));
                        final String doctorName = filterData.getString("Name");

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.search_work_place_address);
                        textView2.setText(filterData.getString("Address"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));
                        final String doctorAddress = filterData.getString("Address");

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.search_work_place_phone);
                        textView3.setText(filterData.getString("Phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));
                        final String doctorPhone = filterData.getString("Phone");

                        //rating
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.doctor_user_rating);
                        if(!filterData.getString("RatingAvg").equals("null") &&
                                !filterData.getString("RatingAvg").isEmpty()) {
                            ratingBar.setRating(Float.valueOf(filterData.getString("RatingAvg")));

                        }
                        final String doctorRating = String.valueOf(filterData.getString("RatingAvg"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.search_total_reviews);
                        if(!filterData.getString("TotalReview").equals("null") &&
                                !filterData.getString("TotalReview").isEmpty()) {
                            textView4.setText(filterData.getString("TotalReview") + " " + getResources().getString(R.string.reviews));
                        } else {
                            textView4.setText(0 + getResources().getString(R.string.reviews));
                        }
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("Phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.search_phone_dialer);
                        imageView.setId(imageView.getId() + 100000 + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + callDialer));
                                startActivity(intent);
                            }
                        });

                        //add to bookmarks
                        //doctor details
                        final String detailsId =  filterData.getString("DoctorId");
                        final String name = filterData.getString("Name");

                        final ImageView addToBookmarks = (ImageView) view.findViewById(R.id.add_to_bookmarks);
                        //addToBookmarks.setId(addToBookmarks.getId() + i + 10000 + i);
                        addToBookmarks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doctorBookMarks(detailsId);
                                Log.v("Status", Preference.getValue(getContext(), "BOOKMARKS_STATUS", ""));

                                if(Preference.getValue(getContext(), "BOOKMARKS_STATUS", "").equals("Bookmarked")) {
                                    addToBookmarks.setImageResource(R.mipmap.add_a_lab);
                                    Toast.makeText(getContext(), name + " is successfully added in your bookmark.", Toast.LENGTH_SHORT).show();
                                } else {
                                    addToBookmarks.setImageResource(R.mipmap.bookmarks);
                                    Toast.makeText(getContext(), name + "is successfully removed from your bookmark.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", detailsId);
                                args.putString("name", name);

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

                        final ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Log.v("Name: ", name);
                                shareIt(doctorName, doctorPhone, doctorAddress, doctorRating);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
}
