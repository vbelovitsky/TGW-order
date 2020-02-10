package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class FirstRunActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        final SeekBar loveBar = findViewById(R.id.edit_love);
        final SeekBar wealthBar = findViewById(R.id.edit_wealth);
        final SeekBar hobbyBar = findViewById(R.id.edit_hobby);
        final SeekBar friendsBar = findViewById(R.id.edit_friends);
        final SeekBar workBar = findViewById(R.id.edit_work);
        final SeekBar healthBar = findViewById(R.id.edit_health);

        Button completeButton = findViewById(R.id.complete_first_run_button);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues loveValues = new ContentValues();
                loveValues.put(DBHelper.KEY_VALUE, loveBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, loveValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(1)});

                ContentValues wealthValues = new ContentValues();
                wealthValues.put(DBHelper.KEY_VALUE, wealthBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, wealthValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(2)});

                ContentValues hobbyValues = new ContentValues();
                hobbyValues.put(DBHelper.KEY_VALUE, hobbyBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, hobbyValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(3)});

                ContentValues friendsValues = new ContentValues();
                friendsValues.put(DBHelper.KEY_VALUE, friendsBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, friendsValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(4)});

                ContentValues workValues = new ContentValues();
                workValues.put(DBHelper.KEY_VALUE, workBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, workValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(5)});

                ContentValues healthValues = new ContentValues();
                healthValues.put(DBHelper.KEY_VALUE, healthBar.getProgress() + 1);
                database.update(DBHelper.TABLE_CATEGORY, healthValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(6)});

                Toast.makeText(FirstRunActivity.this, "Добро пожаловать!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(FirstRunActivity.this, MainActivity.class));
                dbHelper.close();
            }
        });

    }
}
