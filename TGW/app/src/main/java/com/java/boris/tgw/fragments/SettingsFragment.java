package com.java.boris.tgw.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.FirstRunActivity;
import com.java.boris.tgw.MainActivity;
import com.java.boris.tgw.R;

public class SettingsFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_settings);

        Button dropButton = getActivity().findViewById(R.id.drop_db_button);
        Button dropStatisticsButton = getActivity().findViewById(R.id.drop_statistics_button);

        // Нажатие на кнопку - очистка бд
        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getActivity());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.onUpgrade(database, 1, 1);

                // Будет опять экран первого запуска приложения
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("is_first_run", Boolean.FALSE);
                edit.commit();

                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });

        // Нажатие на кнопку - очистка бд
        dropStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getActivity());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.dropStatistics(database);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}