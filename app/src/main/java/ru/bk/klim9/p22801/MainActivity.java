package ru.bk.klim9.p22801;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final String QUERY_URL = "http://devtest.ad-sys.com/c/apptest?id=";
    final String text1 = " http://apptest.com/i?id=";
    final String text2 = " http://apptest.com/i?id";

    String[] sArray;
    String stringResult = "";
    String s3 = "";
    Button buttonSearch;
    TextView textAnswer;
    Uri uriSMSURI;
    Cursor cur;
    MyTask mt;
    boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        textAnswer = (TextView) findViewById(R.id.textAnswer);

        uriSMSURI = Uri.parse("content://sms/inbox");
        cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        buttonSearch.setOnClickListener(this);
    }
    
    /*This method is not designed to search for SEVERAL positive results.
    Reason: in the specification refers to the same message, I have a time limit on the job for two days.
    Also had to use trim method(). The reason: most of the online services for sending SMS messages
    add messages after special characters.
    */
    public void analysisSMSTexts() {
        int i = 0;
        while (cur.moveToNext()) {

            if (cur.getString(13).indexOf(text1) != -1) {

                sArray = cur.getString(13).split("=");
                s3 = sArray[sArray.length - 1].trim();

                if (sArray[sArray.length - 2].indexOf(text2) != -1) {

                    if (s3.matches("[A-Za-z0-9]+")) {
                        stringResult = QUERY_URL + s3;
                        flag = false;
                    }
                }
            }

        }
        if (flag) {
            textAnswer.setText("Not found");
        }else {
            mt = new MyTask();
            mt.execute(stringResult);
        }

    }

    @Override
    public void onClick(View v) {

        analysisSMSTexts();

    }

    class MyTask extends AsyncTask<String, Void, Void>{

        String response;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... urls) {

            for (String url : urls) {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler res = new BasicResponseHandler();
                HttpGet http = new HttpGet(url);

                try {
                    response = (String) hc.execute(http, res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textAnswer.setText(response);

        }
    }
}