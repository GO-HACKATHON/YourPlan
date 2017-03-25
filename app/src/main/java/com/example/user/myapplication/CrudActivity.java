package com.example.user.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CrudActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_saveplan)
        {
            String plan_name = ((EditText)findViewById(R.id.plan_name)).getText().toString();
            String date = ((EditText)findViewById(R.id.date)).getText().toString();
            String time = ((EditText)findViewById(R.id.time)).getText().toString();
            String description = ((EditText)findViewById(R.id.description)).getText().toString();
            String time_to_prepare = ((EditText)findViewById(R.id.time_to_prepare)).getText().toString();
            String plan_place = "Test Tempat";
            String plan_address = "Dimana Aja";
            String lat = "-6.245586";
            String lng = "106.798531";
            String id_user = "1";

            new AddData().execute(plan_name, date, time, description, time_to_prepare,
                    plan_place, plan_address,
                    lat, lng, id_user);
        }
        return super.onOptionsItemSelected(item);
    }

    class AddData extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog = null;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(CrudActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            pDialog.dismiss();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    CrudActivity.this.finish();

                }
            });
        }

        @Override
        protected String doInBackground(String... params)
        {
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("plan_name", params[0]));
            values.add(new BasicNameValuePair("date", params[1]));
            values.add(new BasicNameValuePair("time", params[2]));
            values.add(new BasicNameValuePair("description", params[3]));
            values.add(new BasicNameValuePair("time_to_prepare", params[4]));
            values.add(new BasicNameValuePair("plan_place", params[5]));
            values.add(new BasicNameValuePair("plan_address", params[6]));
            values.add(new BasicNameValuePair("lat", params[7]));
            values.add(new BasicNameValuePair("lng", params[8]));
            values.add(new BasicNameValuePair("id_user", params[9]));
            InputStream is = null;

            String result = "";
            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getResources().getString(R.string.main_url) + ":8000/api/plan/insert");
                httpPost.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
    }
}
