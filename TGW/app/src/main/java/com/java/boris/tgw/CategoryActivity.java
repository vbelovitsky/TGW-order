package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //Инициалицируем элементы интерфейса
        TextView categoryLabel = findViewById(R.id.category_label);
        EditText editCategoryName = findViewById(R.id.edit_category_name);
        SeekBar editCategoryValue = findViewById(R.id.edit_category_value);
        ColorSeekBar editCategoryColor = findViewById(R.id.edit_category_color);
        Button completeButton = findViewById(R.id.category_complete_button);

        id = getIntent().getIntExtra("id", -1);


        categoryLabel.setText(id == -1?"Создайте категорию:":"Измените категорию:");
        editCategoryName.setText(id == -1?"":getIntent().getStringExtra("xName"));
        editCategoryValue.setProgress(id == -1?0:getIntent().getIntExtra("value", 1) - 1);

        editCategoryColor.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                isColorChanged = true;
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Добавляем новую/измененную категорию в бд
                if(id == -1){

                }else{

                }
            }
        });
    }
}
