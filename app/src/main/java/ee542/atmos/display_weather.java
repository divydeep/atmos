package ee542.atmos;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class display_weather extends FragmentActivity implements ActionBar.TabListener {
    MyAdapter mAdapter;
    ViewPager mPager;
    static final int NUM_ITEMS = 3;
    protected JSONObject currentWeather;
    protected JSONObject hourlyWeather;
    protected JSONObject weeklyWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);
        final ActionBar actionBar = getActionBar();
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        Intent intent = getIntent();
        try {
            currentWeather = new JSONObject(intent.getStringExtra("currentWeather"));
            hourlyWeather = new JSONObject(intent.getStringExtra("hourlyWeather"));
            weeklyWeather = new JSONObject(intent.getStringExtra("weeklyWeather"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < mAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    break;
//                default:
//                    return null;
//            }
            Fragment fragment = new SectionFragment();
            Bundle args = new Bundle();
            args.putInt(SectionFragment.ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "NOW";
                case 1:
                    return "HOURLY";
                case 2:
                    return "DAILY";
                default:
                    return null;
            }
        }
    }

    public class SectionFragment extends Fragment {
        public static final String ARG_POSITION = "position";
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/weathericons.ttf");
            View view = null;
            Bundle args = getArguments();
            int position = args.getInt(ARG_POSITION);
            switch (position) {
                case 0:
                    view = handlePage0(typeface, container, inflater);
                    break;
                case 1:
                    view = handlePage1(typeface, container, inflater);
                    break;
                case 2:
                    view = handlePage2(typeface, container, inflater);
                    break;
                default:
                    return null;
            }
            return view;
        }
    }

    private View handlePage0(Typeface typeface, ViewGroup container, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_pager_list, container, false);

        TextView icon1 = (TextView) view.findViewById(R.id.icon);
        TextView icon2 = (TextView) view.findViewById(R.id.iconPrecipitation);
        TextView icon3 = (TextView) view.findViewById(R.id.iconRainChance);
        TextView icon4 = (TextView) view.findViewById(R.id.iconWindSpeed);
        TextView icon5 = (TextView) view.findViewById(R.id.iconPressure);
        TextView icon6 = (TextView) view.findViewById(R.id.humidity);
        TextView icon7 = (TextView) view.findViewById(R.id.iconVisibility);
        TextView icon8 = (TextView) view.findViewById(R.id.iconCloudCover);

        TextView data1 = (TextView) view.findViewById(R.id.temperature);
        TextView data1a= (TextView) view.findViewById(R.id.degree);
        TextView data2 = (TextView) view.findViewById(R.id.precipitation);
        TextView data3 = (TextView) view.findViewById(R.id.rainChance);
        TextView data4 = (TextView) view.findViewById(R.id.windSpeed);
        TextView data5 = (TextView) view.findViewById(R.id.pressure);
        TextView data6 = (TextView) view.findViewById(R.id.iconHumidity);
        TextView data7 = (TextView) view.findViewById(R.id.visibility);
        TextView data8 = (TextView) view.findViewById(R.id.cloudCover);

        icon1.setTypeface(typeface);
        icon2.setTypeface(typeface);
        icon3.setTypeface(typeface);
        icon4.setTypeface(typeface);
        icon5.setTypeface(typeface);
        icon6.setTypeface(typeface);
        icon7.setTypeface(typeface);
        icon8.setTypeface(typeface);
        data1a.setTypeface(typeface);

        JSONObject obj = currentWeather;
        try {
            switch(obj.getString("icon")) {
                case "clear-day":
                    icon1.setText(R.string.icon_clear_day);
                    break;
                case "clear-night":
                    icon1.setText(R.string.icon_clear_night);
                    break;
                case "rain":
                    icon1.setText(R.string.icon_rain);
                    break;
                case "snow":
                    icon1.setText(R.string.icon_snow);
                    break;
                case "sleet":
                    icon1.setText(R.string.icon_sleet);
                    break;
                case "wind":
                    icon1.setText(R.string.icon_wind);
                    break;
                case "fog":
                    icon1.setText(R.string.icon_fog);
                    break;
                case "cloudy":
                    icon1.setText(R.string.icon_cloudy);
                    break;
                case "partly-cloudy-day":
                    icon1.setText(R.string.icon_partly_cloudy_day);
                    break;
                case "partly-cloudy-night":
                    icon1.setText(R.string.icon_partly_cloudy_night);
                    break;
                case "hail":
                    icon1.setText(R.string.icon_hail);
                    break;
                case "thunderstorm":
                    icon1.setText(R.string.icon_thunderstorm);
                case "tornado":
                    icon1.setText(R.string.icon_tornado);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        icon2.setText(R.string.icon_precipitation);
        icon3.setText(R.string.icon_chance_rain);
        icon4.setText(R.string.icon_wind_speed);
        icon5.setText(R.string.icon_pressure);
        icon6.setText(R.string.icon_humidity);
        icon7.setText(R.string.icon_visibility);
        icon8.setText(R.string.icon_cloud_cover);

        try {
            data1.setText(obj.getString("temperature"));
            data1a.setText(R.string.icon_fahrenheit);
            data2.setText(obj.getString("precipitation") + "%");
            data3.setText(obj.getString("rainChance") + "%");
            data4.setText(obj.getString("windSpeed") + " mph");
            data5.setText(obj.getString("pressure") + " mb");
            data6.setText(obj.getString("humidity") + "%");
            data7.setText(obj.getString("visibility") + " mi");
            data8.setText(obj.getString("cloudCover") + "%");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Precipitation");
                alertDialog.setMessage("The combined 'chance', 'intensity', and 'type' of precipitation.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Precipitation Chance");
                alertDialog.setMessage("The probability that any precipitation will occur at all.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Wind Speed");
                alertDialog.setMessage("The average speed of the wind.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Pressure");
                alertDialog.setMessage("The sea-level pressure of the air.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Relative Humidity");
                alertDialog.setMessage("The relative humidity, or 'how close the air is to being saturated by water'.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Visibility");
                alertDialog.setMessage("The distance at which an object or light can be clearly discerned.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        icon8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(display_weather.this).create();
                alertDialog.setTitle("Cloud Cover");
                alertDialog.setMessage("The percentage of sky obscured by clouds.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


        return view;
    }

    private View handlePage1(Typeface typeface, ViewGroup container, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_pager_hourly, container, false);
        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.hourlyTable);
        JSONObject obj1 = hourlyWeather;
        JSONArray jsonArray = null;
        try {
            jsonArray = obj1.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int sizeInDP = 7;

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());
        TextView textView = (TextView) view.findViewById(R.id.summary);
        try {
            textView.setText(obj1.getString("summary"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<jsonArray.length();i++){
            TableRow row = (TableRow) inflater.inflate(R.layout.fragment_row, null);
            row.setId(1000 + i);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(marginInDp,marginInDp,marginInDp,marginInDp);
            row.setLayoutParams(params);

            TextView icon1 = (TextView) row.findViewById(R.id.icon);
            TextView icon2 = (TextView) row.findViewById(R.id.iconTemp);
            TextView icon3 = (TextView) row.findViewById(R.id.iconHumidity);
            TextView icon4 = (TextView) row.findViewById(R.id.iconWindSpeed);
            TextView icon5 = (TextView) row.findViewById(R.id.iconCloudCover);
            icon1.setTypeface(typeface);
            icon2.setTypeface(typeface);
            icon3.setTypeface(typeface);
            icon4.setTypeface(typeface);
            icon5.setTypeface(typeface);

            TextView data1 = (TextView) row.findViewById(R.id.time);
            TextView data2 = (TextView) row.findViewById(R.id.temperature);
            TextView data2a= (TextView) row.findViewById(R.id.degree);
            data2a.setTypeface(typeface);
            data2a.setText(R.string.icon_fahrenheit);
            TextView data3 = (TextView) row.findViewById(R.id.humidity);
            TextView data4 = (TextView) row.findViewById(R.id.windSpeed);
            TextView data5 = (TextView) row.findViewById(R.id.cloudCover);

            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                switch(obj.getString("icon")) {
                    case "clear-day":
                        icon1.setText(R.string.icon_clear_day);
                        break;
                    case "clear-night":
                        icon1.setText(R.string.icon_clear_night);
                        break;
                    case "rain":
                        icon1.setText(R.string.icon_rain);
                        break;
                    case "snow":
                        icon1.setText(R.string.icon_snow);
                        break;
                    case "sleet":
                        icon1.setText(R.string.icon_sleet);
                        break;
                    case "wind":
                        icon1.setText(R.string.icon_wind);
                        break;
                    case "fog":
                        icon1.setText(R.string.icon_fog);
                        break;
                    case "cloudy":
                        icon1.setText(R.string.icon_cloudy);
                        break;
                    case "partly-cloudy-day":
                        icon1.setText(R.string.icon_partly_cloudy_day);
                        break;
                    case "partly-cloudy-night":
                        icon1.setText(R.string.icon_partly_cloudy_night);
                        break;
                    case "hail":
                        icon1.setText(R.string.icon_hail);
                        break;
                    case "thunderstorm":
                        icon1.setText(R.string.icon_thunderstorm);
                    case "tornado":
                        icon1.setText(R.string.icon_tornado);
                }

                long unixSec = obj.getLong("time");
                Date date = new Date(unixSec*1000L);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE K a");
                String normalTIme = simpleDateFormat.format(date);
                data1.setText(normalTIme);
                data2.setText(obj.getString("temperature"));
                data3.setText(obj.getString("humidity") + "%");
                data4.setText(obj.getString("windSpeed") + " mph");
                data5.setText(obj.getString("cloudCover") + "%");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            icon2.setText(R.string.icon_thermometer);
            icon3.setText(R.string.icon_humidity);
            icon4.setText(R.string.icon_wind_speed);
            icon5.setText(R.string.icon_cloud_cover);
            tableLayout.addView(row);
        }
        return view;
    }

    private View handlePage2(Typeface typeface, ViewGroup container, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_pager_daily, container, false);
        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.dailyTable);
        JSONObject obj1 = weeklyWeather;
        JSONArray jsonArray = null;
        try {
            jsonArray = obj1.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int sizeInDP = 7;

        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources().getDisplayMetrics());

        for(int i=0;i<jsonArray.length();i++){
            TableRow row = (TableRow) inflater.inflate(R.layout.fragment_row_daily, null);
            row.setId(1000 + i);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(marginInDp,marginInDp,marginInDp,marginInDp);
            row.setLayoutParams(params);

            TextView icon1 = (TextView) row.findViewById(R.id.icon);
            TextView icon2 = (TextView) row.findViewById(R.id.iconTemp);
            TextView icon3 = (TextView) row.findViewById(R.id.iconRainChance);
            TextView icon4 = (TextView) row.findViewById(R.id.iconSunrise);
            TextView icon5 = (TextView) row.findViewById(R.id.iconSunset);
            icon1.setTypeface(typeface);
            icon2.setTypeface(typeface);
            icon3.setTypeface(typeface);
            icon4.setTypeface(typeface);
            icon5.setTypeface(typeface);

            TextView data1 = (TextView) row.findViewById(R.id.time);
            TextView data2 = (TextView) row.findViewById(R.id.summary);
            TextView data3 = (TextView) row.findViewById(R.id.tempMax);
            TextView data3a= (TextView) row.findViewById(R.id.degree1);
            data3a.setTypeface(typeface);
            data3a.setText(R.string.icon_fahrenheit);
            TextView data4 = (TextView) row.findViewById(R.id.tempMin);
            TextView data4a= (TextView) row.findViewById(R.id.degree2);
            data4a.setTypeface(typeface);
            data4a.setText(R.string.icon_fahrenheit);
            TextView data5 = (TextView) row.findViewById(R.id.rainChance);
            TextView data6 = (TextView) row.findViewById(R.id.sunrise);
            TextView data7 = (TextView) row.findViewById(R.id.sunset);

            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                switch(obj.getString("icon")) {
                    case "clear-day":
                        icon1.setText(R.string.icon_clear_day);
                        break;
                    case "clear-night":
                        icon1.setText(R.string.icon_clear_night);
                        break;
                    case "rain":
                        icon1.setText(R.string.icon_rain);
                        break;
                    case "snow":
                        icon1.setText(R.string.icon_snow);
                        break;
                    case "sleet":
                        icon1.setText(R.string.icon_sleet);
                        break;
                    case "wind":
                        icon1.setText(R.string.icon_wind);
                        break;
                    case "fog":
                        icon1.setText(R.string.icon_fog);
                        break;
                    case "cloudy":
                        icon1.setText(R.string.icon_cloudy);
                        break;
                    case "partly-cloudy-day":
                        icon1.setText(R.string.icon_partly_cloudy_day);
                        break;
                    case "partly-cloudy-night":
                        icon1.setText(R.string.icon_partly_cloudy_night);
                        break;
                    case "hail":
                        icon1.setText(R.string.icon_hail);
                        break;
                    case "thunderstorm":
                        icon1.setText(R.string.icon_thunderstorm);
                    case "tornado":
                        icon1.setText(R.string.icon_tornado);
                }

                long unixSec = obj.getLong("time");
                Date date = new Date(unixSec*1000L);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                String normalTIme = simpleDateFormat.format(date);
                data1.setText(normalTIme);
                data2.setText(obj.getString("summary"));
                data3.setText(obj.getString("temperatureMax"));
                data4.setText(" / " + obj.getString("temperatureMin"));
                data5.setText(obj.getString("rainChance") + "%");

                long unixSunriseSec = obj.getLong("sunrise");
                long unixSunsetSec  = obj.getLong("sunset");

                Date sunriseDate = new Date(unixSunriseSec*1000L);
                Date sunsetDate  = new Date(unixSunsetSec*1000L);

                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("h:mm a");
                String sunriseTime = simpleDateFormat1.format(sunriseDate);
                String sunsetTime = simpleDateFormat1.format(sunsetDate);
                data6.setText(sunriseTime);
                data7.setText(sunsetTime);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            icon2.setText(R.string.icon_thermometer);
            icon3.setText(R.string.icon_chance_rain);
            icon4.setText(R.string.icon_sunrise);
            icon5.setText(R.string.icon_sunset);
            tableLayout.addView(row);
        }

        return view;
    }
}
