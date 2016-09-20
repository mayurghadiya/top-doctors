package searchnative.com.topdoctors;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by root on 12/9/16.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*Toast.makeText(parent.getContext(), "On Item Select : \n" + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
