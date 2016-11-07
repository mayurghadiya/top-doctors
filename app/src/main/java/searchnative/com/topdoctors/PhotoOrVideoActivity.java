package searchnative.com.topdoctors;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoOrVideoActivity extends AppCompatActivity {

    private TextView photoOrVideoTitle;
    private Typeface typeface;
    private ImageView closePhotoOrVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_photo_or_video);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");

        photoOrVideoTitle = (TextView) findViewById(R.id.photo_or_video_title);
        photoOrVideoTitle.setTypeface(typeface);

        //close activity
        closePhotoOrVideo = (ImageView) findViewById(R.id.close_photo_or_video);
        closePhotoOrVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("Item No: ", String.valueOf(i));
            }
        });
    }
}
