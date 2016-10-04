package searchnative.com.topdoctors;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class PrimaryFragment extends Fragment {

    LinearLayout mLinearLayout, labLinearLayout;
    ValueAnimator mAnimator, labAnimator;

    Spinner spinner1, spinnerSpeciality, spinnerLocation, labSpecialitySpinner, labGenderSpinner;
    Typeface typeface;
    String[] genderArray;
    List<String> specialityListData, countryDataList;
    ArrayAdapter<String> spinnerArrayAdapter, specialityArrayAdapter;
    String webServiceUrl = AppConfig.getWebServiceUrl();
    private ProgressDialog mProgressDialog;
    TextView home_page_lab_search;

    //doctor search param
    String getDoctorSearchGender = "", getDoctorSearchSpeciality = "", getDoctorSearchLocation = "";

    //lab search param
    String getLabSearchGender = "", getLabSearchSpeciality = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.primary_layout, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                collapse();
                labCollapse();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

        //expandable
        mLinearLayout = (LinearLayout) getView().findViewById(R.id.p_expandable);
        labLinearLayout = (LinearLayout) getView().findViewById(R.id.lab_expandable);
        //mLinearLayoutHeader = (LinearLayout) getView().findViewById(R.id.p_header);

        final TextView tv = (TextView) getView().findViewById(R.id.home_page_doctor_search_doctor);
        final TextView etLabsSearch = (TextView) getView().findViewById(R.id.home_page_search_lab_textview);
        mLinearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mLinearLayout.setVisibility(View.GONE);

                final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                mLinearLayout.measure(widthSpec, heightSpec);

                mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
                return true;
            }
        });

        //lab expandable
        labLinearLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                labLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                labLinearLayout.setVisibility(View.GONE);

                final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                labLinearLayout.measure(widthSpec, heightSpec);
                labAnimator = labSlideAnimator(0, labLinearLayout.getMeasuredHeight());

                return true;
            }
        });

        //final Drawable img = getContext().getResources().getDrawable(R.mipmap.labs);
        //final Drawable startImg = getContext().getResources().getDrawable(R.mipmap.doctors);
        final Drawable imgArrow = getContext().getResources().getDrawable(R.mipmap.dropdown_arrow);
        final Drawable imgArrawTop = getContext().getResources().getDrawable(R.mipmap.dropdown_arrow_top);
        //get the density of the device

        float density = getResources().getDisplayMetrics().density;
        //Toast.makeText(getContext(), "density: " + density, Toast.LENGTH_LONG).show();
        if(density >= 0.75 && density < 1.0) {
            imgArrow.setBounds(0,0,10,10);
            imgArrawTop.setBounds(0,0,10,10);
        } else if(density >= 1.0 && density <= 1.5) {
            imgArrow.setBounds(0,0,22,22);
            imgArrawTop.setBounds(0,0,22,22);
        } else if(density > 1.5 && density < 2.0) {
            imgArrow.setBounds(0, 0, 25, 25);
            imgArrawTop.setBounds(0,0,25,25);
        } else if (density >= 2.0 && density <= 2.5) {
            imgArrow.setBounds(0,0,30,30);
            imgArrawTop.setBounds(0,0,30,30);
        } else if(density > 2.5 && density < 3.0) {
            imgArrow.setBounds(0,0,40,40);
            imgArrawTop.setBounds(0,0,40,40);
        } else if(density >= 3.0 && density < 3.5) {
            imgArrow.setBounds(0, 0, 50, 50);
            imgArrawTop.setBounds(0,0,40,40);
        } else if(density >= 3.5 && density <= 4.0) {
            imgArrow.setBounds(0, 0, 60, 60);
            imgArrawTop.setBounds(0,0,60,60);
        } else if(density > 4.0) {
            imgArrow.setBounds(0, 0, 70, 70);
            imgArrawTop.setBounds(0,0,70,70);
        } else {
            imgArrow.setBounds(0,0,40,40);
            imgArrawTop.setBounds(0,0,40,40);
        }
        if(LocalInformation.getLocaleLang().equals("ar")) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLinearLayout.getVisibility()==View.GONE){
                        tv.setCompoundDrawables(imgArrawTop,null,null,null);
                        expand();
                    }else{
                        tv.setCompoundDrawables(imgArrow,null,null,null);
                        collapse();
                    }
                }
            });

            etLabsSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(labLinearLayout.getVisibility() == View.GONE) {
                        etLabsSearch.setCompoundDrawables(imgArrawTop, null, null, null);
                        labExpand();
                    } else {
                        etLabsSearch.setCompoundDrawables(imgArrow, null, null, null);
                        labCollapse();
                    }
                }
            });
        } else {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLinearLayout.getVisibility()==View.GONE){
                        tv.setCompoundDrawables(null,null,imgArrawTop,null);
                        expand();
                    }else{
                        tv.setCompoundDrawables(null,null,imgArrow,null);
                        collapse();
                    }
                }
            });

            etLabsSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(labLinearLayout.getVisibility() == View.GONE) {
                        etLabsSearch.setCompoundDrawables(null, null, imgArrawTop, null);
                        labExpand();
                    } else {
                        etLabsSearch.setCompoundDrawables(null, null, imgArrow, null);
                        labCollapse();
                    }
                }
            });
        }

        ImageView v = (ImageView) getView().findViewById(R.id.swipe_menu);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawerLayout mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                mDrawer.openDrawer(Gravity.LEFT);
                return true;
            }
        });


        EditText etMainSearch = (EditText) getView().findViewById(R.id.hope_page_main_search);
        //EditText etDoctorSearchLocation = (EditText) getView().findViewById(R.id.home_doctor_search_location);
        TextView etHospitalSearch = (TextView) getView().findViewById(R.id.home_page_search_hospital_textview);
        TextView etClinicSearch = (TextView) getView().findViewById(R.id.home_page_search_clinics_textview);

        //set font
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");
        etMainSearch.setTypeface(typeface);
        tv.setTypeface(typeface);
        etHospitalSearch.setTypeface(typeface);
        etClinicSearch.setTypeface(typeface);
        etLabsSearch.setTypeface(typeface);

        //location spinner configuration
        spinnerLocation = (Spinner) getView().findViewById(R.id.spinner_location);
        countryDataList = new ArrayList<String>();
        countryDataList.add(getResources().getString(R.string.country_name));
        spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, countryDataList) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                //View view  = super.getDropDownView(position, convertView, parent);
                if(position == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                    textView.setTextColor(Color.GRAY);
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTypeface(typeface);
                ((TextView) view).setTextColor(Color.parseColor("#bcbbc0"));
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        new GetJSONCountry().execute();
        spinnerArrayAdapter.setNotifyOnChange(true);
        final int listSize = countryDataList.size() - 1;
        spinnerLocation.setAdapter(spinnerArrayAdapter);

        Preference.setValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "");

        //location/country wise doctor search
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getDoctorSearchLocation = spinnerLocation.getSelectedItem().toString();
                    //Toast.makeText(getContext(), "density: " + getDoctorSearchLocation, Toast.LENGTH_LONG).show();
                    Preference.setValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "true");
                    Preference.setValue(getContext(), "DOCTOR_LOCATION_SEARCH", "");
                    Preference.setValue(getContext(), "DOCTOR_LOCATION", "");
                    Preference.setValue(getContext(), "DOCTOR_LOCATION_SEARCH", getDoctorSearchLocation);
                    Preference.setValue(getContext(), "DOCTOR_LOCATION", getDoctorSearchLocation);
                    customSearch();
                    //search doctors based on location/country
                    //getDoctorSearchLocation = spinnerLocation.getSelectedItem().toString();
                    //searchDoctors(getDoctorSearchGender, getDoctorSearchSpeciality, getDoctorSearchLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //speciality spinner configuration
        spinnerSpeciality = (Spinner) getView().findViewById(R.id.spinner_speciality);
        labSpecialitySpinner = (Spinner) getView().findViewById(R.id.lab_spinner_speciality);
        specialityListData = new ArrayList<String>();
        specialityListData.add(getResources().getString(R.string.speciality));
        specialityArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, specialityListData) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                //View view  = super.getDropDownView(position, convertView, parent);
                if(position == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                    textView.setTextColor(Color.GRAY);
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(typeface);
                ((TextView) v).setTextColor(Color.parseColor("#bcbbc0"));

                return v;
            }
        };
        specialityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        new GetJSONData().execute();
        specialityArrayAdapter.setNotifyOnChange(true);
        spinnerSpeciality.setAdapter(specialityArrayAdapter);
        labSpecialitySpinner.setAdapter(specialityArrayAdapter);

        //speciality wise doctor search
        spinnerSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    //search doctors based on speciality
                    getDoctorSearchSpeciality = spinnerSpeciality.getSelectedItem().toString();
                    Preference.setValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "true");
                    Preference.setValue(getContext(), "DOCTOR_SPECIALITY_SEARCH", "");
                    Preference.setValue(getContext(), "DOCTOR_SPECIALITY_SEARCH", getDoctorSearchSpeciality);
                    customSearch();
                    //searchDoctors(getDoctorSearchGender, getDoctorSearchSpeciality, getDoctorSearchLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Preference.setValue(getContext(), "LAB_SEARCH_CUSTOM", "");

        //speciality wise lab search
        labSpecialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    //search lab based on speciality
                    getLabSearchSpeciality = labSpecialitySpinner.getSelectedItem().toString();
                    Preference.setValue(getContext(), "LAB_SEARCH_CUSTOM", "true");
                    Preference.setValue(getContext(), "LAB_SEARCH_SPECIALITY", getLabSearchSpeciality);
                    customSearch();
                    //searchLab(getLabSearchGender, getLabSearchSpeciality);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //gender spinner configuration
        spinner1 = (Spinner) getView().findViewById(R.id.spinner1_gender);
        labGenderSpinner = (Spinner) getView().findViewById(R.id.lab_spinner_gender);
        genderArray = getResources().getStringArray(R.array.gender);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, genderArray) {

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                //View view  = super.getDropDownView(position, convertView, parent);
                if(position == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                    textView.setTextColor(Color.GRAY);
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(typeface);
                ((TextView) v).setTextColor(Color.parseColor("#bcbbc0"));

                return v;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(arrayAdapter);
        labGenderSpinner.setAdapter(arrayAdapter);
        addListenerOnSpinnerItemSelection();

        //gender wise doctor search
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    //search doctors based on gender
                    getDoctorSearchGender = spinner1.getSelectedItem().toString();
                    Preference.setValue(getContext(), "DOCTOR_SEARCH_CUSTOM", "true");
                    Preference.setValue(getContext(), "DOCTOR_GENDER_SEARCH", getDoctorSearchGender);
                    customSearch();
                    //searchDoctors(getDoctorSearchGender, getDoctorSearchSpeciality, getDoctorSearchLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        home_page_lab_search = (TextView) getView().findViewById(R.id.home_page_lab_search);
        home_page_lab_search.setTypeface(typeface);
        home_page_lab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preference.setValue(getContext(), "LAB_SEARCH_CUSTOM", "true");
                customSearch();
            }
        });

        //gender wise lab search
        labGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //search lab based on gender
                if(i > 0) {
                    getLabSearchGender = labGenderSpinner.getSelectedItem().toString();
                    Preference.setValue(getContext(), "LAB_SEARCH_CUSTOM", "true");
                    Preference.setValue(getContext(), "LAB_SEARCH_GENDER", getLabSearchGender);
                    customSearch();
                    //searchLab(getLabSearchGender, getLabSearchSpeciality);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //nearby linear layout configuration
        //String nearby = new GetNearBy().execute();
        new GetNearBy().execute();

        Preference.setValue(getContext(), "HOSPITAL_SEARCH", "");
        //hospitals
        etHospitalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preference.setValue(getContext(), "HOSPITAL_SEARCH", "true");
                customSearch();
            }
        });

        //clinics
        Preference.setValue(getContext(), "CLINIC_SEARCH", "");
        etClinicSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preference.setValue(getContext(), "CLINIC_SEARCH", "true");
                customSearch();
                //new GetClinic().execute();
            }
        });

    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    /**
     * GetJSONCountry
     * Get country json data
     */
    public class GetJSONCountry extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "country";
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(Void... params) {
            HttpGet httpGet = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {
                return mClient.execute(httpGet, responseHandler);
            } catch(IOException e) {
                e.printStackTrace();
            }
            mClient.close();
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                List<String> countryList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject country = jsonArray.getJSONObject(i);
                    spinnerArrayAdapter.add(country.getString("country_name"));
                    //Log.v("Country: ", country.getString("country_name"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GetJSONDATA
     * speciality data from the web service
     */
    private class GetJSONData extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "Speciality/index/?lang=" + LocalInformation.getLocaleLang();
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(Void... params) {
            HttpGet httpPost = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {
                return mClient.execute(httpPost, responseHandler);
            } catch (ClientProtocolException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }/* catch (JSONException e) {
                e.printStackTrace();
            }*/
            mClient.close();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //List<String> specialityList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject spec = jsonArray.getJSONObject(i);
                    specialityArrayAdapter.add(spec.getString("SPEC"));
                    //Log.v("Data: ", spec.getString("SPEC"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void expand() {
        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);
        mAnimator.start();
    }

    private void labExpand() {
        labLinearLayout.setVisibility(View.VISIBLE);
        labAnimator.start();
    }

    private void collapse() {
        int finalHeight = mLinearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private void labCollapse() {
        int finalHeight = labLinearLayout.getHeight();

        ValueAnimator mAnimator = labSlideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                labLinearLayout.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private ValueAnimator labSlideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = labLinearLayout.getLayoutParams();
                layoutParams.height = value;
                labLinearLayout.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }

    /**
     * Default near by hospitals
     */
    public class NearBy {
        NearBy(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
            linearLayout.removeAllViews();
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParam.setMarginStart(GetSearchTitleMarginStart());
            textLayoutParam.setMargins(15,10,15,10);
            TextView title = new TextView(getContext());
            title.setLayoutParams(textLayoutParam);
            title.setTextColor(Color.parseColor("#010101"));
            title.setTypeface(typeface, typeface.BOLD);
            title.setTextSize(17);
            title.setText(getResources().getString(R.string.nearby));
            linearLayout.addView(title);
            try {
                JSONObject hospitalObject = new JSONObject(result);
                JSONArray hospitalArray = hospitalObject.getJSONArray("data");
                for(int i = 0; i < hospitalArray.length(); i++) {
                    JSONObject hospital = hospitalArray.getJSONObject(i);

                    //append control
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0,0,0,0);
                    linearLayout.setLayoutParams(layoutParams);
                    View view;
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.nearby, null);

                    if (LocalInformation.getLocaleLang().equals("ar")) {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        if(hospital.getString("name_en").trim().length() == 0) {
                            textView1.setText(hospital.getString("name_en"));
                        } else {
                            textView1.setText(hospital.getString("name"));
                        }
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        if(hospital.getString("address_en").trim().length() == 0) {
                            textView2.setText(hospital.getString("address_en"));
                        } else {
                            textView2.setText(hospital.getString("address"));
                        }
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(hospital.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));

                        linearLayout.addView(view);
                    } else {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        textView1.setText(hospital.getString("name_en"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        textView2.setText(hospital.getString("address_en"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(hospital.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);
                    }

                    final String callDialer = hospital.getString("phone");

                    //phone dialer
                    ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                    imageView.setId(imageView.getId() + i);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openPhoneDialer(callDialer);
                        }
                    });

                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hospitals list
     */
    public class Hospitals {
        Hospitals(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParam.setMarginStart(GetSearchTitleMarginStart());
            textLayoutParam.setMargins(15,10,15,10);
            TextView title = new TextView(getContext());
            title.setLayoutParams(textLayoutParam);
            title.setTextColor(Color.parseColor("#010101"));
            title.setTypeface(typeface, typeface.BOLD);
            title.setTextSize(17);
            title.setText(getResources().getString(R.string.hospital));
            linearLayout.addView(title);
            try {
                JSONObject hospitalObject = new JSONObject(result);
                JSONArray hospitalArray = hospitalObject.getJSONArray("data");
                for(int i = 0; i < hospitalArray.length(); i++) {
                    JSONObject hospital = hospitalArray.getJSONObject(i);

                    //append control
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0,0,0,0);
                    linearLayout.setLayoutParams(layoutParams);
                    View view;
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.nearby, null);

                    if (LocalInformation.getLocaleLang().equals("ar")) {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        if(hospital.getString("name_en").trim().length() == 0) {
                            textView1.setText(hospital.getString("name_en"));
                        } else {
                            textView1.setText(hospital.getString("name"));
                        }
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        if(hospital.getString("address_en").trim().length() == 0) {
                            textView2.setText(hospital.getString("address_en"));
                        } else {
                            textView2.setText(hospital.getString("address"));
                        }
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(hospital.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = hospital.getString("phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });
                    } else {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        textView1.setText(hospital.getString("name_en"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        textView2.setText(hospital.getString("address_en"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(hospital.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = hospital.getString("phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });
                    }
                }
            } catch(JSONException e) {
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
            margin = 10;
        } else if(density >= 1.5 && density < 2.0) {
            margin = 16;
        } else if (density >= 2.0 && density <= 2.5) {
            margin = 26;
        } else if(density > 2.5 && density < 3.0) {
            margin = 30;
        } else if(density >= 3.0 && density < 3.5) {
            margin = 35;
        } else if(density >= 3.5 && density <= 4.0) {
            margin = 40;
        } else if(density > 4.0) {

        } else {

        }
        return margin;
    }

    /**
     * Clinic list
     */
    public class Clinics {
        Clinics(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
            linearLayout.removeAllViews();
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParam.setMarginStart(GetSearchTitleMarginStart());
            textLayoutParam.setMargins(15,10,15,10);
            TextView title = new TextView(getContext());
            title.setLayoutParams(textLayoutParam);
            title.setTextColor(Color.parseColor("#010101"));
            title.setTypeface(typeface, typeface.BOLD);
            title.setTextSize(17);
            title.setText(getResources().getString(R.string.clinics));
            linearLayout.addView(title);
            try {
                JSONObject hospitalObject = new JSONObject(result);
                JSONArray hospitalArray = hospitalObject.getJSONArray("data");
                for(int i = 0; i < hospitalArray.length(); i++) {
                    JSONObject clinic = hospitalArray.getJSONObject(i);

                    //append control
                    //LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0,0,0,0);
                    linearLayout.setLayoutParams(layoutParams);
                    View view;
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.nearby, null);

                    if (LocalInformation.getLocaleLang().equals("ar")) {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        if(clinic.getString("name_en").trim().length() == 0) {
                            textView1.setText(clinic.getString("name_en"));
                        } else {
                            textView1.setText(clinic.getString("name"));
                        }
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        if(clinic.getString("address_en").trim().length() == 0) {
                            textView2.setText(clinic.getString("address_en"));
                        } else {
                            textView2.setText(clinic.getString("address"));
                        }
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(clinic.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = clinic.getString("phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });
                    } else {
                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        textView1.setText(clinic.getString("name_en"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        textView2.setText(clinic.getString("address_en"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(clinic.getString("phone"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = clinic.getString("phone");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });
                    }

                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get hospital list from web service
     */
    public class GetHospitalList extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "work/hospital";
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
            }/* catch (JSONException e) {
                e.printStackTrace();
            }*/
            mClient.close();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new Hospitals(result);
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

    /**
     * Get clinic list from web service
     */
    public class GetClinic extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "work/clinic";
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
            }/* catch (JSONException e) {
                e.printStackTrace();
            }*/
            mClient.close();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            new Clinics(result);
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

    /**
     * Get nearby list
     */
    public class GetNearBy extends AsyncTask<Void, Void, String> {
        String URL = webServiceUrl + "work/nearby";
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
            new NearBy(result);
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

    public void searchDoctors(final String gender, final String speciality, final String location) {

        class SearchDcotors extends AsyncTask<String, Void, String> {
            String URL = AppConfig.getWebServiceUrl() + "work/searchDoctors";
            HttpClient httpClient = new DefaultHttpClient();

            @Override
            protected String doInBackground(String... params) {
                String quickGender = gender;
                String quickSpeciality = speciality;
                String quickLocation = location;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));
                nameValuePairs.add(new BasicNameValuePair("location", quickLocation));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    //Log.v("url", URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    return httpClient.execute(httpPost, responseHandler);
                    //JSONObject json = new JSONObject(responseBody);
                    //Log.v("data: ", json.toString());

                } catch(Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                new DoctorSearchResult(result);
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

        SearchDcotors searchDcotors = new SearchDcotors();
        searchDcotors.execute(gender, speciality, location);
    }

    /**
     * Doctor search result
     */
    public class DoctorSearchResult {
        DoctorSearchResult(String result) {
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParam.setMarginStart(GetSearchTitleMarginStart());
            textLayoutParam.setMargins(15,10,15,10);
            TextView title = new TextView(getContext());
            title.setLayoutParams(textLayoutParam);
            title.setTextColor(Color.parseColor("#010101"));
            title.setTypeface(typeface, typeface.BOLD);
            title.setTextSize(17);
            title.setText(getResources().getString(R.string.search_doctor_label));
            linearLayout.addView(title);

            try {
                JSONObject doctorSearchObject = new JSONObject(result);

                if(doctorSearchObject.getString("status").equals("false")) {
                    TextView textView = new TextView(getContext());
                    textView.setText("No data found");
                    textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    textView.setTextColor(Color.parseColor("#010101"));
                    textView.setTypeface(typeface);
                    textView.setTextSize(15);
                    textView.setLayoutParams(textLayoutParam);

                    linearLayout.addView(textView);
                } else {
                    JSONArray searchResult = doctorSearchObject.getJSONArray("data");
                    for(int i = 0; i < searchResult.length(); i++) {
                        JSONObject doctor = searchResult.getJSONObject(i);

                        //append control
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0,0,0,0);
                        linearLayout.setLayoutParams(layoutParams);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.nearby, null);

                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        textView1.setText(doctor.getString("first_name") + " "+ doctor.getString("last_name"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        textView2.setText(doctor.getString("address"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(doctor.getString("phone_number"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = doctor.getString("phone_number");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });


                    }
                }

            } catch(JSONException e) {
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

    /**
     * Search lab based on gender and speciality
     * @param gender
     * @param speciality
     */
    public void searchLab(final String gender, final String speciality) {

        class LabSearch extends AsyncTask<String, Void, String> {
            String URL = AppConfig.getWebServiceUrl() + "work/searchLab";
            HttpClient httpClient = new DefaultHttpClient();

            @Override
            protected String doInBackground(String... params) {
                String quickGender = gender;
                String quickSpeciality = speciality;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("gender", quickGender));
                nameValuePairs.add(new BasicNameValuePair("speciality", quickSpeciality));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    return httpClient.execute(httpPost, responseHandler);

                } catch(Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                new LabSearchResult(result);
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
    }

    public class LabSearchResult {
        LabSearchResult(String result) {
            Log.v("result", result);
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.nearby_linear_layout);
            linearLayout.removeAllViews();
            linearLayout.setGravity(Gravity.START);
            LinearLayout.LayoutParams textLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textLayoutParam.setMarginStart(GetSearchTitleMarginStart());
            textLayoutParam.setMargins(15,10,15,10);
            TextView title = new TextView(getContext());
            title.setLayoutParams(textLayoutParam);
            title.setTextColor(Color.parseColor("#010101"));
            title.setTypeface(typeface, typeface.BOLD);
            title.setTextSize(17);
            title.setText(getResources().getString(R.string.search_labs_label));
            linearLayout.addView(title);

            try {
                JSONObject doctorSearchObject = new JSONObject(result);

                if(doctorSearchObject.getString("status").equals("false")) {
                    TextView textView = new TextView(getContext());
                    textView.setText("No data found");
                    textView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                    textView.setTextColor(Color.parseColor("#010101"));
                    textView.setTypeface(typeface);
                    textView.setTextSize(15);
                    textView.setLayoutParams(textLayoutParam);

                    linearLayout.addView(textView);
                } else {
                    JSONArray searchResult = doctorSearchObject.getJSONArray("data");
                    for(int i = 0; i < searchResult.length(); i++) {
                        JSONObject doctor = searchResult.getJSONObject(i);

                        //append control
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0,0,0,0);
                        linearLayout.setLayoutParams(layoutParams);
                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.nearby, null);

                        //clinic name
                        TextView textView1 = (TextView) view.findViewById(R.id.title_work_place);
                        textView1.setText(doctor.getString("name_en"));
                        textView1.setTypeface(typeface, typeface.BOLD);
                        textView1.setTextColor(Color.parseColor("#010101"));

                        //clinic address
                        TextView textView2 = (TextView) view.findViewById(R.id.work_place_address);
                        textView2.setText(doctor.getString("address_en"));
                        textView2.setTypeface(typeface);
                        textView2.setTextColor(Color.parseColor("#010101"));

                        //mobile
                        TextView textView3 = (TextView) view.findViewById(R.id.work_place_phone);
                        textView3.setText(doctor.getString("phone_number"));
                        textView3.setTypeface(typeface);
                        textView3.setTextColor(Color.parseColor("#010101"));

                        //total reviews
                        TextView textView4 = (TextView) view.findViewById(R.id.total_reviews);
                        textView4.setText(getResources().getString(R.string.total_reviews));
                        textView4.setTypeface(typeface);
                        textView4.setTextColor(Color.parseColor("#010101"));
                        linearLayout.addView(view);

                        final String callDialer = doctor.getString("phone_number");

                        //phone dialer
                        ImageView imageView = (ImageView) view.findViewById(R.id.phone_dialer);
                        imageView.setId(imageView.getId() + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneDialer(callDialer);
                            }
                        });
                    }
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void customSearch() {
        BaseActivity baseActivity = new BaseActivity();
        FragmentManager mFragmentManager;
        mFragmentManager = getActivity().getSupportFragmentManager();
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        baseActivity.tabFragment.tabLayout.getTabAt(1).select();
    }
}