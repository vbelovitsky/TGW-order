package com.java.boris.tgw.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.R;

public class SettingsFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_settings);

        Button dropButton = getActivity().findViewById(R.id.drop_db_button);

        // Нажатие на кнопку - очистка бд
        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getActivity());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.onUpgrade(database, 1, 1);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}