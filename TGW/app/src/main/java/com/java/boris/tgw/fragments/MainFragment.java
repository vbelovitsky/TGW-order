package com.java.boris.tgw.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.java.boris.tgw.CategoryActivity;
import com.java.boris.tgw.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private Polar polar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.menu_main);

        setTestChart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    private void setTestChart(){
        AnyChartView anyChartView = getActivity().findViewById(R.id.any_chart_fragment);

        polar = AnyChart.polar();

        createSeries(new CustomDataEntry("Здоровье", 9, "green", 1));
        createSeries(new CustomDataEntry("Учеба", 6, "blue", 2));
        createSeries(new CustomDataEntry("Отношения", 5, "pink", 3));
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

            int counter = 0;

            @Override
            public void onClick(Event event) {


                String x =  event.getData().get("x");
                Integer value = Integer.parseInt(event.getData().get("value"));
                event.getData().get("color");
                Integer id = Integer.parseInt(event.getData().get("id"));

                Toast.makeText(getActivity(), x + " " + value + " " + id, Toast.LENGTH_SHORT).show();

                if((counter & 1) == 0) {
                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
                    startActivity(intent);
                }
                counter++;

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
