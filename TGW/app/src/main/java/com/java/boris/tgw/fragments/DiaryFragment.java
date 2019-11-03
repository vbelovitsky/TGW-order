package com.java.boris.tgw.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.boris.tgw.DBHelper;
import com.java.boris.tgw.GoalActivity;
import com.java.boris.tgw.R;

import java.util.ArrayList;

public class DiaryFragment extends Fragment {

    ListView goalListView;

    private ArrayList<Goal> goalListArray = new ArrayList<>();

    private DBHelper dbHelper;
    private String LOG_TAG = "dbLog";

    private boolean refresh = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.menu_diary);

        TextView isEmpty = getActivity().findViewById(R.id.diary_empty);

        goalListView = getActivity().findViewById(R.id.goals_list);
        FloatingActionButton addGoalButton = getActivity().findViewById(R.id.add_goal_button);

        // Заполнение массива для листвью из бд
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> dbGoalData = extractData(database);

        for (int i = 0; i < dbGoalData.size(); i++) {
            String[] lineData = dbGoalData.get(i).split("@");
            goalListArray.add(new Goal(Integer.parseInt(lineData[0]), lineData[1],
                    Integer.parseInt(lineData[2]), lineData[5], Integer.parseInt(lineData[4]), Color.parseColor(lineData[7]), Integer.parseInt(lineData[6])));
        }
        if(goalListArray.size() != 0) isEmpty.setVisibility(View.INVISIBLE);
        else isEmpty.setVisibility(View.VISIBLE);

        ArrayAdapter<Goal> adapter = new GoalAdapter(getActivity());

        goalListView.setAdapter(adapter);

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalActivity.class);
                intent.putExtra("id", -1);
                startActivity(intent);
                refresh = !refresh;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refresh) {
            goalListArray.clear();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
            refresh = !refresh;
        }
    }

    // region Extract data
    private ArrayList<String> extractData(SQLiteDatabase database) {
        ArrayList<String> dataLines = new ArrayList<>();

        Cursor cursor;
        cursor = database.query(DBHelper.TABLE_GOALS + " join " + DBHelper.TABLE_CATEGORY + " on " + DBHelper.KEY_GOALS_CATEGORY + " = " + DBHelper.KEY_ID,
                null, null, null, null, null, null);

        // Данные в массиве обновляются по ссылке
        logCursor(cursor, dataLines);

        cursor.close();
        dbHelper.close();

        return dataLines;
    }

    private void logCursor(Cursor cursor, ArrayList<String> _dataLines) {
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
// endregion

    // Класс для хранения всех составляющих цели
    private class Goal {
        public final int goalId;
        public final String goalName;
        public final int isCompleted;
        public final String categoryName;
        public final int categoryId;
        public final int color;
        public final int categoryValue;

        Goal(int goalId, String goalName, int isCompleted, String categoryName, int categoryId, int color, int categoryValue) {
            this.goalId = goalId;
            this.goalName = goalName;
            this.isCompleted = isCompleted;
            this.categoryName = categoryName;
            this.categoryId = categoryId;
            this.color = color;
            this.categoryValue = categoryValue;
        }
    }

    // Адаптер для списка целей
    private class GoalAdapter extends ArrayAdapter<Goal> {

        GoalAdapter(Context context) {
            super(context, R.layout.custom_goal_item, goalListArray);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Goal goal = getItem(position);
            final GoalHolder holder;


            if (convertView == null) {
                holder = new GoalHolder();
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_goal_item, null);

                holder.goalName = convertView.findViewById(R.id.goal_text);
                holder.categoryName = convertView.findViewById(R.id.goal_category);
                holder.isCompleted = convertView.findViewById(R.id.goal_completed);

                convertView.setTag(holder);
            } else {
                holder = (GoalHolder) convertView.getTag();
            }

            holder.goalName.setText(goal.goalName);
            holder.categoryName.setText(goal.categoryName);
            holder.isCompleted.setChecked(goal.isCompleted != 0);
            holder.categoryName.setBackgroundColor(goal.color);

            holder.isCompleted.setTag(new int[]{goal.goalId, goal.categoryId});
            holder.goalName.setTag(new String[]{String.valueOf(goal.goalId), goal.goalName});

            // Нажатие на чекбокс цели
            holder.isCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int[] ids = (int[]) holder.isCompleted.getTag();

                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    ContentValues goalValues = new ContentValues();
                    ContentValues categoryValues = new ContentValues();

                    // Если цель была не выполнена, то значение категории увеличивается на 1
                    if (holder.isCompleted.isChecked()) {
                        goalValues.put(DBHelper.KEY_GOALS_COMPLETED, 1);
                        database.update(DBHelper.TABLE_GOALS, goalValues, DBHelper.KEY_GOALS_ID + "=?", new String[]{Integer.toString(ids[0])});


                        categoryValues.put(DBHelper.KEY_VALUE, goalListArray.get(position).categoryValue >= 10 ? 10 : goalListArray.get(position).categoryValue + 1);
                        // Поздавление пользователя, если значение категории стало равным 10
                        if(goalListArray.get(position).categoryValue + 1 >= 10){
                            Toast.makeText(getActivity(), "Ура! Вы достигли совершенства в категории " + goalListArray.get(position).categoryName + "!", Toast.LENGTH_SHORT).show();
                        }
                        database.update(DBHelper.TABLE_CATEGORY, categoryValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(ids[1])});
                    }
                    // иначе - уменьшается на 1
                    else {
                        goalValues.put(DBHelper.KEY_GOALS_COMPLETED, 0);
                        database.update(DBHelper.TABLE_GOALS, goalValues, DBHelper.KEY_GOALS_ID + "=?", new String[]{Integer.toString(ids[0])});

                        categoryValues.put(DBHelper.KEY_VALUE, goalListArray.get(position).categoryValue - 1);
                        database.update(DBHelper.TABLE_CATEGORY, categoryValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(ids[1])});
                    }

                    dbHelper.close();
                }
            });

            holder.goalName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] data = (String[]) holder.goalName.getTag();
                    Intent intent = new Intent(getActivity(), GoalActivity.class);
                    intent.putExtra("id", Integer.parseInt(data[0]));
                    intent.putExtra("name", data[1]);
                    startActivity(intent);
                    refresh = !refresh;
                }
            });

            return convertView;
        }

        private class GoalHolder {
            protected TextView goalName;
            protected TextView categoryName;
            protected CheckBox isCompleted;
        }
    }
}
