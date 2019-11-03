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
import com.java.boris.tgw.HelpActivity;
import com.java.boris.tgw.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private Polar polar;
    private DBHelper dbHelper;
    private String LOG_TAG = "dbLog";
    private int categoryCount = 5;
    private boolean isReturnedFromCategories = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.menu_main);

        // Инициализируем переменные для работы с бд
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Достаем данные из бд и сразу вызываем метод создания колеса
        ArrayList<String> dataLines = extractData(database);
        categoryCount = dataLines.size();
        setPolarChart(dataLines);

        // Обработчик нажатия на кнопку добавления категории
        FloatingActionButton addCategoryButton = getActivity().findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryCount <= 10) {
                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
                    intent.putExtra("id", -1);
                    isReturnedFromCategories = true;
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "Может быть не более 10 категорий", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Обработчик нажатия на кнопку для вызова помощника
        FloatingActionButton helperButton = getActivity().findViewById(R.id.helper_button);
        helperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private ArrayList<String> extractData(SQLiteDatabase database){
        ArrayList<String> dataLines = new ArrayList<>();

        Cursor cursor;
        cursor = database.query(DBHelper.TABLE_CATEGORY, null, null, null, null, null, null);

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

    private void setPolarChart(ArrayList<String> dataLines){
        AnyChartView anyChartView = getActivity().findViewById(R.id.any_chart_fragment);
        anyChartView.setProgressBar(getActivity().findViewById(R.id.progress_polar));
        polar = AnyChart.polar();


        // Заполняем колесо информацией
        for(int i = 0; i < dataLines.size(); i++){
            String[] dataLine = dataLines.get(i).split("@");
            createSeries(new CustomDataEntry(Integer.parseInt(dataLine[0]),
                    dataLine[1], Integer.parseInt(dataLine[2]), dataLine[3]));
        }

        // Настройка внешнего вида колеса
        polar.yGrid(false);
        polar.yScale()
                .maximum(10)
                .minimum(0);
        polar.defaultSeriesType(PolarSeriesType.COLUMN)
                .yAxis(false)
                .xScale(ScaleTypes.ORDINAL);
        anyChartView.setChart(polar);

        // Обработчик нажатия на категорию
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
                    isReturnedFromCategories = true;
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
        series.pointWidth("100%");
    }
}
