package com.example.ivan.mafia;

import android.content.Intent;
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
    TextView textRegister, textNoAccount;

    Handler handler;

    SharedPreferences sPref;

    String name, password;

    Mafia mafia = Mafia.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_pass);

        editName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textRegister = findViewById(R.id.textRegister);
        textNoAccount = findViewById(R.id.textNoAccount);

        sPref = getSharedPreferences("prefs", MODE_PRIVATE);

        signInMode();
    }

    private void signInMode() {
        textNoAccount.setText("Нет аккаунта? ");
        textRegister.setText("Зарегистрироваться");
        textRegister.setOnClickListener(textReg -> registerMode());

        btnSignIn.setText("Войти");
        btnSignIn.setOnClickListener(view -> {
            name = editName.getText().toString();
            password = editPassword.getText().toString();
            if (name.length() < minNameChars)
                Toast.makeText(this, "Имя должно быть больше " + minNameChars + " символов", Toast.LENGTH_SHORT).show();
            else if (password.length() < minPasswordChars)
                Toast.makeText(this, "Пароль должен быть больше " + minPasswordChars + " символов", Toast.LENGTH_SHORT).show();
            else mafia.signIn(handler, name, password);
        });

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.what == 11) {
                if (message.equals("OK")) {
                    saveNameAndPassword(getSharedPreferences("prefs", MODE_PRIVATE), name, password);
                    Toast.makeText(this, "Вы успешно вошли в аккаунт!", Toast.LENGTH_SHORT).show();
                    setResult(1, new Intent().putExtra("name", name));
                    finish();
                }
                else Toast.makeText(this, "Ошибка авторизации: " + message, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void registerMode() {
        textNoAccount.setText("Уже зарегистрированы? ");
        textRegister.setText("Войти");
        textRegister.setOnClickListener(textRegister2 -> signInMode());

        btnSignIn.setText("Зарегистрироваться");
        btnSignIn.setOnClickListener(btnSignIn -> {
            name = editName.getText().toString();
            password = editPassword.getText().toString();
            if (name.length() < minNameChars)
                Toast.makeText(this, "Имя должно быть больше " + minNameChars + " символов", Toast.LENGTH_SHORT).show();
            else if (password.length() < minPasswordChars)
                Toast.makeText(this, "Пароль должен быть больше " + minPasswordChars + " символов", Toast.LENGTH_SHORT).show();
            else mafia.register(handler, name, password);
        });

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.what == 10) {
                if (message.equals("Аккаунт успешно создан!")) {
                    saveNameAndPassword(getSharedPreferences("prefs", MODE_PRIVATE), name, password);
                    Toast.makeText(this, "Вы успешно зарегистрировались!", Toast.LENGTH_SHORT).show();
                    setResult(1, new Intent().putExtra("name", name));
                    finish();
                }
                else Toast.makeText(this, "Ошибка регистрации: " + message, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    public static void saveNameAndPassword(SharedPreferences sPrefs, String name, String password) {
        SharedPreferences.Editor ePref = sPrefs.edit();
        ePref.putString("name", name);
        ePref.putString("password", password);
        ePref.apply();
    }
}
