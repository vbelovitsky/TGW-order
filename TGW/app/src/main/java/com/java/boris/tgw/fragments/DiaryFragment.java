package com.java.boris.tgw.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.GoalActivity;
import com.java.boris.tgw.R;

import java.util.ArrayList;

public class DiaryFragment extends Fragment {

    ListView goalListView;
    private String[] goalArray = {"Плавать утром", "Здоровое питание", "Найти девушку", "Заработать денег", "Научиться рисовать",
            "Сдать на права", "Прогулки", "Дарить цветы", "Выучить новый ЯП", "Игра на гитаре"};
    private String[] categoryArray = {"Здоровье", "Здоровье", "Отношения", "Работа", "Хобби",
            "Работа", "Здоровье", "Отношения", "Работа", "Хобби"};
    private int[] colorArray = {0xFF93E92B, 0xFF93E92B, 0xFFFF7474, 0, 0xFFFD8908,
            0xFF93E92B, 0xFF93E92B, 0xFFFF7474, 0, 0xFFFD8908};
    private boolean[] isCompletedArray = {false, true, true, false, true,
            false, true, true, false, true};

    private static ArrayList<Goal> goalListArray = new ArrayList<>();


    private DBHelper dbHelper;
    private String LOG_TAG = "dbLog";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_diary);

        // ListView, Adapter, custom Item, AddButton, Db, AddActivity

        goalListView = getActivity().findViewById(R.id.goals_list);
        FloatingActionButton addGoalButton = getActivity().findViewById(R.id.add_goal_button);

        // Заполнение массива для листвью из бд
        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        for(int i = 0; i < goalArray.length; i++){
            goalListArray.add(new Goal(goalArray[i], categoryArray[i], isCompletedArray[i], colorArray[i]));
        }

        ArrayAdapter<Goal> adapter = new GoalAdapter(getActivity());

        goalListView.setAdapter(adapter);


        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalActivity.class);
                startActivity(intent);
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
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








    private class Goal{
        public final String goalName;
        public final String categoryName;
        public final boolean isCompleted;
        public final int color;

        Goal(String goalName, String categoryName, boolean isCompleted, int color){
            this.goalName = goalName;
            this.categoryName = categoryName;
            this.isCompleted = isCompleted;
            this.color = color;
        }
    }

    private class GoalAdapter extends ArrayAdapter<Goal> {

        GoalAdapter(Context context){
            super(context, R.layout.custom_goal_item, goalListArray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Goal goal = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_goal_item, null);
            }

            ((TextView)convertView.findViewById(R.id.goal_text)).setText(goal.goalName);
            ((TextView)convertView.findViewById(R.id.goal_category)).setText(goal.categoryName);
            ((CheckBox)convertView.findViewById(R.id.goal_completed)).setChecked(goal.isCompleted);
            convertView.findViewById(R.id.goal_category).setBackgroundColor(goal.color);

            return convertView;
        }
    }
}
