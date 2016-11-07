package searchnative.com.topdoctors;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
 * Created by root on 7/10/16.
 */

public class CountryData {

    private static CountryData mInstance = null;

    public List countryList;

    public String webServiceUrl = AppConfig.getWebServiceUrl();

    public String appLang = LocalInformation.getLocaleLang();

    private CountryData() {
        setCountryList();
    }

    public static CountryData getmInstance() {
        if(mInstance == null) {
            mInstance = new CountryData();
        }

        return mInstance;
    }

    public void setCountryList() {

        class CountryJSON extends AsyncTask<Void, Void, String> {
            String URL = webServiceUrl + "country";
            AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

            @Override
            protected String doInBackground(Void... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("lang", appLang));
                try {
                    HttpPost httpPost = new HttpPost(URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    return mClient.execute(httpPost, responseHandler);
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    mClient.close();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    countryList = new ArrayList();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject country = jsonArray.getJSONObject(i);
                        countryList.add(country.getString("country_name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        new CountryJSON().execute();
    }
}
