package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.java.boris.tgw.fragments.MainFragment;

import java.util.ArrayList;

public class GoalActivity extends ListActivity {

    EditText editGoal;
    ListView listView;

    String[] categoryList;
    int[] categoryIdArray;

    DBHelper dbHelper;
    String LOG_TAG = "goal tag";

    ContentValues contentValues;
    SQLiteDatabase database;

    int selectedCategory = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        editGoal = findViewById(R.id.edit_goal_text);
        listView = getListView();
        Button createGoalButton = findViewById(R.id.goal_complete_button);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        ArrayList<String> dataLines = extractData(database);

        categoryList = new String[dataLines.size()];
        categoryIdArray = new int[dataLines.size()];
        for(int i = 0; i < dataLines.size(); i++){
            String[] dataLine = dataLines.get(i).split("@");
            categoryList[i] = dataLine[1];
            categoryIdArray[i] = Integer.parseInt(dataLine[0]);
        }

        setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1, categoryList));


        contentValues = new ContentValues();
        createGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editGoal.length() == 0) {
                    editGoal.setError("Введите, пожалуйста, название!");
                } else if (editGoal.getText().toString().contains("@") || editGoal.getText().toString().contains("\n")) {
                    editGoal.setError("Недопустимые символы");
                } else if(selectedCategory < 0){

                } else {
                    contentValues.put(DBHelper.KEY_GOALS_TEXT, editGoal.getText().toString());
                    contentValues.put(DBHelper.KEY_GOALS_COMPLETED, 0);
                    contentValues.put(DBHelper.KEY_GOALS_CATEGORY, categoryIdArray[selectedCategory]);
                    database.insert(DBHelper.TABLE_GOALS, null, contentValues);
                    dbHelper.close();
                    finish();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = position;
            }
        });

    }


    private ArrayList<String> extractData(SQLiteDatabase database){
        ArrayList<String> dataLines = new ArrayList<>();

        Cursor cursor;
        cursor = database.query(DBHelper.TABLE_CATEGORY, null, null, null, null, null, null);

        // Данные в массиве обновляются по ссылке
        logCursor(cursor, dataLines);

        cursor.close();

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
}
