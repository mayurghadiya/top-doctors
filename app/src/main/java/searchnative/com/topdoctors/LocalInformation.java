package searchnative.com.topdoctors;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by root on 9/9/16.
 */
public class LocalInformation {

    private static String LANG;

    public static String getLocaleLang() {
        return LANG;
    }

    public static String setLocalLang(String lang) {
        return LANG = lang;
    }
}
