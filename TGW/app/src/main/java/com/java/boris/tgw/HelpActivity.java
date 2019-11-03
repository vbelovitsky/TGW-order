package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class HelpActivity extends AppCompatActivity {

    static Random random = new Random();
    DBHelper dbHelper;
    String LOG_TAG = "db Log: ";

    ArrayList<String> data;

    // Текст советов для улучшения жизни по базовым аспектам
    String[][] helpData = new String[][] {
            {"Завести полового партнёра", "Уделять партнёру больше времени", "Уделить родителям больше времени", "Уделить детям больше времени"},
            {"Купить новый телефон", "Обновить гардероб", "Переехать", "Купить машину"},
            {"Заняться спортом", "Прочитать книгу", "Сходить в кино", "Послушать новый альбом"},
            {"Встретиться с другом", "Устроить праздничный вечер", "Познакомиться с кем-нибудь", "Купить другу подарок"},
            {"Добиться повышения", "Закрыть квартал", "Повысить квалификацию", "Провести коуч-анализ предприятия"},
            {"Заняться спортом", "Сходить к врачу", "Медитация", "День детоксикации"},};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        data = extractData(database);

        // Вставляем данные из бд в удобный список
        ArrayList<CategoryHelp> categoryHelps = new ArrayList<>();
        for (int i = 0; i < data.size(); i++){
            String[] line = data.get(i).split("@");

            categoryHelps.add(new CategoryHelp(Integer.parseInt(line[0]), line[1], Integer.parseInt(line[2])));

        }

        sortHelpArray(categoryHelps);

        int moda = findModa(categoryHelps);
        int mediana = categoryHelps.get(categoryHelps.size()/2).value;

        // Если мода найдена, то значение нормы равно моде, иначе медиане
        int norma = moda != -1? moda : mediana;

        // Проверяем отхождение от нормы от 5 до 0
        ArrayList<CategoryHelp> abnormal = new ArrayList<>();
        String label = "Улучшайте эти аспекты:";
        for(int i = 5; i >= 0; i--){
            for(int j = 0; j < categoryHelps.size(); j++){
                if(categoryHelps.get(j).value <= norma - i){
                    abnormal.add(categoryHelps.get(j));
                }
            }
            if(abnormal.size() > 0){
                if(i == 5 || i == 4) label = "Срочно исправляйте эти аспекты:";
                break;
            }
        }

        // Массив названий аспектов, отклоняющихся от нормы
        String[] abnormalNames = new String[abnormal.size()];
        for(int i = 0; i < abnormal.size(); i++){
            abnormalNames[i] = abnormal.get(i).name;
        }

        // Массив советов для улучшения жизни
        ArrayList<String> advices = new ArrayList<>();
        for(int i = 0; i < abnormal.size(); i++){
            // Если категория относится к базовым тогда для нее генерируется совет
            if(abnormal.get(i).id <= helpData.length){
                advices.add(helpData[abnormal.get(i).id - 1][random.nextInt(4)]);
            }
        }

        // Если плохие категории не относятся к базовым, то отображаюся советы по умолчанию
        if (advices.size() == 0){
            advices.add("Подумайте, как исправить аспекты выше");
            advices.add("Создайте для себя цели в дневнике!");
        }

        TextView categoryLabel = findViewById(R.id.help_category_label);
        categoryLabel.setText(label);

        ListView categoryList = findViewById(R.id.help_categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, abnormalNames);
        categoryList.setAdapter(categoryAdapter);


        ListView adviceList = findViewById(R.id.help_advices);
        ArrayAdapter<String> adviceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, advices);
        adviceList.setAdapter(adviceAdapter);


    }


    // Метод для нахождения моды
    private int findModa(ArrayList<CategoryHelp> categoryHelps){
        // Массив повторяющихся чисел
        int [] duplicates = new int[11];
        for(int i = 0; i < categoryHelps.size(); i++){
            duplicates[categoryHelps.get(i).value]++;
        }

        int maxCount = -1;
        for(int i = 0; i < duplicates.length; i++){
            if(duplicates[i] > maxCount) maxCount = duplicates[i];
        }
        // Нет повторяющихся чисел
        if(maxCount <= 1) return -1;

        // Находим наибольшое число из частовстречающихся (мода)
        for(int i = duplicates.length -1; i >=0; i--){
            if(duplicates[i] == maxCount) return i;
        }

        return -1;
    }

    // Сортировка массива
    private void sortHelpArray(ArrayList<CategoryHelp> categoryHelps){
        Collections.sort(categoryHelps, new Comparator<CategoryHelp>() {
            @Override
            public int compare(CategoryHelp lhs, CategoryHelp rhs) {
                return Integer.compare(lhs.value, rhs.value);
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

    protected class CategoryHelp{
        public int id;
        public String name;
        public int value;

        CategoryHelp(int id, String name, int value){
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }
}
