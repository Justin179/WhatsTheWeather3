package tw.com.nec.justin_chen.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;


    public void findWeather(View view){
        //Log.i("cityName: ",cityName.getText().toString());

        // 把輸入文字的鍵盤移除
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);


        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=cb93996c1a3153cc8295bdbd5fe23ec4");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        // http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=cb93996c1a3153cc8295bdbd5fe23ec4





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);


    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather!", Toast.LENGTH_LONG);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

//                    Log.i("main", jsonPart.getString("main"));
//                    Log.i("description", jsonPart.getString("description"));

                    if(main != "" && description != ""){
                        message += main+": "+description+"\r\n";
                    }


                }

                if(message!=""){
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}
