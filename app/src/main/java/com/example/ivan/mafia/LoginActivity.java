package com.example.ivan.mafia;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    int minNameChars = 3;
    int minPasswordChars = 3;

    EditText editName, editPassword;
    Button btnSignIn;
    TextView textViewRegister;

    Handler handler;

    SharedPreferences sPref;

    Mafia mafia = Mafia.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_pass);

        editName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textViewRegister = findViewById(R.id.textViewRegister);

        sPref = getSharedPreferences("prefs", MODE_PRIVATE);

        btnSignIn.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String password = editPassword.getText().toString();
            if (name.length() < minNameChars)
                Toast.makeText(this, "Имя должно быть больше " + minNameChars + " символов", Toast.LENGTH_SHORT).show();
            else if (password.length() < minPasswordChars)
                Toast.makeText(this, "Пароль должен быть больше " + minPasswordChars + " символов", Toast.LENGTH_SHORT).show();
            else mafia.signIn(handler, name, password);
        });

        textViewRegister.setOnClickListener(view -> {

        });

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.what == 11) {
                if (message.equals("OK")) {
                    setResult(1);
                    finish();
                }
                else Toast.makeText(this, "Ошибка авторизации: " + message, Toast.LENGTH_SHORT).show();
            }
            return true;
        });


        // setPass(getSharedPreferences("prefs", MODE_PRIVATE), "12345");

    }

    public static void saveNameAndPassword(SharedPreferences sPrefs, String name, String password) {
        SharedPreferences.Editor ePref = sPrefs.edit();
        ePref.putString("name", name);
        ePref.putString("password", password);
        ePref.apply();
    }
}
