package searchnative.com.topdoctors;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by root on 19/9/16.
 */
public class AddDoctor extends Fragment {

    Typeface typeface;
    TextView addDoctorTitle;
    EditText addDoctorName, addDoctorClinicName, addDoctorEmail, addDoctorMobileNumber, addDoctorClinicAddress;
    Spinner addDoctorLocation, addDoctorSpeciality, addDoctorGender;

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

        addDoctorTitle.setTypeface(typeface);
        addDoctorName.setTypeface(typeface);
        addDoctorClinicName.setTypeface(typeface);
        addDoctorEmail.setTypeface(typeface);
        addDoctorMobileNumber.setTypeface(typeface);
        addDoctorClinicAddress.setTypeface(typeface);
    }

    public void loadGenderSpinner() {

    }
}
