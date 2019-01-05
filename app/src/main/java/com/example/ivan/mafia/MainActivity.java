package com.example.ivan.mafia;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    public static final int myToken = 421928;

    Handler checkHandler, playHandler;
    SharedPreferences sPrefs;
    Mafia mafia = Mafia.getInstance();
    ArrayList<HashMap<String, Object>> roomsData;
    RoomInfo[] roomList;

    Button btnCreate, btnConnect, btnSettings, btnAbout;
    TextView textAccount, textSignOut;
    Intent intentLogin, intentPlay;

    String savedName, savedPassword;

    String rName, rPass, rMaker;

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


        mafia.configInterceptor();


        checkHandler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                textAccount.setText(R.string.try_again);
                textAccount.setOnClickListener(view -> {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    mafia.checkAccount(checkHandler, savedName, savedPassword);
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
        savedName = sPrefs.getString("name", null);
        savedPassword = sPrefs.getString("password", null);
        if (savedName == null || savedPassword == null) startActivityForResult(intentLogin, 0);
        else mafia.checkAccount(checkHandler, savedName, savedPassword);

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
            intent.putExtra("roomCreator", "Иван2");
            startActivity(intent);
        });
        btnCreate.setOnClickListener(view -> {

        });
        btnConnect.setOnClickListener(view -> mafia.checkRooms(playHandler));

        textAccount.setText(savedName);
        textSignOut.setVisibility(View.VISIBLE);
        textSignOut.setOnClickListener(view -> {
            LoginActivity.saveNameAndPassword(sPrefs, null, null);
            startActivityForResult(intentLogin, 0);
        });
        findViewById(R.id.progressBar).setVisibility(View.GONE);


        DialogInterface.OnClickListener roomSelectListener = (dialog1, position) -> {
            rName = (String) roomsData.get(position).get("roomName");
            rPass = "";
            rMaker = roomList[position].getRoomMaker();
            if (roomList[position].getPassword()) {
                AlertDialog.Builder roomPasswordDialogBuilder = new AlertDialog.Builder(this);
                roomPasswordDialogBuilder.setTitle("Пароль");
                roomPasswordDialogBuilder.setView(getLayoutInflater().inflate(R.layout.password_dialog, null));
                roomPasswordDialogBuilder.setPositiveButton("Ок", (dialog2, w2) -> {
                    rPass = ((EditText) ((AlertDialog) dialog2).findViewById(R.id.editPassword)).getText().toString();
                    mafia.joinRoom(playHandler, savedName, rName, rPass);
                });
                roomPasswordDialogBuilder.setNegativeButton("Отменить", null);
                roomPasswordDialogBuilder.show();
            } else mafia.joinRoom(playHandler, savedName, rName, rPass);
        };

        playHandler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg.what) {
                case 2:
                    roomList = new Gson().fromJson(message, RoomInfo[].class);
                    if (roomList == null) {
                        Toast.makeText(this, "Нет созданных комнат", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    roomsData = new ArrayList<>(roomList.length);
                    HashMap<String, Object> map;
                    for (RoomInfo room: roomList) {
                        map = new HashMap<>(3);
                        map.put("roomName", room.getRoomName());
                        map.put("isLaunched", room.getLaunched());
                        map.put("roomMaker", room.getRoomMaker());
                        map.put("password", room.getPassword());
                        roomsData.add(map);
                    }
                    AlertDialog.Builder roomsDialogBuilder = new AlertDialog.Builder(this);
                    roomsDialogBuilder.setAdapter(new RoomListAdapter(), roomSelectListener);
                    roomsDialogBuilder.show();
                    break;
                case 3:
                    if (message.equals("Комната с таким названием уже существует!")) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    intentPlay.putExtra("playerName", savedName);
                    intentPlay.putExtra("roomName", "Комната1");
                    intentPlay.putExtra("roomPassword", "123");
                    intentPlay.putExtra("roomMaker", savedName);
                    startActivity(intentPlay);
                    break;
                case 7:
                    if (message.equals("Комнаты с такой комбинацией логина и пароля не существует!")) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (message.equals("Вы не можете присоединиться к игре, так как она уже началась"))
                        Toast.makeText(this, "Попытка подключиться к игре", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra("playerName", savedName);
                    intent.putExtra("roomName", rName);
                    intent.putExtra("roomPassword", rPass);
                    intent.putExtra("roomMaker", rMaker);
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

    private class RoomListAdapter extends BaseAdapter {

        LayoutInflater inflater = getLayoutInflater();

        @Override
        public int getCount() {
            return roomsData.size();
        }

        @Override
        public Object getItem(int position) {
            return roomsData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = inflater.inflate(R.layout.room_item, parent, false);

            HashMap<String, Object> map = roomsData.get(position);

            String roomName = (String) map.get("roomName");
            boolean isLaunched = (boolean) map.get("isLaunched");
            String roomStatus = isLaunched ? "Запущена" : "Ожидание игроков";
            String roomMaker = (String) map.get("roomMaker");
            boolean password = (boolean) map.get("password");
            String roomType = password ? "Закрытая (Пароль)" : "Открытая";

            TextView textRoomName = view.findViewById(R.id.textRoomName);
            TextView textRoomStatus = view.findViewById(R.id.textRoomStatus);
            TextView textRoomMaker = view.findViewById(R.id.textRoomMaker);
            TextView textRoomType = view.findViewById(R.id.textRoomType);
            textRoomName.setText(roomName);
            textRoomStatus.setText("Состояние: " + roomStatus);
            textRoomMaker.setText("Создатель комнаты: " + roomMaker);
            textRoomType.setText("Тип комнаты: " + roomType);

            if (position == roomsData.size()-1) {
                view.findViewById(R.id.imgLine).setVisibility(View.GONE);
            } else view.findViewById(R.id.imgLine).setVisibility(View.VISIBLE);


            int color = isLaunched ? R.color.isNotAliveColor : R.color.isAliveColor;
            textRoomStatus.setTextColor(ContextCompat.getColor(MainActivity.this, color));

            return view;
        }
    }

}
