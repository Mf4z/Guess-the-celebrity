package com.mf4z.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celeb_url;
    ArrayList<String> celeb_name;

    private static final String site_url = "http://www.posh24.se/kandisar";
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    private Random mRandom;
    private int mChosenCeleb;
    private int mLocationOfCorrect;
    private ArrayList<String> mAnswers;
    private Pattern mPattern;
    private Matcher mMatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celeb_url = new ArrayList<String>();
        celeb_name = new ArrayList<String>();
        mAnswers = new ArrayList<>();

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button_answer_0);
        button1 = (Button) findViewById(R.id.button_answer_1);
        button2 = (Button) findViewById(R.id.button_answer_2);
        button3 = (Button) findViewById(R.id.button_answer_3);

        DownloadCelebData celebData = new DownloadCelebData();

        //Holds result of Dowloaded celeb info from the web in html
        String result = null;

        try {
            result = celebData.execute(site_url).get();

            String[] splitString = result.split("<div class=\"listedArticles\">");

            //Finds celeb photo url from result and adds it to the ArrayList of celeb photo urls
            findPhotoUrlFromResult(splitString[0]);

            Log.i("Celebrity", splitString[0]);

            //Finds celeb name from result and adds it to the ArrayList of celeb names
            findCelebNameFromResult(splitString[0]);

        } catch (Exception e) {

            e.printStackTrace();
        }





        for (String url : celeb_url) {

            Log.i("Celebrity_url_photos", url);

        }


        for (String url : celeb_name) {

            Log.i("Celebrity_url_names", url);

        }


        setGame();
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


    private void setGame() {

        mRandom = new Random();
        mChosenCeleb = mRandom.nextInt(celeb_url.size());
        DownloadTopCelebsPhoto photo = new DownloadTopCelebsPhoto();

        String photo_url = celeb_url.get(mChosenCeleb);

        Bitmap celeb_photo;

        try {
            celeb_photo = photo.execute(photo_url).get();
            imageView.setImageBitmap(celeb_photo);
        } catch (Exception e) {

            e.printStackTrace();
        }

        mLocationOfCorrect = mRandom.nextInt(4);

        int incorrectAnswerLocation = 0;

        mAnswers.clear();

        for (int i= 0; i < 4; i++){

                if (i == mLocationOfCorrect){
                    mAnswers.add(celeb_name.get(mChosenCeleb));
                }

                else{

                    incorrectAnswerLocation = mRandom.nextInt(celeb_url.size());

                    while (incorrectAnswerLocation == mChosenCeleb){

                        incorrectAnswerLocation = mRandom.nextInt(celeb_url.size());
                    }

                    mAnswers.add(celeb_name.get(incorrectAnswerLocation));
                }
        }

        button0.setText(mAnswers.get(0));
        button1.setText(mAnswers.get(1));
        button2.setText(mAnswers.get(2));
        button3.setText(mAnswers.get(3));




    }

    private void findPhotoUrlFromResult(String result) {

        //Using Regex to get Photo url
        mPattern = Pattern.compile("<img src=\"(.*?)\"");
        mMatcher = mPattern.matcher(result);

        while (mMatcher.find()) {
            String found = mMatcher.group(1);
            System.out.println(found);
            celeb_url.add(found);
        }

    }


    private void findCelebNameFromResult(String result) {

        //Using Regex to get Celeb name
        mPattern = Pattern.compile("alt=\"(.*?)\"");
        mMatcher = mPattern.matcher(result);

        while (mMatcher.find()) {
            String found = mMatcher.group(1);
            System.out.println(found);
            celeb_name.add(found);
        }

    }

    public void chosenCeleb(View view){

        String selectedTag =  view.getTag().toString();
        int tag = Integer.parseInt(selectedTag);
        int correctAnswer = mLocationOfCorrect;

        if(tag == correctAnswer){
            Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this,"wrong! It was : "+mAnswers.get(correctAnswer),Toast.LENGTH_SHORT).show();
        }

        //Resets game after correct guess
        setGame();

    }
}


