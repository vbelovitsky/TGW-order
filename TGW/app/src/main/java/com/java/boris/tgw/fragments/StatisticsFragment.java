package com.java.boris.tgw.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.R;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    AnyChartView statisticChart;
    int currentChart = 0;

    DBHelper dbHelper;
    ArrayList<String> statisticsData;
    private String LOG_TAG = "dbLog";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_statistics);

        statisticChart = getActivity().findViewById(R.id.statistics_chart);
        Button prevButton = getActivity().findViewById(R.id.button_prev);
        Button nextButton = getActivity().findViewById(R.id.button_next);

        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        statisticsData = extractData(database);

        setLineChart(statisticChart);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    private ArrayList<String> extractData(SQLiteDatabase database){
        ArrayList<String> dataLines = new ArrayList<>();

        Cursor cursor;
        cursor = database.query(DBHelper.TABLE_STATISTICS, null, null, null, null, null, null);

        // Данные в массиве обновляются по ссылке
        logCursor(cursor, dataLines);

        cursor.close();
        dbHelper.close();

        return  dataLines;
    }

    private void logCursor(Cursor cursor, ArrayList<String> _dataLines){
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                StringBuilder stringBuilder = new StringBuilder();
                do {
                    stringBuilder.setLength(0);
                    for (String cn : cursor.getColumnNames()) {
                        stringBuilder.append(cursor.getString(cursor.getColumnIndex(cn))).append("@");
                    }
                    Log.d(LOG_TAG, stringBuilder.toString());
                    _dataLines.add(stringBuilder.toString());
                } while (cursor.moveToNext());
            }
        } else Log.d(LOG_TAG, "Cursor is null");
    }



    private void setLineChart(AnyChartView statisticChart){
        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("График изменения всех аспектов жизни");
        cartesian.yAxis(0).title("Удовлетворенность аспектом");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();


        String[] categories = statisticsData.get(statisticsData.size() - 1).split("@")[2].split("!");
        String[] colors = statisticsData.get(statisticsData.size() - 1).split("@")[3].split("!");
        for(int i = 0; i < statisticsData.size(); i++){
            String[] lineData = statisticsData.get(i).split("@");

            String date = lineData[1];
            String[] values = lineData[3].split("!");

            seriesData.add(new CustomDataEntry(date, values));
        }


        Set set = Set.instantiate();
        set.data(seriesData);

        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series = cartesian.line(seriesMapping);
        series.name(categories[0]);
        series.hovered().markers().enabled(true);
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        for(int i = 1; i < categories.length; i++){
            seriesMapping = set.mapAs("{ x: 'x', value: 'value" + (i+1) + "' }");
            series = cartesian.line(seriesMapping);
            series.name(categories[i]);
            series.hovered().markers().enabled(true);
            series.hovered().markers()
                    .type(MarkerType.CIRCLE)
                    .size(4d);
            series.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5d)
                    .offsetY(5d);
        }


        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        statisticChart.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, String[] values) {
            super(x, Integer.parseInt(values[0]));
            for(int i = 1; i < values.length; i++){
                setValue("value" + (i+1), Integer.parseInt(values[i]));
            }
        }

    }
}