package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.divyanshu.colorseekbar.ColorSeekBar;

public class CategoryActivity extends AppCompatActivity {

    private boolean isColorChanged = false;
    private int id;
    private DBHelper dbHelper;
    private SQLiteDatabase database;


    private EditText editCategoryName;
    private SeekBar editCategoryValue;
    private ColorSeekBar editCategoryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Инициалицазия переменных для работы с бд
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        //Инициалицируем элементы интерфейса
        TextView categoryLabel = findViewById(R.id.category_label);
        editCategoryName = findViewById(R.id.edit_category_name);
        editCategoryValue = findViewById(R.id.edit_category_value);
        editCategoryColor = findViewById(R.id.edit_category_color);
        Button completeButton = findViewById(R.id.category_complete_button);
        final Button deleteButton = findViewById(R.id.category_delete_button);

        id = getIntent().getIntExtra("id", -1);

        categoryLabel.setText(id == -1 ? "Создайте категорию:" : "Измените категорию:");
        editCategoryName.setText(id == -1 ? "" : getIntent().getStringExtra("xName"));
        editCategoryValue.setProgress(id == -1 ? 0 : getIntent().getIntExtra("value", 1) - 1);

        editCategoryColor.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                isColorChanged = true;
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues contentValues = new ContentValues();

                // Валидация ввода
                if (editCategoryName.length() == 0) {
                    editCategoryName.setError("Введите, пожалуйста, название!");
                } else if (editCategoryName.getText().toString().contains("@") || editCategoryName.getText().toString().contains("\n")) {
                    editCategoryName.setError("Недопустимые символы");
                } else {
                    //Добавление новой/измененной категории в бд
                    if (id == -1) {
                        contentValues.put(DBHelper.KEY_NAME, editCategoryName.getText().toString());
                        contentValues.put(DBHelper.KEY_VALUE, editCategoryValue.getProgress() + 1);
                        contentValues.put(DBHelper.KEY_COLOR, String.format("#%06X", (0xFFFFFF & editCategoryColor.getColor())));

                        database.insert(DBHelper.TABLE_CATEGORY, null, contentValues);
                        finishAffinity();
                        startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                    } else {
                        contentValues.put(DBHelper.KEY_NAME, editCategoryName.getText().toString());
                        contentValues.put(DBHelper.KEY_VALUE, editCategoryValue.getProgress() + 1);

                        if (isColorChanged)
                            contentValues.put(DBHelper.KEY_COLOR, String.format("#%06X", (0xFFFFFF & editCategoryColor.getColor())));

                        database.update(DBHelper.TABLE_CATEGORY, contentValues, DBHelper.KEY_ID + "=?", new String[]{Integer.toString(id)});
                        finishAffinity();
                        startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                    }
                }
            }
        });

        if(id != -1) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.delete(DBHelper.TABLE_CATEGORY, DBHelper.KEY_ID + "=" + id, null);
                    finishAffinity();
                    startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                }
            });
        }
    }
}
