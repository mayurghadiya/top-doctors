package searchnative.com.topdoctors;

import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;

/**
 * Created by root on 9/9/16.
 */
public class SearchFragment extends Fragment {

    LinearLayout mLinearLayout;
    ValueAnimator mAnimator;
    private String[] gender;
    private String[] statSpec;
    AutoCompleteTextView actvGender, actvSpeciality;

    ArrayList<String> specialityList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

        ImageView v = (ImageView) getView().findViewById(R.id.swipe_menu);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawerLayout mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                mDrawer.openDrawer(Gravity.LEFT);
                return true;
            }
        });


        //set font
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ExoMedium.otf");
        EditText etMainSearch = (EditText) getView().findViewById(R.id.search_tab_search_text);
        etMainSearch.setTypeface(typeface);

    }

}
