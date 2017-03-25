package com.example.user.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Geocoder;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CrudActivity extends AppCompatActivity
{
//    TextView location1;

    private DatePicker datePicker;
    private Calendar calendar;
    private TimePicker timePicker;
    private TextView dateView;
    private TextView timeView;
    private int year, month, day, name_month, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);
//
//        location1 = (TextView)findViewById(R.id.location);
//        location1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//            }
//        });
        EditText location = (EditText) findViewById(R.id.location);
        location.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    findPlace(v);
                }

                return false;
            }

        });
//        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                findPlace(v);
//            }
//        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateView = (TextView) findViewById(R.id.date);
        dateView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDate(v);
                }
                return false;
            }

        });
        timeView = (TextView) findViewById(R.id.time);
        timeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    setTime(v);
                }
                return false;
            }

        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        name_month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);


        showDate(year,month + 1, day);
        showTime(hour,minute);

        String[] timeList = new String[] {"5 mins", "10 mins", "15 mins", "20 mins"};
        Spinner s = (Spinner)findViewById(R.id.time_to_prepare);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                timeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);

    }
    public void findPlace(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());


                ((EditText) findViewById(R.id.location))
                        .setText(place.getName()+",\n"+
                                place.getAddress());

                String pLat = String.valueOf(place.getLatLng().latitude);
                String pLng = String.valueOf(place.getLatLng().longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());
            }
        }
    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        //akan menampilkan teks ketika kalendar muncul setelah menekan tombol
        Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_SHORT)
                .show();
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(998);
        //akan menampilkan teks ketika kalendar muncul setelah menekan tombol
        Toast.makeText(getApplicationContext(), "Select Time", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datepickerdialog =  new DatePickerDialog(this, myDateListener, year, month, day);
            datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datepickerdialog;
        }else if(id == 998){
            TimePickerDialog timepickerdialog = new TimePickerDialog(this, myTimeListener, hour, minute, true);
            return timepickerdialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showTime(arg1, arg2);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month<10?("0"+month):(month)).append("-").append(day<10?("0"+day):(day)));
    }

    private void showTime(int hour, int minute) {
        timeView.setText(new StringBuilder().append(hour<10?("0"+hour):(hour)).append(":")
                .append(minute<10?("0"+minute):(minute)));
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
            String time_to_prepare = ((Spinner)findViewById(R.id.time_to_prepare)).getSelectedItem().toString();
            String plan_place = "Test Tempat";
            String plan_address = "Dimana Aja";
            String lat = "-6.245586";
            String lng = "106.798531";
            String id_user = (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("Id", null);

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
            pDialog.setCancelable(false);
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
                httpPost.setHeader("Authorization", "Bearer " + (getApplicationContext().getSharedPreferences("LoginToken", Context.MODE_PRIVATE)).getString("jwt-auth-token", null));
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
                Log.d(null, result);//            jsonStr = httpHandler.makeCallService(getResources().getString(R.string.main_url) + ":8000/api/plan/" + idUser
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
    }
}
