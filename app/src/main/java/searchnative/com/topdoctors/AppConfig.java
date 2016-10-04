package searchnative.com.topdoctors;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by root on 14/9/16.
 */
public class AppConfig {

    static String WEB_SERVICE_URL = "http://dev.searchnative.com/topdoctor/api/";

    //private static String WEB_SERVICE_URL = "http://192.168.1.28/top-doctors/api/";

    /**
     * Get web service url
     * @return string
     */
    public static String getWebServiceUrl() {
        return WEB_SERVICE_URL;
    }

    public static void setWebServiceUrl(String url) {
        WEB_SERVICE_URL = url;
    }

}
