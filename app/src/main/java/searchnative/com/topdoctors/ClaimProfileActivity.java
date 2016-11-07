package searchnative.com.topdoctors;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClaimProfileActivity extends AppCompatActivity {

    ImageView closeButton;
    TextView claimProfileMessage, claimProfileContactMessage, claimProfileTitle, uploadPhotoText,
                claimProfileNote, claimProfileNoteStart, claimProfileNoteEnd;
    Typeface typeface;
    EditText email, mobileNumber, clinicNumber;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_claim_profile);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");

        closeButton = (ImageView) findViewById(R.id.close_claim_profile);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        claimProfileTitle = (TextView) findViewById(R.id.claim_profile_title);
        claimProfileTitle.setTypeface(typeface);

        Intent sender = getIntent();
        Bundle extras = sender.getExtras();
        claimProfileMessage = (TextView) findViewById(R.id.profile_name_claim_message);
        claimProfileMessage.setText(getResources().getString(R.string.are_you).toUpperCase() + "\n" + extras.getString("name").toUpperCase() + "?");
        claimProfileMessage.setTypeface(typeface);

        claimProfileContactMessage = (TextView) findViewById(R.id.fill_form_message);
        claimProfileContactMessage.setText(getResources().getString(R.string.claim_profile_contact_message));
        claimProfileContactMessage.setTypeface(typeface);

        email = (EditText) findViewById(R.id.claim_profile_email);
        email.setTypeface(typeface);

        mobileNumber = (EditText) findViewById(R.id.claim_profile_mobile_number);
        mobileNumber.setTypeface(typeface);

        clinicNumber = (EditText) findViewById(R.id.claim_profile_clinic_number);
        clinicNumber.setTypeface(typeface);

        uploadPhotoText = (TextView) findViewById(R.id.claim_profile_upload_text);
        uploadPhotoText.setTypeface(typeface);

        claimProfileNote = (TextView) findViewById(R.id.claim_profile_terms_note);
        claimProfileNote.setTypeface(typeface);

        claimProfileNoteStart = (TextView) findViewById(R.id.claim_profile_terms_start);
        claimProfileNoteStart.setTypeface(typeface);

        claimProfileNoteEnd = (TextView) findViewById(R.id.claim_profile_terms_end);
        claimProfileNoteEnd.setTypeface(typeface);

        submitButton = (Button) findViewById(R.id.claim_profile_submit);
        submitButton.setTypeface(typeface);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    public boolean validateData() {
        Boolean isValid = true;

        if(email.getText().toString().trim().length() == 0) {
            email.setError(getResources().getString(R.string.email_error));
            isValid = false;
        }

        //check for valid email
        Pattern pattern1 = Pattern.compile( "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
        Matcher matcher1 = pattern1.matcher(email.getText().toString());
        if (!matcher1.matches()) {
            email.setError(getResources().getString(R.string.valid_email_error));
            isValid = false;
        }

        if(mobileNumber.getText().toString().trim().length() == 0) {
            mobileNumber.setError(getResources().getString(R.string.mobile_error));
            isValid = false;
        }

        if(clinicNumber.getText().toString().trim().length() == 0) {
            clinicNumber.setError("Clinic number is required");
            isValid = false;
        }

        return isValid;
    }
}
