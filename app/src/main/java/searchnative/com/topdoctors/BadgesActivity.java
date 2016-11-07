package searchnative.com.topdoctors;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BadgesActivity extends AppCompatActivity {

    private Typeface typeface;
    private TextView badges1, badges2, badges3, badges4, badges5, badges6, title;
    private ImageView closeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_badges);

        typeface = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/ExoMedium.otf");

        badges1 = (TextView) findViewById(R.id.badges1);
        badges2 = (TextView) findViewById(R.id.badges2);
        badges3 = (TextView) findViewById(R.id.badges3);
        badges4 = (TextView) findViewById(R.id.badges4);
        badges5 = (TextView) findViewById(R.id.badges5);
        badges6 = (TextView) findViewById(R.id.badges6);
        title = (TextView) findViewById (R.id.badgesText);
        closeActivity = (ImageView) findViewById(R.id.close_activity);

        badges1.setTypeface(typeface);
        badges2.setTypeface(typeface);
        badges3.setTypeface(typeface);
        badges4.setTypeface(typeface);
        badges5.setTypeface(typeface);
        badges6.setTypeface(typeface);
        title.setTypeface(typeface);

        //close activity
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
