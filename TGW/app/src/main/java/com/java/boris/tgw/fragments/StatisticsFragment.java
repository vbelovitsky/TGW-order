package com.java.boris.tgw.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anychart.AnyChartView;
import com.java.boris.tgw.R;

public class StatisticsFragment extends Fragment {

    AnyChartView statisticChart;
    int currentChart = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_statistics);

        statisticChart = getActivity().findViewById(R.id.statistics_chart);
        Button prevButton = getActivity().findViewById(R.id.button_prev);
        Button nextButton = getActivity().findViewById(R.id.button_next);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }
}