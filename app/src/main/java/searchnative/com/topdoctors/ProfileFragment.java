package searchnative.com.topdoctors;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private LinearLayout badgesLayout;
    private Typeface typeface;
    private TextView titleEditProfile, profileName, profileEmail, reviewCount, photosCount, badgesCount, titleReview,
            titlePhotos, titleBadges, location, userLocation, mobileNumber, commentUserName, userNameComment, commentTime,
            usersCount, starCount, userPhotosCount, userMobileNumber;
    private ProgressDialog mProgressDialog;
    private ImageView editProfileName, editEmail, editUserLocation, editMobileNumber;
    private String getFirstName, getLastName, getEmail, getLocation, getMobile;
    private Spinner locationSpinner;
    private List countryList;
    private ArrayAdapter countryArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoMedium.otf");

        titleEditProfile = (TextView) getView().findViewById(R.id.title_edit_profile);
        profileName = (TextView) getView().findViewById(R.id.profile_name);
        profileEmail = (TextView) getView().findViewById(R.id.profile_email);
        reviewCount = (TextView) getView().findViewById(R.id.review_count);
        photosCount = (TextView) getView().findViewById(R.id.photos_count);
        badgesCount = (TextView) getView().findViewById(R.id.badges_count);
        titleReview = (TextView) getView().findViewById(R.id.title_review);
        titlePhotos = (TextView) getView().findViewById(R.id.title_photos);
        titleBadges = (TextView) getView().findViewById(R.id.title_badges);
        location = (TextView) getView().findViewById(R.id.location);
        userLocation = (TextView) getView().findViewById(R.id.user_location);
        mobileNumber = (TextView) getView().findViewById(R.id.mobile_number);
        //commentUserName = (TextView) getView().findViewById(R.id.comment_user_name);
        //userNameComment = (TextView) getView().findViewById(R.id.user_name_comment);
        //commentTime = (TextView) getView().findViewById(R.id.comment_time);
        //usersCount = (TextView) getView().findViewById(R.id.users_count);
        //starCount = (TextView) getView().findViewById(R.id.star_count);
        //userPhotosCount = (TextView) getView().findViewById(R.id.user_photos_count);
        userMobileNumber = (TextView) getView().findViewById(R.id.user_mobile_number);
        editProfileName = (ImageView) getView().findViewById(R.id.profile_name_edit_image);
        editEmail = (ImageView) getView().findViewById(R.id.update_email_user);
        editUserLocation = (ImageView) getView().findViewById(R.id.editUserLocation);
        editMobileNumber = (ImageView) getView().findViewById(R.id.editMobileNumber);

        titleEditProfile.setTypeface(typeface, typeface.BOLD);
        profileName.setTypeface(typeface, typeface.BOLD);
        profileEmail.setTypeface(typeface);
        reviewCount.setTypeface(typeface);
        photosCount.setTypeface(typeface);
        badgesCount.setTypeface(typeface);
        titleReview.setTypeface(typeface);
        titlePhotos.setTypeface(typeface);
        titleBadges.setTypeface(typeface);
        location.setTypeface(typeface, typeface.BOLD);
        userLocation.setTypeface(typeface);
        mobileNumber.setTypeface(typeface, typeface.BOLD);
        //commentUserName.setTypeface(typeface);
        //userNameComment.setTypeface(typeface);
        //commentTime.setTypeface(typeface);
        //usersCount.setTypeface(typeface);
        //starCount.setTypeface(typeface);
        //userPhotosCount.setTypeface(typeface);
        userMobileNumber.setTypeface(typeface);

        badgesLayout = (LinearLayout) getView().findViewById(R.id.badges_layout);
        badgesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BadgesActivity.class));
            }
        });

        //edit profile name pop up
        editProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setView(promptView);

                final TextView titleText = (TextView) promptView.findViewById(R.id.textView1);
                titleText.setTypeface(typeface);
                titleText.setTextColor(getResources().getColor(R.color.black));

                final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);
                userInput.setText(getFirstName);
                userInput.setTextColor(getResources().getColor(R.color.black));
                userInput.setTypeface(typeface);

                final EditText lastName = (EditText) promptView.findViewById(R.id.editTextLastName);
                lastName.setText(getLastName);
                lastName.setTypeface(typeface);

                alertDialog
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getFirstName = userInput.getText().toString();
                                        getLastName = lastName.getText().toString();
                                        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
                                        Log.v("User ID", userId);
                                        updateProfileName(userId, getFirstName, getLastName);
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                userInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(userInput.getText().toString().trim().length() == 0 ||
                                lastName.getText().toString().trim().length() == 0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                lastName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(lastName.getText().toString().trim().length() == 0 ||
                                userInput.getText().toString().trim().length() == 0 ) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        //edit email
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.update_email_prompts, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(promptView);

                final TextView titleText = (TextView) promptView.findViewById(R.id.textView1);
                titleText.setTypeface(typeface);
                titleText.setTextColor(getResources().getColor(R.color.black));

                final EditText emailEditText = (EditText) promptView.findViewById(R.id.editTextEmail);
                emailEditText.setText(getEmail);
                emailEditText.setTextColor(getResources().getColor(R.color.black));
                emailEditText.setTypeface(typeface);

                builder
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getEmail = emailEditText.getText().toString();
                                        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
                                        updateEmail(userId, getEmail);
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                emailEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(emailEditText.getText().toString().trim().length() == 0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            //check for valid email
                            Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
                            Matcher matcher1 = pattern1.matcher(emailEditText.getText().toString());
                            if (!matcher1.matches()) {
                                emailEditText.setError(getResources().getString(R.string.valid_email_error));
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            } else {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        //edit location
        editUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.update_location_prompt, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(promptView);

                final TextView titleText = (TextView) promptView.findViewById(R.id.textView1);
                titleText.setTypeface(typeface);
                titleText.setTextColor(getResources().getColor(R.color.black));

                locationSpinner = (Spinner) promptView.findViewById(R.id.userLocation);
                countryList = new ArrayList();
                countryList.add(getResources().getString(R.string.country));
                countryList.addAll(CountryData.getmInstance().countryList);
                countryArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                        countryList) {
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
                            textView.setTextColor(getResources().getColor(R.color.textHighlightColor));
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
                        if(position == 0) {
                            ((TextView) view).setTextColor(getResources().getColor(R.color.textHighlightColor));
                            ((TextView) view).setTextSize(16);
                        } else {
                            ((TextView) view).setTextColor(getResources().getColor(R.color.black));
                            ((TextView) view).setTextSize(16);
                        }

                        return view;
                    }
                };
                countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                countryArrayAdapter.setNotifyOnChange(true);
                locationSpinner.setAdapter(countryArrayAdapter);

                for(int i = 0; i < countryArrayAdapter.getCount(); i++) {
                    if(getLocation.trim().equals(countryArrayAdapter.getItem(i).toString())) {
                        locationSpinner.setSelection(i);
                    }
                }

                builder
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
                                        getLocation = locationSpinner.getSelectedItem().toString();
                                        updateCountry(userId, getLocation);
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i > 0) {
                            locationSpinner.setSelection(i);
                            //userLocation.setText(getLocation);
                            //Log.v("data", locationSpinner.getSelectedItem().toString());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //update mobile
        editMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.update_mobile_prompt, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(promptView);

                final TextView titleText = (TextView) promptView.findViewById(R.id.textView1);
                titleText.setTypeface(typeface);
                titleText.setTextColor(getResources().getColor(R.color.black));

                final EditText editUserMobile = (EditText) promptView.findViewById(R.id.editTextMobile);
                editUserMobile.setTypeface(typeface);
                editUserMobile.setText(getMobile);
                editUserMobile.setTextColor(getResources().getColor(R.color.black));

                builder
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getMobile = editUserMobile.getText().toString();
                                        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
                                        updateMobile(userId, getMobile);
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                editUserMobile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(editUserMobile.getText().toString().trim().length() < 10) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        });
    }

    public void updateEmail(final String userId, final String email) {

        class UpdateEmail extends AsyncTask<String, Void, String> {
            final String URL = AppConfig.getWebServiceUrl() + "profile/userProfileUpdateEmail";
            String quickUserId = userId;
            String quickEmail = email;
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("email", quickEmail));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    profileEmail.setText(getEmail);
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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

        UpdateEmail updateEmail = new UpdateEmail();
        updateEmail.execute(userId, email);

    }

    public void updateMobile(final String userId, final String mobile) {

        class UpdateMobile extends AsyncTask<String, Void, String> {

            final String URL = AppConfig.getWebServiceUrl() + "profile/userProfileUpdateMobile";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickUserId = Preference.getValue(getContext(), "LOGIN_ID", "");
            String quickMobile = mobile;

            @Override
            protected String doInBackground(String... params) {
                Log.v("Login Id", quickUserId);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("mobile", quickMobile));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.v("User Profile", result);
                //new RenderUserProfileDetails(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    userMobileNumber.setText(getMobile);
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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

        UpdateMobile updateMobile = new UpdateMobile();
        updateMobile.execute(userId, mobile);
    }

    public void updateCountry(final String userId, final String country) {

        class UpdateCountry extends AsyncTask<String, Void, String> {
            final String URL = AppConfig.getWebServiceUrl() + "profile/userProfileUpdateLocation";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            String quickUserId = userId;
            String quickCountry = country;

            @Override
            protected String doInBackground(String... params) {
                Log.v("Login Id", quickUserId);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("location", quickCountry));

                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.v("User Profile", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    userLocation.setText(getLocation);
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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

        UpdateCountry updateCountry = new UpdateCountry();
        updateCountry.execute(userId, country);
    }

    class UserProfileDetails extends AsyncTask<String, Void, String> {

        final String URL = AppConfig.getWebServiceUrl() + "profile/userProfile";
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        String quickUserId = Preference.getValue(getContext(), "LOGIN_ID", "");

        @Override
        protected String doInBackground(String... params) {
            Log.v("Login Id", quickUserId);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));

            try {
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                return mClient.execute(httpPost, responseHandler);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mClient.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("User Profile", result);
            new RenderUserProfileDetails(result);
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

    class RenderUserProfileDetails {
        RenderUserProfileDetails(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //user name
                String userName = jsonArray.getJSONObject(0).getString("Name");
                String email = jsonArray.getJSONObject(0).getString("email");
                String location = jsonArray.getJSONObject(0).getString("location");
                String mobile = jsonArray.getJSONObject(0).getString("phone");

                getFirstName = jsonArray.getJSONObject(0).getString("firstName");
                getLastName = jsonArray.getJSONObject(0).getString("lastName");
                getEmail = email;
                getLocation = location;
                getMobile = mobile;

                //set values
                titleEditProfile.setText(userName);
                profileName.setText(userName);
                profileEmail.setText(email);
                userMobileNumber.setText(mobile);
                userLocation.setText(location);
                //userNameComment.setText(userName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProfileName(final String userId, final String firstName, final String lastName) {

        class UpdateProfileName extends AsyncTask<String, Void, String> {
            final String URL = AppConfig.getWebServiceUrl() + "profile/userProfileUpdateFullName";
            String quickFirstName = firstName;
            String quickLastName = lastName;
            String quickUserId = userId;

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                nameValuePairs.add(new BasicNameValuePair("firstName", quickFirstName));
                nameValuePairs.add(new BasicNameValuePair("lastName", quickLastName));
                Log.v("Nam value pair", nameValuePairs.toString());
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    titleEditProfile.setText(getFirstName + " " + getLastName);
                    profileName.setText(getFirstName + " " + getLastName);
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
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

        UpdateProfileName updateProfileName = new UpdateProfileName();
        updateProfileName.execute(userId, firstName, lastName);
    }

    public void userAllReviews(final String userId) {

        class UserAllReviews extends AsyncTask<String, Void, String> {

            final String URL = AppConfig.getWebServiceUrl() + "review/UserByDoctorReviews";
            String quickUserId = userId;

            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userId", quickUserId));
                Log.v("Nam value pair", nameValuePairs.toString());
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                new RenderAllUserReview(result);
                /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }*/
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //mProgressDialog = new ProgressDialog(getContext());
                //mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                //mProgressDialog.setCancelable(false);
                //mProgressDialog.show();
            }
        }

        UserAllReviews userAllReviews = new UserAllReviews();
        userAllReviews.execute(userId);
    }

    @Override
    public void onResume(){
        super.onResume();
        new UserProfileDetails().execute();
        //load all review
        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
        userAllReviews(userId);
    }

    class RenderAllUserReview {
        RenderAllUserReview(String result) {

            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.userAllReviewList);
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
                    JSONArray reviewResult = jsonObject.getJSONArray("result");

                    for (int i = 0; i < reviewResult.length(); i++) {
                        final JSONObject reviewData = reviewResult.getJSONObject(i);

                        View view;
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.user_all_reviews, null);

                        TextView textView = (TextView) view.findViewById(R.id.comment_user_name);
                        textView.setText(reviewData.getString("doctorName"));
                        textView.setTypeface(typeface);

                        TextView textView1 = (TextView) view.findViewById(R.id.user_name_comment);
                        textView1.setText(getFirstName + " " + getLastName);
                        textView1.setTypeface(typeface);

                        TextView textView2 = (TextView) view.findViewById(R.id.comment_time);
                        textView2.setTypeface(typeface);
                        textView2.setText(reviewData.getString("commentTime"));

                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.userRating);
                        ratingBar.setRating(Float.valueOf(reviewData.getString("avgReview")));

                        ImageView editUserReview = (ImageView) view.findViewById(R.id.userReview);
                        editUserReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    editUserReview(reviewData.getString("doctorId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        linearLayout.addView(view);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("Review result", result);
        }
    }

    public void editUserReview(final String doctorId) {
        final String userId = Preference.getValue(getContext(), "LOGIN_ID", "");
        Log.v("User Id", userId);
        Log.v("Doctor Id", doctorId);
    }
}