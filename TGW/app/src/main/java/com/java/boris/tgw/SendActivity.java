package com.java.boris.tgw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        final EditText editText = findViewById(R.id.edit_send_text);
        Button button = findViewById(R.id.send_complete_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.length() != 0){
                    Toast.makeText(SendActivity.this, "Ваше сообщение успешно отправлено!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SendActivity.this, MainActivity.class));
                }
                else{
                    editText.setError("Сообщение не должно быть пустым!");
                }
            }
        });
    }

}
