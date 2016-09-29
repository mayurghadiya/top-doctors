package searchnative.com.topdoctors;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;

/**
 * Created by root on 9/9/16.
 */
public class SearchFragment extends Fragment {

    EditText searchKeyword;
    String searchData;
    Typeface typeface;
    LinearLayout linearLayout;
    String webServiceUrl = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clearPreferences();

        //hospitalSearchCustom();
        new GetAllWorkMaster().execute();

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.search_layout);
        relativeLayout.setBackgroundColor(Color.WHITE);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");

        ImageView v = (ImageView) getActivity().findViewById(R.id.search_result_menu);
        searchKeyword = (EditText) getView().findViewById(R.id.hope_page_main_search_custom);

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawerLayout mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                mDrawer.openDrawer(Gravity.LEFT);
                return true;
            }
        });

        linearLayout = (LinearLayout) getView().findViewById(R.id.search_result_layout);

        //new GetAllWorkMaster().execute();

        final EditText editText = (EditText) getView().findViewById(R.id.hope_page_main_search_custom);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        //view.clearFocus();
                        Preference.setValue(getContext(), "SEARCH_KEYWORD", editText.getText().toString());
                        startActivity(new Intent(getContext(), SearchFilterPop.class));
                        //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        relativeLayout = (RelativeLayout) getView().findViewById(R.id.search_layout);
                        relativeLayout.setBackgroundColor(Color.GRAY);
                        return true;
                    }

                }

                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    globalSearch(editText.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    Preference.setValue(getContext(), "SEARCH_KEYWORD", editText.getText().toString());
                    return true;
                }

                return false;
            }
        });
    }

    public void globalSearch(final String search) {

        class GlobalSearch extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/globalSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
            String quickSearch = search;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("search", quickSearch));
                nameValuePairs.add(new BasicNameValuePair("lang", LocalInformation.getLocaleLang()));

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
                new RenderSearchResult(result);
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
        GlobalSearch globalSearch = new GlobalSearch();
        globalSearch.execute(search);

    }

    @Override
    public void onResume(){
        super.onResume();
        setMenuVisibility(true);
        relativeLayout.setBackgroundColor(Color.WHITE);
        String search = Preference.getValue(getContext(), "IS_SEARCH", "");
        if(search.equals("1")) {
            String location = Preference.getValue(getContext(), "FILTER_LOCATION", "");
            String speciality = Preference.getValue(getContext(), "FILTER_SPECIALITY", "");
            String gender = Preference.getValue(getContext(), "FILTER_GENDER", "");
            String name = Preference.getValue(getContext(), "FILTER_NAME", "");
            String searchData = Preference.getValue(getContext(), "SEARCH_KEYWORD", "");

            searchFilterRequest(location, speciality, gender, name, searchData);
        }


    }

    public void searchFilterRequest(final String location, final String speciality, final String gender, final String name, final String search) {

        class SearchFilter extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/filterSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
            String quickLocation = location;
            String quickSpeciality = speciality;
            String quickGender = gender;
            String quickName = name;
            String quickSearch = search;


            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("location", quickLocation));
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));
                nameValuePairs.add(new BasicNameValuePair("name", quickName));
                nameValuePairs.add(new BasicNameValuePair("searchdata", quickSearch));

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
                new RenderSearchResult(result);
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

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.execute(location, speciality, gender, name);
    }

    public void clearPreferences() {
        Preference.setValue(getContext(), "IS_SEARCH", "");
        Preference.setValue(getContext(), "FILTER_LOCATION", "");
        Preference.setValue(getContext(), "FILTER_SPECIALITY", "");
        Preference.setValue(getContext(), "FILTER_GENDER", "");
        Preference.setValue(getContext(), "FILTER_NAME", "");
        Preference.setValue(getContext(), "SEARCH_KEYWORD", "");
    }

    public class RenderSearchResult {
        RenderSearchResult(String result) {
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
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

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
                                openPhoneDialer(callDialer);
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
                                mFragmentTransaction.replace(R.id.containerView, doctorprofile).commit();
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public class RenderHospitelSearchResult {
        RenderHospitelSearchResult(String result) {
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
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

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
                                openPhoneDialer(callDialer);
                            }
                        });

                        //doctor details
                        final String detailsId =  filterData.getString("HospitalId");

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

                                HospitalProfileFragment hospitalProfileFragment = new HospitalProfileFragment();
                                hospitalProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("profile");
                                mFragmentTransaction.replace(R.id.containerView, hospitalProfileFragment).commit();
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
     * Render clinic search results
     */
    public class RenderClinicSearchResult {
        RenderClinicSearchResult(String result) {
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
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

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
                                openPhoneDialer(callDialer);
                            }
                        });

                        //doctor details
                        final String clinicId =  filterData.getString("ClinicId");

                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", clinicId);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                ClinicProfileFragment clinicProfileFragment = new ClinicProfileFragment();
                                clinicProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("profile");
                                mFragmentTransaction.replace(R.id.containerView, clinicProfileFragment).commit();
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class RenderLabSearchResult {
        RenderLabSearchResult(String result) {
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
                    JSONArray searchResult = jsonObject.getJSONArray("data");
                    Log.v("Result: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

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
                                openPhoneDialer(callDialer);
                            }
                        });

                        //lab details
                        final String detailsId =  filterData.getString("LabId");

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

                                LabProfileFragment labProfileFragment = new LabProfileFragment();
                                labProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("profile");
                                mFragmentTransaction.replace(R.id.containerView, labProfileFragment).commit();
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
     * Open phone dialer
     * @param phoneNumber string
     */
    public void openPhoneDialer(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public class GetAllWorkMaster extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "work/allWorkMaster";
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(Void... params) {
            HttpGet httpGet = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                return mClient.execute(httpGet, responseHandler);
            } catch (ClientProtocolException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            mClient.close();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new RenderSearchResult(result);
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

    /**
     * Doctor search
     */
    public void doctorSearch() {
        final String location = Preference.getValue(getContext(), "DOCTOR_LOCATION_SEARCH", "");
        final String speciality = Preference.getValue(getContext(), "DOCTOR_SPECIALITY_SEARCH", "");
        final String gender = Preference.getValue(getContext(), "DOCTOR_GENDER_SEARCH", "");

        class DoctorCustomSearch extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/doctorSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickLocation = location;
            String quickSpeciality = speciality;
            String quickGender = gender;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("location", quickLocation));
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));

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
                Log.v("Doctor: ", result);
                new RenderSearchResult(result);
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

        DoctorCustomSearch doctorCustomSearch = new DoctorCustomSearch();
        doctorCustomSearch.execute(location, speciality, gender);

        Log.v("location", location);
        Log.v("speciality", speciality);
        Log.v("gender", gender);
        Preference.setValue(getContext(), "DOCTOR_LOCATION_SEARCH", "");
        Preference.setValue(getContext(), "DOCTOR_SPECIALITY_SEARCH", "");
        Preference.setValue(getContext(), "DOCTOR_GENDER_SEARCH", "");
    }

    /**
     * Hospital search
     */
    public void hospitalSearchCustom() {
        class HospitalSearch extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "search/hospitalSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                HttpPost httpGet = new HttpPost(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    return mClient.execute(httpGet, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                mClient.close();
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                new RenderHospitelSearchResult(result);
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

        HospitalSearch hospitalSearch = new HospitalSearch();
        hospitalSearch.execute();
    }

    public void clinicSearchCustom() {
        class ClinicSearch extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "search/clinicSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                HttpPost httpGet = new HttpPost(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    return mClient.execute(httpGet, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                mClient.close();
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                new RenderClinicSearchResult(result);
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

        ClinicSearch clinicSearch = new ClinicSearch();
        clinicSearch.execute();
    }

    public void labSearch() {
        final String gender = Preference.getValue(getContext(), "LAB_SEARCH_GENDER", "");
        final String speciality = Preference.getValue(getContext(), "LAB_SEARCH_SPECIALITY", "");

        class LabSearch extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/labSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickSpeciality = speciality;
            String quickGender = gender;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));

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
                new RenderLabSearchResult(result);
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

        LabSearch labSearch = new LabSearch();
        labSearch.execute(gender, speciality);

        Preference.setValue(getContext(), "LAB_SEARCH_GENDER", "");
        Preference.setValue(getContext(), "LAB_SEARCH_SPECIALITY", "");
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        //String hospital = Preference.getValue(getContext(), "HOSPITAL_SEARCH", "");
//        Log.v("search", Preference.getValue(getContext(), "HOSPITAL_SEARCH", ""));
        if (visible) {
            if(Preference.getValue(getContext(), "HOSPITAL_SEARCH", "").equals("true")) {
                //Toast.makeText(getContext(), Preference.getValue(getContext(), "HOSPITAL_SEARCH", ""), Toast.LENGTH_SHORT).show();
                hospitalSearchCustom();
                Preference.setValue(getContext(), "HOSPITAL_SEARCH", "");
            } else if(Preference.getValue(getContext(), "CLINIC_SEARCH", "").equals("true")) {
                clinicSearchCustom();
                Preference.setValue(getContext(), "CLINIC_SEARCH", "");
            } else if(Preference.getValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "").equals("true")) {
                doctorSearch();
                Preference.setValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "");
            } else if(Preference.getValue(getContext(), "LAB_SEARCH_CUSTOM", "").equals("true")) {
                labSearch();
                Preference.setValue(getContext(), "LAB_SEARCH_CUSTOM", "");
            }
        }
    }

}
