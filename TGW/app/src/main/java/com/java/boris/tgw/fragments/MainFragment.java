package com.java.boris.tgw.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Polar;
import com.anychart.core.polar.series.Column;
import com.anychart.enums.PolarSeriesType;
import com.anychart.enums.ScaleTypes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.boris.tgw.CategoryActivity;
import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private Polar polar;
    private DBHelper dbHelper;
    private String LOG_TAG = "dbLog";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.menu_main);

        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

//        Cursor cursor;
//        cursor = database.query(DBHelper.TABLE_CATEGORY, null, null, null, null, null, null);
//        logCursor(cursor);
//        cursor.close();
//
//        dbHelper.close();

        setPolarChart(database);

        FloatingActionButton addCategoryButton = getActivity().findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("id", -1);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    private void logCursor(Cursor cursor, ArrayList<String> _dataLines){
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                StringBuilder stringBuilder = new StringBuilder();
                do {
                    stringBuilder.setLength(0);
                    for (String cn : cursor.getColumnNames()) {
                        stringBuilder.append(cursor.getString(cursor.getColumnIndex(cn))).append(" ");
                    }
                    Log.d(LOG_TAG, stringBuilder.toString());
                    _dataLines.add(stringBuilder.toString());
                } while (cursor.moveToNext());
            }
        } else Log.d(LOG_TAG, "Cursor is null");
    }


    private void setPolarChart(SQLiteDatabase database){
        AnyChartView anyChartView = getActivity().findViewById(R.id.any_chart_fragment);

        polar = AnyChart.polar();


//        createSeries(new CustomDataEntry("Здоровье", 9, "green", 1));
//        createSeries(new CustomDataEntry("Учеба", 6, "blue", 2));
//        createSeries(new CustomDataEntry("Отношения", 5, "pink", 3));
//        createSeries(new CustomDataEntry("Работа", 7, "gray", 4));
//        createSeries(new CustomDataEntry("Хобби", 10, "yellow", 5));

        Cursor cursor;
        cursor = database.query(DBHelper.TABLE_CATEGORY, null, null, null, null, null, null);

        ArrayList<String> dataLines = new ArrayList<>();
        logCursor(cursor, dataLines);
        for(int i = 0; i < dataLines.size(); i++){
            String[] dataLine = dataLines.get(i).split(" ");
            createSeries(new CustomDataEntry(Integer.parseInt(dataLine[0]),
                    dataLine[1], Integer.parseInt(dataLine[2]), dataLine[3]));
        }

        cursor.close();
        dbHelper.close();

        polar.yGrid(false);
        polar.yScale()
                .maximum(10)
                .minimum(0);
        polar.defaultSeriesType(PolarSeriesType.COLUMN)
                .yAxis(false)
                .xScale(ScaleTypes.ORDINAL);
        anyChartView.setChart(polar);

        polar.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value", "color", "id"}) {
            boolean isAble = true;
            @Override
            public void onClick(Event event) {


                String x =  event.getData().get("x");
                Integer value = Integer.parseInt(event.getData().get("value"));
                event.getData().get("color");
                Integer id = Integer.parseInt(event.getData().get("id"));

                if(isAble) {
                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("xName", x);
                    intent.putExtra("value", value);
                    startActivity(intent);
                }
                isAble = !isAble;
            }
        });
    }

    private class CustomDataEntry extends DataEntry {
        CustomDataEntry(Number id, String x, Number value, String color) {
            setValue("id", id);
            setValue("x", x);
            setValue("value", value);
            setValue("color", color);
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
