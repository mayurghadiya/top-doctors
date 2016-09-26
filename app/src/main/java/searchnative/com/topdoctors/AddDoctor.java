package searchnative.com.topdoctors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 19/9/16.
 */
public class AddDoctor extends Fragment {

    Typeface typeface;
    TextView addDoctorTitle, addDoctorUploadNote, addDoctorTermsNote, addDoctorTermsStart, addDoctorTermsEnd;
    EditText addDoctorName, addDoctorClinicName, addDoctorEmail, addDoctorMobileNumber, addDoctorClinicAddress;
    Spinner addDoctorLocation, addDoctorSpeciality, addDoctorGender;
    String[] genderArray;
    static String webServiceUrl = AppConfig.getWebServiceUrl();
    ArrayAdapter<String> locationArrayAdapter, specialityArrayAdapter;
    List<String> specialityList, locationList;
    Button submitButton;

    String getName, getClinicName, getEmail, getMobile, getLocation, getCLinicAddress, getSpeciality, getGender;

    private ImageView imageView;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL = webServiceUrl + "doctor/add";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "userfile";
    private String doctorId;

    /**
     * Create view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_doctor, container, false);

        return view;
    }

    /**
     * OnActivity created
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set font style
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");

        addDoctorTitle = (TextView) getView().findViewById(R.id.add_doctor_title);
        addDoctorName = (EditText) getView().findViewById(R.id.add_doctor_name);
        addDoctorClinicName = (EditText) getView().findViewById(R.id.add_doctor_clinic_name);
        addDoctorEmail = (EditText) getView().findViewById(R.id.add_doctor_email);
        addDoctorMobileNumber = (EditText) getView().findViewById(R.id.add_doctor_mobile_number);
        addDoctorClinicAddress = (EditText) getView().findViewById(R.id.add_doctor_clinic_address);
        addDoctorLocation = (Spinner) getView().findViewById(R.id.add_doctor_location);
        addDoctorSpeciality = (Spinner) getView().findViewById(R.id.add_doctor_speciality);
        addDoctorGender = (Spinner) getView().findViewById(R.id.add_doctor_gender);
        addDoctorUploadNote = (TextView) getView().findViewById(R.id.add_doctor_upload_note);
        submitButton = (Button) getView().findViewById(R.id.add_doctor_submit);
        addDoctorTermsNote = (TextView) getView().findViewById(R.id.add_doctor_terms_note);
        addDoctorTermsStart = (TextView) getView().findViewById(R.id.add_doctor_terms_start);
        addDoctorTermsEnd = (TextView) getView().findViewById(R.id.add_doctor_terms_end);

        addDoctorTitle.setTypeface(typeface);
        addDoctorName.setTypeface(typeface);
        addDoctorClinicName.setTypeface(typeface);
        addDoctorEmail.setTypeface(typeface);
        addDoctorMobileNumber.setTypeface(typeface);
        addDoctorClinicAddress.setTypeface(typeface);
        addDoctorUploadNote.setTypeface(typeface);
        submitButton.setTypeface(typeface);
        addDoctorTermsNote.setTypeface(typeface);
        addDoctorTermsStart.setTypeface(typeface);
        addDoctorTermsEnd.setTypeface(typeface);

        imageView  = (ImageView) getView().findViewById(R.id.imageView);

        //load all gender, location and speciality spinner
        loadGenderSpinner();

        specialityList = new ArrayList<String>();
        specialityList.add(getResources().getString(R.string.speciality));
        specialityArrayAdapter = new ArrayAdapter<String>(getContext(),
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
                ((TextView) v).setTextSize(18);

                return v;
            }
        };
        specialityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loadSpecialitySpinner();
        specialityArrayAdapter.setNotifyOnChange(true);
        addDoctorSpeciality.setAdapter(specialityArrayAdapter);

        addDoctorSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    getSpeciality = addDoctorSpeciality.getSelectedItem().toString();
                    //Toast.makeText(getContext(), getSpeciality, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationList = new ArrayList<String>();
        locationList.add(getResources().getString(R.string.location));
        locationArrayAdapter = new ArrayAdapter<String>(getContext(),
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
                ((TextView) v).setTextSize(18);
                return v;
            }
        };
        loadLocationSpinner();
        locationArrayAdapter.setNotifyOnChange(true);
        addDoctorLocation.setAdapter(locationArrayAdapter);

        addDoctorLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    getLocation = addDoctorLocation.getSelectedItem().toString();
                    //Toast.makeText(getContext(), getLocation, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grabEnteredData();
                if(dataValidation()) {
                    uploadImage();
                }

            }
        });

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.upload_photo_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getActivity().getWindow().setLayout((int) (width * 1.0), (int) (height * 1.0));
    }

    /**
     * Grab entered data
     */
    public void grabEnteredData() {
        getName = addDoctorName.getText().toString();
        getClinicName = addDoctorClinicName.getText().toString();
        getEmail = addDoctorEmail.getText().toString();
        getMobile = addDoctorMobileNumber.getText().toString();
        getCLinicAddress = addDoctorClinicAddress.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean dataValidation() {
        boolean isValid = true;

        if(addDoctorName.getText().toString().trim().length() == 0) {
            addDoctorName.setError("Name is required");
            isValid = false;
        }

        if(addDoctorClinicName.getText().toString().trim().length() == 0) {
            addDoctorClinicName.setError("Clinic name is required");
            isValid = false;
        }

        if(addDoctorEmail.getText().toString().trim().length() == 0) {
            addDoctorEmail.setError("Email is required");
            isValid = false;
        }

        //check for valid email
        Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
        Matcher matcher1 = pattern1.matcher(addDoctorEmail.getText().toString());
        if (!matcher1.matches()) {
            addDoctorEmail.setError(getResources().getString(R.string.enter_valid_password));
            isValid = false;
        }

        if(addDoctorMobileNumber.getText().toString().trim().length() == 0) {
            addDoctorMobileNumber.setError("Mobile number is required");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Image to string
     * @param bmp
     * @return string
     */
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * Show file chooser
     */
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Upload Image
     */
    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
               //String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("name", getName);
                params.put("clinic_name", getClinicName);
                params.put("email", getEmail);
                params.put("mobile", getMobile);
                params.put("location", getLocation);
                params.put("address", getCLinicAddress);
                params.put("speciality", getSpeciality);
                params.put("gender", getGender);
                params.put("lang", LocalInformation.getLocaleLang());
                params.put(KEY_IMAGE, image);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    /**
     * Load gender spinner with data
     */
    public void loadGenderSpinner() {
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
                ((TextView) v).setTextSize(18);

                return v;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addDoctorGender.setAdapter(arrayAdapter);

        addDoctorGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getGender = addDoctorGender.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Load speciality spinner with data
     */
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new SpecialityData().execute();
    }

    /**
     * Load location spinner with data
     */
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
                    List<String> countryList = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject country = jsonArray.getJSONObject(i);
                        locationArrayAdapter.add(country.getString("country_name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        new LocationData().execute();
    }
}
