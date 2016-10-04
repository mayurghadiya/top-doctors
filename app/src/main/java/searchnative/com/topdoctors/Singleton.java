package searchnative.com.topdoctors;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayur on 28/9/16.
 */

public class Singleton {

    private static Singleton instance;

    public static List<String> specialityArrayList;

    static String webServiceUrl = AppConfig.getWebServiceUrl();


    public static void initInstance() {
        if(instance == null) {
            getSpecialityList();
            Log.v("data", specialityArrayList.toString());
            instance = new Singleton();
        }
    }

    public static Singleton getInstance() {
        return instance;
    }

    private Singleton() {

    }

    public static void getSpecialityList() {

        class SpecialityData extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "Speciality/index/?lang=" + LocalInformation.getLocaleLang();
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                HttpPost httpPost = new HttpPost(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    return mClient.execute(httpPost, responseHandler);
                } catch (ClientProtocolException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                mClient.close();

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    specialityArrayList = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject spec = jsonArray.getJSONObject(i);
                        specialityArrayList.add(spec.getString("SPEC"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new SpecialityData().execute();
    }

}