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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    //private double latitude, longitude;
    private SupportMapFragment supportMapFragment;
    private ImageView mapLocation;
    private LinearLayout searchResultListing,searchResultLocation;
    private boolean isListingLayout = true;
    private List<Double> latitudeList = new ArrayList<>();
    private List<Double> longitudeList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private boolean isMapLoaded = false;
    private TextView topTenDoctorText;
    private List specialityIcon;
    private String appLang = LocalInformation.getLocaleLang();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clearPreferences();
        new GetAllWorkMaster().execute();
        relativeLayout = (RelativeLayout) getView().findViewById(R.id.search_layout);
        relativeLayout.setBackgroundColor(Color.WHITE);
        searchResultListing = (LinearLayout) getView().findViewById(R.id.search_result_listing);
        searchResultLocation = (LinearLayout) getView().findViewById(R.id.search_result_with_location);

        specialityIcon = new ArrayList();
        specialityIcon.addAll(SpecialityData.getmInstance().specialityListIcon);

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

        mapLocation = (ImageView) getView().findViewById(R.id.search_map_location);
        topTenDoctorText = (TextView) getView().findViewById(R.id.top_ten_doctors_text);
        topTenDoctorText.setVisibility(View.GONE);
        topTenDoctorText.setTypeface(typeface);
        mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isListingLayout) {
                    searchResultListing.setVisibility(View.VISIBLE);
                    searchResultLocation.setVisibility(View.GONE);
                    isListingLayout = false;
                    mapLocation.setImageResource(R.mipmap.maplocation);
                } else {
                    FragmentManager fragmentManager;
                    fragmentManager = getActivity().getSupportFragmentManager();
                    supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.search_result_relative_layout);
                    if(!isMapLoaded) {
                        supportMapFragment = SupportMapFragment.newInstance();
                        isMapLoaded = true;
                    }
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap map) {
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlg, 16));
                            for(int i = 0; i < latitudeList.size(); i++) {
                                map.addMarker(new MarkerOptions().position(new
                                        LatLng(Double.valueOf(latitudeList.get(i)), Double.valueOf(longitudeList.get(i))))
                                        .title(titles.get(i).toString()));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latitudeList.get(i)),
                                        Double.valueOf(longitudeList.get(i))), 16));
                            }
                            map.setTrafficEnabled(true);
                            map.setIndoorEnabled(true);
                            map.setBuildingsEnabled(true);
                            map.getUiSettings().setZoomControlsEnabled(true);
                        }
                    });
                    fragmentManager.beginTransaction().replace(R.id.search_result_relative_layout, supportMapFragment).commit();

                    searchResultListing.setVisibility(View.GONE);
                    searchResultLocation.setVisibility(View.VISIBLE);
                    isListingLayout = true;
                    mapLocation.setImageResource(R.mipmap.menu_1);
                }
            }
        });
    }

    public void listingOrMapLayout() {
        isListingLayout = true;
        isMapLoaded = false;
        latitudeList = new ArrayList<>();
        longitudeList = new ArrayList<>();
        if(isListingLayout) {
            searchResultListing.setVisibility(View.VISIBLE);
            searchResultLocation.setVisibility(View.GONE);
            isListingLayout = false;
            mapLocation.setImageResource(R.mipmap.maplocation);
        } else {
            searchResultListing.setVisibility(View.GONE);
            searchResultLocation.setVisibility(View.VISIBLE);
            isListingLayout = true;
            mapLocation.setImageResource(R.mipmap.maplocation);
        }
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
                } finally {
                    mClient.close();
                }

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
        Log.v("OnResume: ", "resume");
        String globalSearchType = Preference.getValue(getContext(), "GLOBAL_FILTER_TYPE", "");
        Log.v("Search type", globalSearchType);
        setMenuVisibility(true);
        relativeLayout.setBackgroundColor(Color.WHITE);
        String search = Preference.getValue(getContext(), "IS_SEARCH", "");
        if(search.equals("1")) {
            String location = Preference.getValue(getContext(), "FILTER_LOCATION", "");
            String speciality = Preference.getValue(getContext(), "FILTER_SPECIALITY", "");
            String gender = Preference.getValue(getContext(), "FILTER_GENDER", "");
            String name = Preference.getValue(getContext(), "FILTER_NAME", "");
            String searchData = Preference.getValue(getContext(), "SEARCH_KEYWORD", "");
            Log.v("Location: ", location);
            Log.v("spec: ", speciality);
            Log.v("gender", gender);
            Log.v("name", name);
            Log.v("search data", searchData);
            Log.v("Search", search);

            searchFilterRequest(location, speciality, gender, name, searchData, globalSearchType);
        }


    }

    public void searchFilterRequest(final String location, final String speciality, final String gender, final String name, final String search,
                                    final String searchType) {

        class SearchFilter extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/filterSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
            String quickLocation = location;
            String quickSpeciality = speciality;
            String quickGender = gender;
            String quickName = name;
            String quickSearch = search;
            String quickSearchType = searchType;
            String quickUserId = Preference.getValue(getContext(), "LOGIN_ID", "");

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("location", quickLocation));
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));
                nameValuePairs.add(new BasicNameValuePair("name", quickName));
                nameValuePairs.add(new BasicNameValuePair("searchdata", quickSearch));
                nameValuePairs.add(new BasicNameValuePair("searchType", quickSearchType));
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
                Log.v("Global Search", result);
                if(Preference.getValue(getContext(), "GLOBAL_FILTER_TYPE", "").equals("doctor")) {
                  new RenderDoctorSearchResult(result);
                } else {
                    new RenderSearchResult(result);
                }
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
            Log.v("result Render", result);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            try {
                listingOrMapLayout();
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
                    Log.v("Result All: ", searchResult.toString());
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

                        //speciality icons
                        LinearLayout specialityLayout = (LinearLayout) view.findViewById(R.id.speciality_mipmap);
                        String allSpecialityMipmap = filterData.getString("Mipmap");
                        String[] mipmapArray = allSpecialityMipmap.split(",");

                        for (String specialityMipmapName : mipmapArray) {
                            View specialityIcon;
                            LayoutInflater specialityIconInflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            specialityIcon = specialityIconInflator.inflate(R.layout.mipmap_icon, null);
                            ImageView mipmapSpec = (ImageView) specialityIcon.findViewById(R.id.mipmapIcon);
                            int mipmapId = getContext().getResources().getIdentifier(specialityMipmapName
                                    , "mipmap", getContext().getPackageName());
                            mipmapSpec.setImageResource(mipmapId);

                            specialityLayout.addView(specialityIcon);
                        }

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
                        textView4.setText("0 " + getResources().getString(R.string.total_reviews));
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
                        final String detailsId =  filterData.getString("WorkId");
                        final String hospitalName = filterData.getString("Name");

                        final String workType = filterData.getString("WorkType");

                        if(!filterData.getString("Latitude").equals("null") &&
                                !filterData.getString("Latitude").isEmpty() &&
                                !filterData.getString("Longitude").equals("null") &&
                                !filterData.getString("Longitude").isEmpty()) {
                            latitudeList.add(Double.valueOf(filterData.getString("Latitude")));
                            longitudeList.add(Double.valueOf(filterData.getString("Longitude")));
                            titles.add(filterData.getString("Name"));
                        }
                        if(workType.equals("Lab")) {
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

                                    LabProfileFragment doctorprofile = new LabProfileFragment();
                                    doctorprofile.setArguments(args);

                                    mFragmentTransaction.addToBackStack("labProfile");
                                    mFragmentTransaction.replace(R.id.search_layout, doctorprofile).commit();
                                }
                            });
                        } else if(workType.equals("Clinic")) {
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

                                    ClinicProfileFragment doctorprofile = new ClinicProfileFragment();
                                    doctorprofile.setArguments(args);

                                    mFragmentTransaction.addToBackStack("clinicProfile");
                                    mFragmentTransaction.replace(R.id.search_layout, doctorprofile).commit();
                                }
                            });
                        } else if(workType.equals("Hospital")) {
                            LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                            showDetails.setId(getId() + i);
                            showDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle args = new Bundle();
                                    args.putString("id", detailsId);
                                    args.putString("hospitalName", hospitalName);

                                    FragmentManager mFragmentManager;
                                    mFragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction mFragmentTransaction;
                                    mFragmentTransaction = mFragmentManager.beginTransaction();

                                    HospitalProfileFragment doctorprofile = new HospitalProfileFragment();
                                    doctorprofile.setArguments(args);

                                    mFragmentTransaction.addToBackStack("hospitalProfile");
                                    mFragmentTransaction.replace(R.id.search_layout, doctorprofile).commit();
                                }
                            });
                        }
                    }
                    Log.v("Lat & Long: ", latitudeList.toString() + longitudeList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchKeyword.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            topTenDoctorText.setVisibility(View.GONE);
        }

    }

    public class RenderHospitelSearchResult {
        RenderHospitelSearchResult(String result) {
            //hideFilterIcon();
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            try {
                listingOrMapLayout();
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
                        textView4.setText("30 " + getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        //rating
                        RatingBar doctorUserRating = (RatingBar) view.findViewById(R.id.doctor_user_rating);
                        doctorUserRating.setRating(3.5f);

                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("Phone");
                        final String hospitalName = filterData.getString("Name");
                        final String hospitalAddress = filterData.getString("Address");

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

                                mFragmentTransaction.addToBackStack("hospitalProfile");
                                mFragmentTransaction.replace(R.id.search_layout, hospitalProfileFragment).commit();
                            }
                        });

                        if(!filterData.getString("Latitude").equals("null") &&
                                !filterData.getString("Latitude").isEmpty() &&
                                !filterData.getString("Longitude").equals("null") &&
                                !filterData.getString("Longitude").isEmpty()) {
                            latitudeList.add(Double.valueOf(filterData.getString("Latitude")));
                            longitudeList.add(Double.valueOf(filterData.getString("Longitude")));
                            titles.add(filterData.getString("Name"));
                        }

                        //share on social
                        ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shareIt(hospitalName, callDialer, hospitalAddress, "");
                            }
                        });

                        //specialites
                        LinearLayout specialityLayout = (LinearLayout) view.findViewById(R.id.speciality_mipmap);

                        String allSpecialityMipmap = filterData.getString("Mipmap");
                        String[] mipmapArray = allSpecialityMipmap.split(",");
                        for (String specialityMipmapName : mipmapArray) {
                            View mipmapIcon;
                            LayoutInflater mipmapIconInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            mipmapIcon = mipmapIconInflater.inflate(R.layout.mipmap_icon, null);
                            ImageView mipmapSpec = (ImageView) mipmapIcon.findViewById(R.id.mipmapIcon);

                            int mipmapId = getContext().getResources().getIdentifier(specialityMipmapName
                                    , "mipmap", getContext().getPackageName());
                            //ImageView spec = new ImageView(getContext());
                            mipmapSpec.setImageResource(mipmapId);
                            //int dp = (int) (getContext().getResources().getDimension(R.dimen.doctor_speciality_icon_width_height) / getContext().getResources().getDisplayMetrics().density);

                            specialityLayout.addView(mipmapIcon);
                        }
                        /*
                        JSONArray hospitalSpec = jsonObject.getJSONArray("Speciality");
                        for (int j = 0; j < hospitalSpec.length(); j++) {
                            final JSONObject hospitalSpeciality = hospitalSpec.getJSONObject(i);
                            ImageView spec = new ImageView(getContext());
                            int mipmapId = getContext().getResources().getIdentifier(hospitalSpeciality.getString("Mipmap"), "mipmap", getContext().getPackageName());
                            spec.setImageResource(mipmapId);
                            spec.setMinimumHeight(100);
                            spec.setMaxHeight(50);
                            spec.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                            specialityLayout.addView(spec);
                        }*/
                    }
                    Log.v("Lat & Long: ", latitudeList.toString() + longitudeList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            searchKeyword.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            topTenDoctorText.setVisibility(View.GONE);
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
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            try {
                listingOrMapLayout();
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
                    Log.v("Result Inside Class: ", searchResult.toString());
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

                        //speciality
                        int specialityId = getContext().getResources().getIdentifier(filterData.getString("Mipmap"),
                                "mipmap",
                                getContext().getPackageName());
                        ImageView specialityImage = (ImageView) view.findViewById(R.id.speciality);
                        specialityImage.setImageResource(specialityId);

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
                        final String clinicAddress = filterData.getString("Address");
                        final String clinicName = filterData.getString("Name");

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
                        final String mipmap = filterData.getString("Mipmap");
                        final String speciality = filterData.getString("Speciality");

                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", clinicId);
                                args.putString("mipmap", mipmap);
                                args.putString("speciality", speciality);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                ClinicProfileFragment clinicProfileFragment = new ClinicProfileFragment();
                                clinicProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("clinicProfile");
                                mFragmentTransaction.replace(R.id.search_layout, clinicProfileFragment).commit();
                            }
                        });

                        if(!filterData.getString("Latitude").equals("null") &&
                                !filterData.getString("Latitude").isEmpty() &&
                                !filterData.getString("Longitude").equals("null") &&
                                !filterData.getString("Longitude").isEmpty()) {
                            latitudeList.add(Double.valueOf(filterData.getString("Latitude")));
                            longitudeList.add(Double.valueOf(filterData.getString("Longitude")));
                            titles.add(filterData.getString("Name"));
                        }

                        //share on social
                        ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.v("name", clinicName);
                                shareIt(clinicName, callDialer, clinicAddress, "");
                            }
                        });

                    }
                    Log.v("Lat & Long: ", latitudeList.toString() + longitudeList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchKeyword.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            topTenDoctorText.setVisibility(View.GONE);
        }
    }

    public class RenderDoctorSearchResult {
        RenderDoctorSearchResult(String result) {
            hideFilterIcon();
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            try {
                listingOrMapLayout();
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
                        if(!filterData.getString("TotalReview").equals("null") &&
                                !filterData.getString("TotalReview").isEmpty())
                            textView4.setText(filterData.getString("TotalReview") + " " + getResources().getString(R.string.total_reviews));
                        else
                            textView4.setText(0 + " " + getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        //rating
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.doctor_user_rating);
                        if(!filterData.getString("RatingAvg").equals("null") &&
                                !filterData.getString("RatingAvg").isEmpty())
                            ratingBar.setRating(Float.valueOf(filterData.getString("RatingAvg")));

                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("Phone");
                        final String doctorName = filterData.getString("Name");
                        final String doctorAddress = filterData.getString("Address");
                        final String doctorRating = String.valueOf(filterData.getString("RatingAvg"));

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
                        final String clinicId =  filterData.getString("DoctorId");
                        final String doctorname =  filterData.getString("Name");
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
                            }
                        });

                        if(!filterData.getString("Latitude").equals("null") &&
                                !filterData.getString("Latitude").isEmpty() &&
                                !filterData.getString("Longitude").equals("null") &&
                                !filterData.getString("Longitude").isEmpty()) {
                            latitudeList.add(Double.valueOf(filterData.getString("Latitude")));
                            longitudeList.add(Double.valueOf(filterData.getString("Longitude")));
                            titles.add(filterData.getString("Name"));
                        }

                        //social share
                        ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shareIt(doctorName, callDialer, doctorAddress, doctorRating);
                            }
                        });


                    }
                    Log.v("Lat & Long: ", latitudeList.toString() + longitudeList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchKeyword.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            topTenDoctorText.setVisibility(View.GONE);
        }
    }

    public class RenderLabSearchResult {
        RenderLabSearchResult(String result) {
            hideFilterIcon();
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            try {
                listingOrMapLayout();
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

                        //speciality
                        int specialityId = getContext().getResources().getIdentifier(filterData.getString("Mipmap"),
                                "mipmap",
                                getContext().getPackageName());
                        ImageView specialityImage = (ImageView) view.findViewById(R.id.speciality);
                        specialityImage.setImageResource(specialityId);

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
                        final String labName = filterData.getString("Name");
                        final String labAddress = filterData.getString("Address");

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
                        final String mipmap = filterData.getString("Mipmap");
                        final String speciality = filterData.getString("Speciality");

                        LinearLayout showDetails = (LinearLayout) view.findViewById(R.id.show_details);
                        showDetails.setId(getId() + i);
                        showDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("id", detailsId);
                                args.putString("mipmap", mipmap);
                                args.putString("speciality", speciality);

                                FragmentManager mFragmentManager;
                                mFragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction;
                                mFragmentTransaction = mFragmentManager.beginTransaction();

                                LabProfileFragment labProfileFragment = new LabProfileFragment();
                                labProfileFragment.setArguments(args);

                                mFragmentTransaction.addToBackStack("labProfile");
                                mFragmentTransaction.replace(R.id.search_layout, labProfileFragment).commit();
                            }
                        });

                        //social share
                        ImageView socialShare = (ImageView) view.findViewById(R.id.share_details);
                        socialShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shareIt(labName, callDialer, labAddress, "");
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchKeyword.setVisibility(View.VISIBLE);
            mapLocation.setVisibility(View.VISIBLE);
            topTenDoctorText.setVisibility(View.GONE);
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
        String URL = webServiceUrl + "work/allWorkMaster?lang=" + appLang;
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
            } catch (ClientProtocolException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                mClient.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("Render Work All result", result);
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

    @Override
    public void onDestroyView() {
        //mContainer.removeAllViews();
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.search_layout);
        mContainer.removeAllViews();
        super.onDestroyView();
    }


    /**
     * Doctor search
     */
    public void doctorSearch() {
        Preference.setValue(getContext(), "GLOBAL_FILTER_TYPE", "doctor");
        final String location = Preference.getValue(getContext(), "DOCTOR_LOCATION_SEARCH", "");
        Preference.getValue(getContext(), "FILTER_LOCATION", location);
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
                Log.v("Doctor Result: ", result);
                new RenderDoctorSearchResult(result);
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
        //Preference.setValue(getContext(), "DOCTOR_LOCATION_SEARCH", "");
        //Preference.setValue(getContext(), "DOCTOR_SPECIALITY_SEARCH", "");
        //Preference.setValue(getContext(), "DOCTOR_GENDER_SEARCH", "");
        searchKeyword.setVisibility(View.VISIBLE);
        mapLocation.setVisibility(View.VISIBLE);
        topTenDoctorText.setVisibility(View.GONE);
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.v("Before render", result);
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    return mClient.execute(httpPost, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    mClient.close();
                }

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
        //final String gender = Preference.getValue(getContext(), "LAB_SEARCH_GENDER", "");
        final String speciality = Preference.getValue(getContext(), "LAB_SEARCH_SPECIALITY", "");
        class LabSearch extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "search/labSearch";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickSpeciality = speciality;
            //String quickGender = gender;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                //nameValuePairs.add(new BasicNameValuePair("gender", quickGender));
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
        labSearch.execute(speciality);

        Preference.setValue(getContext(), "LAB_SEARCH_GENDER", "");
        Preference.setValue(getContext(), "LAB_SEARCH_SPECIALITY", "");
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
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
            } else if(Preference.getValue(getContext(), "USER_BOOKMARKS", "").equals("true")) {
                String loginUserId = Preference.getValue(getContext(), "LOGIN_ID", "");
                /*searchKeyword.setVisibility(View.GONE);
                mapLocation.setVisibility(View.GONE);
                topTenDoctorText.setVisibility(View.VISIBLE);*/
                Preference.setValue(getContext(), "USER_BOOKMARKS", "");
                userBookmarks(loginUserId);
            } else if(Preference.getValue(getContext(), "PRIMARY_SEARCH", "").equals("true")) {
                Log.v("inside primary", "primary search");
                String primarySearch = Preference.getValue(getContext(), "PROMARY_SEARCH_KEY", "");
                Log.v("Primary Search Keyword", primarySearch);
                globalSearch(primarySearch);
                searchKeyword.setText(primarySearch);
                Preference.setValue(getContext(), "PRIMARY_SEARCH", "");
                Preference.setValue(getContext(), "PROMARY_SEARCH_KEY", "");
            } else if (Preference.getValue(getContext(), "SEARCH_REDIRECT", "").equals("true")){
                new GetAllWorkMaster().execute();
                Preference.setValue(getContext(), "SEARCH_REDIRECT", "");
                //globalSearch(searchKeyword.getText().toString());
            }
        }
    }

    public void userBookmarks(final String userId) {

        class DoctorCustomSearch extends AsyncTask<String, Void, String> {
            String URL = webServiceUrl + "bookmark/bookmarkList";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickUserId = userId;

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
                Log.v("Doctor: ", result);
                new RenderUserBookmarksResult(result);
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
        doctorCustomSearch.execute(userId);
    }

    class RenderUserBookmarksResult {
        RenderUserBookmarksResult(String result) {
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            try {
                listingOrMapLayout();
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
                    Log.v("ResultData: ", String.valueOf(searchResult.length()));
                    for(int i = 0; i < searchResult.length(); i++) {
                        final JSONObject filterData = searchResult.getJSONObject(i);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.search_result, null);

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
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        linearLayout.addView(view);

                        final String callDialer = filterData.getString("phone");

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
                        final String clinicId =  filterData.getString("doctorId");
                        final String doctorname =  filterData.getString("firstname") + " " + filterData.getString("lastname");
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
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchKeyword.setVisibility(View.GONE);
            mapLocation.setVisibility(View.GONE);
            topTenDoctorText.setVisibility(View.VISIBLE);
            //Preference.setValue(getContext(), "TOP_TEN_DOCTORS", "");
        }
    }

    public void hideFilterIcon() {
        //searchKeyword.setCompoundDrawables(null, null, null, null);
        //searchKeyword.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        //searchKeyword.setFocusable(false);
    }

    private int getCategoryPos(String category) {
        return specialityIcon.indexOf(category);
    }

    public void shareIt(final String name, final String phone, final String address, final String rating) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + "Phone: " + phone + "\n" + "Address: "
                + address + "\n" + "Rating: " + rating);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));
    }
}
