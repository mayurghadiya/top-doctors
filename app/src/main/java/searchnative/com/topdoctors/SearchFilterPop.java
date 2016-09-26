package searchnative.com.topdoctors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 21/9/16.
 */
public class SearchFilterPop extends AppCompatActivity {

    Spinner locationSpinner, specialitySpinner, spinnerGender;
    String webServiceUrl = AppConfig.getWebServiceUrl();
    ArrayAdapter<String> locationArrayAdapter, specialityArrayAdapter;
    List<String> locationList, specialityList;
    Typeface typeface;
    String[] genderArray;
    EditText editText;
    Button searchButton;
    TextView titleText;
    String getLocation, getSpeciality, getGender, getName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.search_filter);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        typeface = Typeface.createFromAsset(this.getAssets(), "fonts/ExoMedium.otf");

        locationSpinner = (Spinner) findViewById(R.id.popup_spinner_location);
        specialitySpinner = (Spinner) findViewById(R.id.popup_spinner_speciality);
        spinnerGender = (Spinner) findViewById(R.id.popup_spinner_gender);
        searchButton = (Button) findViewById(R.id.popup_search_button);
        titleText = (TextView) findViewById(R.id.textView2);

        locationList = new ArrayList<String>();
        locationList.add(getResources().getString(R.string.location));
        locationArrayAdapter = new ArrayAdapter<String>(SearchFilterPop.this,
                android.R.layout.simple_spinner_dropdown_item, locationList) {
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
                    textView.setTextColor(Color.BLACK);
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
                ((TextView) v).setTextSize(18);
                return v;
            }
        };
        loadLocationSpinner();
        locationArrayAdapter.setNotifyOnChange(true);
        locationSpinner.setAdapter(locationArrayAdapter);

        specialityList = new ArrayList<String>();
        specialityList.add(getResources().getString(R.string.speciality));
        specialityArrayAdapter = new ArrayAdapter<String>(SearchFilterPop.this,
                android.R.layout.simple_spinner_dropdown_item, specialityList) {
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
                    textView.setTextColor(Color.BLACK);
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
                ((TextView) v).setTextSize(18);
                return v;
            }
        };
        specialityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loadSpecialitySpinner();
        specialityArrayAdapter.setNotifyOnChange(true);
        specialitySpinner.setAdapter(specialityArrayAdapter);

        loadGenderSpinner();

        editText = (EditText) findViewById(R.id.popup_edittext_name);
        editText.setTypeface(typeface);
        searchButton.setTypeface(typeface);
        titleText.setTypeface(typeface);

        //set filter values
        setPreviousFilterData();

        getLocation();
        getSpeciality();
        getGender();
        //getName();

        Preference.setValue(SearchFilterPop.this, "IS_SEARCH", "");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getName = editText.getText().toString();
                Preference.setValue(SearchFilterPop.this, "FILTER_NAME", getName);
                Preference.setValue(SearchFilterPop.this, "IS_SEARCH", "1");
                finish();
            }
        });
        //Preference.setValue(SearchFilterPop.this, "IS_SEARCH", "1");

        float density = getResources().getDisplayMetrics().density;
        double heightDensity = .41;
        //Toast.makeText(SearchFilterPop.this, "density: " + density, Toast.LENGTH_LONG).show();
        if(density >= 0.75 && density < 1.0) {
            heightDensity = .51;
        } else if(density >= 1.0 && density <= 1.5) {
            heightDensity = .49;
        } else if(density > 1.5 && density < 2.0) {
            heightDensity = .46;
        } else if (density >= 2.0 && density <= 2.5) {
            heightDensity = 0.45;
        } else if(density > 2.5 && density < 3.0) {
            heightDensity = 0.41;
        } else if(density >= 3.0 && density < 3.5) {
            heightDensity = .39;
        } else if(density >= 3.5 && density <= 4.0) {
            heightDensity = .37;
        } else if(density > 4.0) {
            heightDensity = .35;
        } else {
        }

        getWindow().setLayout((int) (width * .89), (int) (height * heightDensity));


    }

    public void setPreviousFilterData() {
        String name = Preference.getValue(SearchFilterPop.this, "FILTER_NAME", "");

        if(name.trim().length() > 0) {
            editText.setText(name);
        }
    }

    public void loadLocationSpinner() {

        class LocationData extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "country";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                HttpPost httpPost = new HttpPost(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    return mClient.execute(httpPost, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                mClient.close();

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject country = jsonArray.getJSONObject(i);
                        locationArrayAdapter.add(country.getString("country_name"));
                    }

                    String prevLocation = Preference.getValue(SearchFilterPop.this, "FILTER_LOCATION", "");;

                    for(int i = 0; i < locationArrayAdapter.getCount(); i++) {
                        if(prevLocation.trim().equals(locationArrayAdapter.getItem(i).toString())) {
                            locationSpinner.setSelection(i);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        new LocationData().execute();
    }

    public void loadSpecialitySpinner() {

        class SpecialityData extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "Speciality/index/?lang=" + LocalInformation.getLocaleLang();
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                HttpPost httpPost = new HttpPost(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    return mClient.execute(httpPost, responseHandler);
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject spec = jsonArray.getJSONObject(i);
                        specialityArrayAdapter.add(spec.getString("SPEC"));
                    }

                    String prevSpeciality = Preference.getValue(SearchFilterPop.this, "FILTER_SPECIALITY", "");;

                    for(int i = 0; i < specialityArrayAdapter.getCount(); i++) {
                        if(prevSpeciality.trim().equals(specialityArrayAdapter.getItem(i).toString())) {
                            specialitySpinner.setSelection(i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new SpecialityData().execute();
    }

    public void loadGenderSpinner() {
        genderArray = getResources().getStringArray(R.array.gender);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchFilterPop.this,
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
                    textView.setTextColor(Color.BLACK);
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
                ((TextView) v).setTextSize(18);

                return v;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapter);

        String prevGender = Preference.getValue(SearchFilterPop.this, "FILTER_GENDER", "");;

        for(int i = 0; i < arrayAdapter.getCount(); i++) {
            if(prevGender.trim().equals(arrayAdapter.getItem(i).toString())) {
                spinnerGender.setSelection(i);
            }
        }
    }

    public void getLocation() {
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getLocation = locationSpinner.getSelectedItem().toString();
                    Preference.setValue(SearchFilterPop.this, "FILTER_LOCATION", getLocation);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getSpeciality() {
        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getSpeciality = specialitySpinner.getSelectedItem().toString();
                    Preference.setValue(SearchFilterPop.this, "FILTER_SPECIALITY", getSpeciality);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getGender() {
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getGender = spinnerGender.getSelectedItem().toString();
                    Preference.setValue(SearchFilterPop.this, "FILTER_GENDER", getGender);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
