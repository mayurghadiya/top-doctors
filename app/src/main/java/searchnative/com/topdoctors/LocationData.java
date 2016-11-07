package searchnative.com.topdoctors;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 21/10/16.
 */

public class LocationData {

    public static LocationData mInstance = null;

    public List locationList, locationId;

    public String webServiceUrl = AppConfig.getWebServiceUrl();

    private LocationData() {
        setCountryList();
    }

    public static LocationData getmInstance() {
        if(mInstance == null) {
            mInstance = new LocationData();
        }

        return mInstance;
    }

    public void setCountryList() {

        class LocationJSON extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "location/getLocation?lang=" + LocalInformation.getLocaleLang();
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
            //String quickLang = LocalInformation.getLocaleLang();

            @Override
            protected String doInBackground(Void... params) {
                //List<NameValuePair> nameValuePairs = new ArrayList<>();
                //nameValuePairs.add(new BasicNameValuePair("lang", quickLang));

                try {
                    HttpGet httpGet = new HttpGet(URL);
                    //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpGet, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.v("Location URL", URL);
                try {
                    locationList = new ArrayList();
                    locationId = new ArrayList();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject country = jsonArray.getJSONObject(i);
                        locationList.add(country.getString("name"));
                        locationId.add(country.getString("locationId"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        new LocationJSON().execute();
    }
}
