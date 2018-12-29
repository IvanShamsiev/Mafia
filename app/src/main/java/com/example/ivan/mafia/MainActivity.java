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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";

    public static final int myToken = 421928;

    public static Handler handler;

    SharedPreferences sPrefs;

    Mafia mafia;
    Button btnCreate, btnConnect, btnSettings, btnAbout;
    TextView textAccount, textSignOut;

    String savedName, savedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAccount = findViewById(R.id.textAccount);
        textSignOut = findViewById(R.id.textSignOut);

        textAccount.setText(R.string.account_check);
        textSignOut.setVisibility(View.GONE);


        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
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
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivityForResult(intent, 0);
                    }
                    break;
            }
            return true;
        });

        sPrefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mafia = Mafia.getInstance();

        savedName = sPrefs.getString("name", null);
        savedPassword = sPrefs.getString("password", null);
        if (savedName == null || savedPassword == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 0);
        } else mafia.checkAccount(handler, savedName, savedPassword);

    }


    private void start() {
        btnCreate = findViewById(R.id.btnCreate);
        btnConnect = findViewById(R.id.btnConnect);
        btnSettings = findViewById(R.id.btnSettings);
        btnAbout = findViewById(R.id.btnAbout);

        textAccount.setText(savedName);
        textSignOut.setVisibility(View.VISIBLE);
        textSignOut.setOnClickListener(view -> {
            LoginActivity.saveNameAndPassword(sPrefs, null, null);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 0);
        });
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        btnCreate.setOnClickListener(view -> mafia.checkRooms(handler));
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
