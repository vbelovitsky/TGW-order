package com.java.boris.tgw;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Polar;
import com.anychart.core.SeriesPoint;
import com.anychart.core.polar.series.Column;
import com.anychart.core.utils.SeriesA11y;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.PolarSeriesType;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.ScaleTypes;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.scales.Linear;
import com.java.boris.tgw.fragments.DiaryFragment;
import com.java.boris.tgw.fragments.MainFragment;
import com.java.boris.tgw.fragments.SettingsFragment;
import com.java.boris.tgw.fragments.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Polar polar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_main);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;

        switch (id){
            case R.id.nav_main:
                fragment = new MainFragment();
                break;
            case R.id.nav_diary:
                fragment = new DiaryFragment();
                break;
            case R.id.nav_statistics:
                fragment = new StatisticsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
        }

        if(fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }








































    private void setTestChart(){
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        polar = AnyChart.polar();

        createSeries(new CustomDataEntry("Здоровье", 9, "green", 1));
        createSeries(new CustomDataEntry("Учеба", 6, "blue", 2));
        createSeries(new CustomDataEntry("Отношения", 2, "pink", 3));
        createSeries(new CustomDataEntry("Работа", 7, "gray", 4));
        createSeries(new CustomDataEntry("Хобби", 10, "yellow", 5));

        polar.yGrid(false);

        polar.yScale()
                .maximum(10)
                .minimum(0);

        polar.defaultSeriesType(PolarSeriesType.COLUMN)
                .yAxis(false)
                .xScale(ScaleTypes.ORDINAL);

        anyChartView.setChart(polar);

        polar.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value", "color", "id"}) {
            @Override
            public void onClick(Event event) {
                String x =  event.getData().get("x");
                Integer value = Integer.parseInt(event.getData().get("value"));
                event.getData().get("color");
                Integer id = Integer.parseInt(event.getData().get("id"));

                Toast.makeText(MainActivity.this, x + " " + value + " " + id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CustomDataEntry extends DataEntry {
        CustomDataEntry(String x, Number value, String color, Number id) {
            setValue("x", x);
            setValue("value", value);
            setValue("color", color);
            setValue("id", id);
        }
    }

    private void createSeries(CustomDataEntry dataEntry) {
        List<DataEntry> data = new ArrayList<>();
        data.add(dataEntry);

        Column series = polar.column(data);
        series.color((String) dataEntry.getValue("color"));
        series.pointWidth("99%");
    }
}
