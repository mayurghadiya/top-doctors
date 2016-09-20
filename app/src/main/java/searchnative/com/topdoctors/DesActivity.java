package searchnative.com.topdoctors;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class DesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_content);

        TextView textView = (TextView)findViewById(R.id.text_view);
        textView.setText("A TextView in Fragment another");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
