package searchnative.com.topdoctors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddClinicActivity extends AppCompatActivity {

    private ImageView closeActivity;
    private TextView addClinicText;
    private EditText addClinicName, addClinicEmail, addClinicMobileNumber, addClinicAddress;
    private Typeface typeface;
    private Button submitButton;
    private Spinner addClinicLocation, addClinicSpeciality, clinicDoctorSpinner;
    private List locationList, specialityList, specialityIcon, clinicDoctor, clinicDoctorIds;
    private ArrayAdapter locationArrayAdapter, doctorArrayAdapter;
    private ImageView imageView;
    private LinearLayout uploadPhotoLayout;
    private String webServiceUrl = AppConfig.getWebServiceUrl();
    private String UPLOAD_URL = webServiceUrl + "work/workAdd";
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_IMAGE = "image";
    private String getName, getAddress, getPhone, getEmail, getLocation = "", getSpeciality = "", getDoctor, getGetDoctorId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_clinic);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");

        addClinicText = (TextView) findViewById(R.id.add_clinic_text);
        addClinicName = (EditText) findViewById(R.id.add_clinic_name);
        addClinicEmail = (EditText) findViewById(R.id.add_clinic_email);
        addClinicMobileNumber = (EditText) findViewById(R.id.add_clinic_mpbile_number);
        addClinicAddress = (EditText) findViewById(R.id.add_clinic_address);
        submitButton = (Button) findViewById(R.id.add_clinic_submit);
        addClinicLocation = (Spinner) findViewById(R.id.add_clinic_location);
        addClinicSpeciality = (Spinner) findViewById(R.id.add_clinic_speciality);
        imageView = (ImageView) findViewById(R.id.imageView);
        uploadPhotoLayout = (LinearLayout) findViewById(R.id.upload_photo_layout);
        clinicDoctorSpinner = (Spinner) findViewById(R.id.clinic_doctor);

        addClinicText.setTypeface(typeface);
        addClinicName.setTypeface(typeface);
        addClinicEmail.setTypeface(typeface);
        addClinicMobileNumber.setTypeface(typeface);
        addClinicAddress.setTypeface(typeface);
        submitButton.setTypeface(typeface);

        //speciality spinner
        specialityList = new ArrayList();
        specialityIcon = new ArrayList();
        specialityList.add(getResources().getString(R.string.speciality));
        specialityList.addAll(SpecialityData.getmInstance().specialityList);
        specialityIcon.addAll(SpecialityData.getmInstance().specialityListIcon);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), specialityIcon, specialityList);
        addClinicSpeciality.setAdapter(customAdapter);
        addClinicSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getSpeciality = SpecialityData.getmInstance().specialityList.get(i - 1).toString();
                    Log.v("speciality", getSpeciality);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //location spinner
        locationList = new ArrayList();
        locationList.add(getResources().getString(R.string.location));
        locationList.addAll(LocationData.getmInstance().locationList);
        locationArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationList) {
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
                    textView.setTextColor(getResources().getColor(R.color.textHighlightColor));
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == 0) {
                    ((TextView) v).setTextColor(getResources().getColor(R.color.textHighlightColor));
                } else {
                    ((TextView) v).setTextColor(getResources().getColor(R.color.black));
                }
                ((TextView) v).setTypeface(typeface);
                int dp = (int) (getResources().getDimension(R.dimen.add_doctor_input_font) / getResources().getDisplayMetrics().density);
                ((TextView) v).setTextSize(dp);
                return v;
            }
        };
        addClinicLocation.setAdapter(locationArrayAdapter);

        addClinicLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0) {
                    getLocation = LocationData.getmInstance().locationList.get(i - 1).toString();
                    Log.v("location", getLocation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //close activity
        closeActivity = (ImageView) findViewById(R.id.close_activity);
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //show file chooser
        uploadPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        //submit clinic
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    assignVariable();
                    uploadImage();
                }

            }
        });

        //doctor spinner
        clinicDoctor = new ArrayList();
        clinicDoctorIds = new ArrayList();
        clinicDoctor.add(getResources().getString(R.string.doctor));
        clinicDoctor.addAll(DoctorList.getmInstance().doctorList);
        clinicDoctorIds.addAll(DoctorList.getmInstance().doctorIds);
        doctorArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clinicDoctor) {
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
                    textView.setTextColor(getResources().getColor(R.color.textHighlightColor));
                    v = textView;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                return v;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == 0) {
                    ((TextView) v).setTextColor(getResources().getColor(R.color.textHighlightColor));
                } else {
                    ((TextView) v).setTextColor(getResources().getColor(R.color.black));
                }
                ((TextView) v).setTypeface(typeface);
                int dp = (int) (getResources().getDimension(R.dimen.add_doctor_input_font) / getResources().getDisplayMetrics().density);
                ((TextView) v).setTextSize(dp);
                return v;
            }
        };
        clinicDoctorSpinner.setAdapter(doctorArrayAdapter);

        clinicDoctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    getDoctor = DoctorList.getmInstance().doctorList.get(i - 1).toString();
                    getGetDoctorId = DoctorList.getmInstance().doctorIds.get(i).toString();
                    Log.v("Doctor iD", getGetDoctorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void assignVariable() {
        getName = addClinicName.getText().toString();
        getAddress = addClinicAddress.getText().toString();
        getPhone = addClinicMobileNumber.getText().toString();
        getEmail = addClinicEmail.getText().toString();
        getAddress = addClinicAddress.getText().toString();
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
     * Upload Image
     */
    private void uploadImage() {
        //Showing the progress dialog
        if(getLocation.isEmpty()) {
            Toast.makeText(AddClinicActivity.this, getResources().getString(R.string.please_select_location), Toast.LENGTH_LONG).show();
        } else if(getSpeciality.isEmpty()) {
            Toast.makeText(AddClinicActivity.this, getResources().getString(R.string.please_select_speciality), Toast.LENGTH_LONG).show();
        } else if(getGetDoctorId.isEmpty()) {
            Toast.makeText(AddClinicActivity.this, getResources().getString(R.string.please_select_doctor), Toast.LENGTH_LONG).show();
        }  else if (imageView.getDrawable() == null) {
            //Log.v("Image data: ", "Image not selected");
            Toast.makeText(AddClinicActivity.this, getResources().getString(R.string.please_upload_syndicate_id), Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog loading = ProgressDialog.show(this,
                    getResources().getString(R.string.uploading_message),
                    getResources().getString(R.string.please_wait_message), false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            loading.dismiss();
                            String message = "";
                            try {
                                JSONObject responseMessage = new JSONObject(s);
                                message = responseMessage.getString("message");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Showing toast message of the response
                            Toast.makeText(AddClinicActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            loading.dismiss();

                            //Showing toast
                            Toast.makeText(AddClinicActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    String image = getStringImage(bitmap);

                    //Getting Image Name
                    //String name = editTextName.getText().toString().trim();

                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put("name", getName);
                    params.put("address", getAddress);
                    params.put("phone", getPhone);
                    params.put("email", getEmail);
                    params.put("location", getLocation);
                    params.put("speciality", getSpeciality);
                    params.put("doctor", getGetDoctorId);
                    params.put("lang", LocalInformation.getLocaleLang());
                    params.put(KEY_IMAGE, image);
                    params.put("workType", "Clinic");

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }
    }

    public boolean validateData() {
        Boolean isValidate = true;

        if(addClinicName.getText().toString().trim().length() == 0) {
            addClinicName.setError(getResources().getString(R.string.clinic_name_is_required));
            isValidate = false;
        }

        if(addClinicEmail.getText().toString().trim().length() == 0) {
            addClinicEmail.setError(getResources().getString(R.string.email_error));
            isValidate = false;
        }

        //check for valid email
        Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
        Matcher matcher1 = pattern1.matcher(addClinicEmail.getText().toString());
        if (!matcher1.matches()) {
            addClinicEmail.setError(getResources().getString(R.string.valid_email_error));
            isValidate = false;
        }

        if(addClinicMobileNumber.getText().toString().trim().length() == 0) {
            addClinicMobileNumber.setError(getResources().getString(R.string.mobile_error));
            isValidate = false;
        }

        return isValidate;
    }
}
