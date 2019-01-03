package com.example.ivan.mafia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    public static final int myToken = 421928;

    public static Handler handler;
    SharedPreferences sPrefs;
    Mafia mafia;

    Button btnCreate, btnConnect, btnSettings, btnAbout;
    TextView textAccount, textSignOut;
    Intent intentLogin, intentPlay;

    String savedName, savedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAccount = findViewById(R.id.textAccount);
        textSignOut = findViewById(R.id.textSignOut);

        textAccount.setText(R.string.account_check);
        textSignOut.setVisibility(View.GONE);

        intentLogin = new Intent(this, LoginActivity.class);
        intentPlay = new Intent(this, PlayActivity.class);


        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                textAccount.setText(R.string.try_again);
                textAccount.setOnClickListener(view -> {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    mafia.checkAccount(handler, savedName, savedPassword);
                    textAccount.setText(R.string.account_check);
                    textAccount.setOnClickListener(null);
                });
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg.what) {
                case 1:
                    if (message.equals("OK")) {
                        Toast.makeText(this, "Проверка аккаунта: Успешно!", Toast.LENGTH_SHORT).show();
                        start();
                    }
                    else {
                        Toast.makeText(this, "Ошибка авторизации: " + message, Toast.LENGTH_SHORT).show();
                        LoginActivity.saveNameAndPassword(sPrefs, null, null);
                        startActivityForResult(intentLogin, 0);
                    }
                    break;
            }
            return true;
        });

        sPrefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mafia.configInterceptor();
        mafia = Mafia.getInstance();

        savedName = sPrefs.getString("name", null);
        savedPassword = sPrefs.getString("password", null);
        if (savedName == null || savedPassword == null) startActivityForResult(intentLogin, 0);
        else mafia.checkAccount(handler, savedName, savedPassword);

    }


    private void start() {
        btnCreate = findViewById(R.id.btnCreate);
        btnConnect = findViewById(R.id.btnConnect);
        btnSettings = findViewById(R.id.btnSettings);
        btnAbout = findViewById(R.id.btnAbout);


        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra("playerName", savedName);
            intent.putExtra("roomName", "Комната1");
            intent.putExtra("roomPassword", "123");
            intent.putExtra("roomCreator", "Иван");
            startActivity(intent);
        });

        textAccount.setText(savedName);
        textSignOut.setVisibility(View.VISIBLE);
        textSignOut.setOnClickListener(view -> {
            LoginActivity.saveNameAndPassword(sPrefs, null, null);
            startActivityForResult(intentLogin, 0);
        });
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        btnCreate.setOnClickListener(view -> mafia.createRoom(handler, savedName, "Комната1", "123"));
        btnConnect.setOnClickListener(view -> mafia.checkAccount(handler, "Иван", "123"));

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg.what) {
                case 1:
                    // if (message.equals("OK")) return true;
                    Toast.makeText(this, "Ошибка авторизации: " + message, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    RoomList[] data = new Gson().fromJson(message, RoomList[].class);
                    for (RoomList pM: data) Log.d(TAG, pM.toString());
                    break;
                case 3:
                    if (message.equals("Комната с таким названием и паролем уже существует!")) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra("playerName", savedName);
                    intent.putExtra("roomName", "Комната1");
                    intent.putExtra("roomPassword", "123");
                    intent.putExtra("roomCreator", "Иван");
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == 1) {
                savedName = data.getStringExtra("name");
                start();
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) finishAffinity();
            else ActivityCompat.finishAffinity(this);
        }
    }

    void log(String txt) {
        Log.d(TAG, txt);
    }

}
