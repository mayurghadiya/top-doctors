package searchnative.com.topdoctors;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 8/9/16.
 */
public class Preference {

    private static SharedPreferences sharedPreferences = null;

    public static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences("PREF_FILE", Context.MODE_PRIVATE);
    }

    public static String getValue(Context context, String key, String value) {
        Preference.openPref(context);
        String result = Preference.sharedPreferences.getString(key, value);
        //Preference.sharedPreferences = null;

        return result;
    }

    public static void setValue(Context context, String key, String value) {
        Preference.openPref(context);
        SharedPreferences.Editor editor = Preference.sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        //editor = null;
        //Preference.sharedPreferences = null;
    }
}
