package ee542.atmos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected Button searchBtn;
    protected Button clearBtn;
    protected Button currentLocation;
    protected TextView street_address;
    protected TextView city;
    protected TextView state;
    protected Spinner spinner;
    protected AutoCompleteTextView cityAuto;
    protected EditText streetAddr;

    protected GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected Location mCurrentLocation;

    private View mProgressView;

    protected JSONObject currentWeather;
    protected JSONObject hourlyWeather;
    protected JSONObject weeklyWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/metroscript.ttf");
        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto.ttf");

        textView.setTypeface(typeface);
        textView.setTextSize(90);

        street_address = (TextView) findViewById(R.id.street_address_title);
        city = (TextView) findViewById(R.id.city_title);
        state = (TextView) findViewById(R.id.state_title);
        spinner = (Spinner) findViewById(R.id.spinner);
        cityAuto = (AutoCompleteTextView) findViewById(R.id.city);
        streetAddr = (EditText) findViewById(R.id.streetAddress);
        searchBtn = (Button) findViewById(R.id.searchButton);
        clearBtn = (Button) findViewById(R.id.clearButton);
        currentLocation = (Button) findViewById(R.id.currentLoc);

        spinner.setFocusableInTouchMode(true);
        street_address.setTypeface(roboto);
        city.setTypeface(roboto);
        state.setTypeface(roboto);

        mProgressView = findViewById(R.id.progressBar);

        getActionBar().hide();
        JSONObject obj = null;
        JSONArray cityArray = null;
        List<String> cityList = new ArrayList<String>();
        try {
            obj = new JSONObject(loadJSONFromAsset());
            cityArray = obj.getJSONArray("array");
            for (int i=0;i<cityArray.length();i++){
                cityList.add(cityArray.getString(i));
            }
        }
        catch (JSONException j) {
            j.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, cityList);

        cityAuto.setAdapter(adapter);
        cityAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    cityAuto.clearFocus();
                    spinner.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    spinner.performClick();
                }
                return false;
            }
        });

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, R.layout.spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stateAdapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMethod(view);
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streetAddr.setText("");
                cityAuto.setText("");
                spinner.setSelection(0);
                showProgress(false);
            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLocationMethod();
            }
        });

        buildGoogleApiClient();
    }

    private void searchMethod(View view) {
        checkFields();
    }

    private void checkFields() {
        streetAddr.setError(null);
        cityAuto.setError(null);
        boolean cancel = false;
        View focusView = null;
        String address = streetAddr.getText().toString();
        String city = cityAuto.getText().toString();

        if (TextUtils.isEmpty(address)) {
            streetAddr.setError("This field is required");
            focusView = streetAddr;
            cancel = true;
        }

        if (TextUtils.isEmpty(city)) {
            cityAuto.setError("This field is required");
            focusView = cityAuto;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            doSearch();
        }
    }

    private void doSearch() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "http://atmos-env-search.elasticbeanstalk.com/";

        HashMap<String, String> params = new HashMap<>();
        params.put("street", streetAddr.getText().toString());
        params.put("city", cityAuto.getText().toString());
        params.put("state", spinner.getSelectedItem().toString());

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, url, params, this.createRequestSuccessListener(), this.createRequestErrorListener());
        queue.add(jsonObjectRequest);
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("atmos/volley", error.toString());
            }
        };
    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ack = response.getString("ack");
                    if (ack.equals("OK")) {
                        String latitude = response.getString("latitude");
                        String longitude = response.getString("longitude");
                        getWeather(latitude, longitude);
                    }
                    else {
                        showProgress(false);
                        Context context = getApplicationContext();
                        CharSequence text = "Sorry, please enter a correct address!";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void getWeather(String latitude, String longitude) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "http://atmos-env-weather.elasticbeanstalk.com/";

        HashMap<String, String> params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        CustomRequest weatherRequest = new CustomRequest(Request.Method.POST, url, params, this.weatherRequestSuccessListener(), this.weatherRequestErrorListener());
        weatherRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 5 , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(weatherRequest);
    }

    private Response.ErrorListener weatherRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("atmos/volley", error.toString());
            }
        };
    }

    private Response.Listener<JSONObject> weatherRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    currentWeather = response.getJSONObject("currently");
                    hourlyWeather  = response.getJSONObject("hourly");
                    weeklyWeather  = response.getJSONObject("daily");
                    callNextActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void callNextActivity() {
        Intent intent = new Intent(this, display_weather.class);
        intent.putExtra("currentWeather", currentWeather.toString());
        intent.putExtra("hourlyWeather", hourlyWeather.toString());
        intent.putExtra("weeklyWeather", weeklyWeather.toString());
        showProgress(false);
        startActivity(intent);
    }


    private String loadJSONFromAsset() {
        String cityJson = null;
        try{
            InputStream is = getAssets().open("json/cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            cityJson = new String(buffer,"UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return cityJson;
    }

    protected synchronized void doLocationMethod() {
        checkLocationSettings();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    protected synchronized void checkLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        showProgress(true);
                        Log.i("atmos", "All location settings are satisfied.");
                        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        Log.i("atmos", String.valueOf(mCurrentLocation.getLatitude()));
                        Log.i("atmos", String.valueOf(mCurrentLocation.getLongitude()));
                        getWeather(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        checkLocationSettings();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        showProgress(true);
                        Log.i("atmos", "User agreed to make required location settings changes.");
                        LocationRequest mLocationRequest = new LocationRequest();
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient,
                                mLocationRequest,
                                this
                        ).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.i("atmos", "yayy");
                            }
                        });
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("atmos", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("atmos", "Connected");
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("atmos", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("atmos", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
//        showProgress(true);
        mCurrentLocation = location;
        Log.i("atmos", String.valueOf(mCurrentLocation.getLatitude()));
        Log.i("atmos", String.valueOf(mCurrentLocation.getLongitude()));
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.i("atmos","Location Services Stopped");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}