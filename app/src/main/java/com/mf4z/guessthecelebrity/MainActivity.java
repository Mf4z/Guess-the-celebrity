package com.mf4z.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String[] celeb_url;
    String[] celeb_name;

private static final String site_url = "http://www.posh24.se/kandisar";
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button_answer_0);
        button1 = (Button) findViewById(R.id.button_answer_1);
        button2 = (Button) findViewById(R.id.button_answer_2);
        button3 = (Button) findViewById(R.id.button_answer_3);

        DownloadCelebData celebData = new DownloadCelebData();

        String result = null;
        try {
            result = celebData.execute(site_url).get();
        } catch (Exception e) {

            e.printStackTrace();
        }

        //Using Regex to get Photo url
        Pattern p = Pattern.compile("<img src=\"(.*?)\"");
        Matcher m = p.matcher(result);

        int count = 0;
        while(m.find()){
            //celeb_url[count] = m.group(1);
            Log.i("Celebrity_url_photos",m.group(1));


            count++;
        }

        Log.i("Celebrity",result);

//        count = 0;
//        while (count < celeb_url.length)
//        Log.i("Celebrity_url_photos",celeb_url[count]);
    }


    public class DownloadTopCelebsPhoto extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection;
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadCelebData extends AsyncTask<String, Void, String> {

        URL url;
        HttpURLConnection connection;
        String result = "";

        @Override
        protected String doInBackground(String... urls) {

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
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
                return "Failed" + e;
            }
        }
    }
}


