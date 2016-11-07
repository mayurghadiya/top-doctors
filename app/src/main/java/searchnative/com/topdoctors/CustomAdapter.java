package searchnative.com.topdoctors;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 6/10/16.
 */

public class CustomAdapter extends BaseAdapter {

    Context context;
    List icons = new ArrayList();
    List speciality = new ArrayList();
    LayoutInflater layoutInflater;
    private int hidingItemIndex;
    private Typeface typeface;


    public CustomAdapter(Context applicationContext, List icons, List speciality) {
        this.context = applicationContext;
        this.icons = icons;
        Log.v("log", String.valueOf(icons.size()));
        this.speciality = speciality;
        layoutInflater = (LayoutInflater.from(applicationContext));
        this.hidingItemIndex = 0;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ExoMedium.otf");
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.custom_spinner_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        Context context = icon.getContext();
        int id = context.getResources().getIdentifier(icons.get(i).toString(), "mipmap", context.getPackageName());
        icon.setImageResource(id);
        TextView names = (TextView) view.findViewById(R.id.textView);
        if(i == 0) {
            icon.setVisibility(View.GONE);
            names.setTextColor(context.getResources().getColor(R.color.textHighlightColor));
            //names.setPadding(0,0,0,10);
        }
        names.setTypeface(typeface);
        names.setText(speciality.get(i).toString());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if (position == hidingItemIndex) {
            TextView tv = new TextView(context);
            tv.setVisibility(View.GONE);
            tv.setTypeface(typeface);
            //tv.setTextSize(16);
            int dp = (int) (context.getResources().getDimension(R.dimen.add_doctor_input_font) / context.getResources().getDisplayMetrics().density);
            tv.setTextSize(dp);
            v = tv;
        } else {
            v = super.getDropDownView(position, null, parent);
        }
        return v;
    }
}
