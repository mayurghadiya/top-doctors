package searchnative.com.topdoctors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NavHeaderMainActivity extends AppCompatActivity {

    public static TextView textViewObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        Intent sender = getIntent();
        String extraData = sender.getExtras().getString("Name");

        textViewObj = (TextView) findViewById(R.id.login_user_name);
        textViewObj.setText("Hello");

        //TextView user = (TextView) findViewById(R.id.login_user_name);
        //user.setText(extraData);

        startActivity(new Intent(NavHeaderMainActivity.this, MainActivity.class));

        //TextView user = (TextView) findViewById(R.id.login_user_name);
        //user.setText(extraData);

    }


}
