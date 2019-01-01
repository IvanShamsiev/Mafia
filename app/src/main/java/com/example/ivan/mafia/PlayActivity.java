package com.example.ivan.mafia;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity {

    String playerName, roomName, roomPassword;
    View playerView;
    Mafia mafia = Mafia.getInstance();

    Button btnVote;
    TextView textSheriffIsRight;
    GridView playerList;
    MyAdapter adapter;
    ArrayList<HashMap<String, Object>> list = new ArrayList<>();
    Handler playCheckHandler;

    GameInfo gameInfo;

    Thread roomRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");
        roomName = intent.getStringExtra("roomName");
        roomPassword = intent.getStringExtra("roomPassword");

        textSheriffIsRight = findViewById(R.id.textSheriffIsRight);
        playerList = findViewById(R.id.playerList);
        btnVote = findViewById(R.id.btnVote);
//        btnVote.setEnabled(false);

        setTitle(roomName + " (Ожидание игроков)");

//        HashMap<String, Object> map;
//
//        map = new HashMap<>();
//        map.put("name", "Иванушка");
//        map.put("image", R.drawable.ic_civilian);
//        list.add(map);
//
//        for (int i = 0; i < 100; i++) {
//            map = new HashMap<>();
//            map.put("name", "Иван" + i);
//            map.put("image", R.drawable.ic_civilian);
//            list.add(map);
//        }
//        adapter = new MyAdapter(list);

        playerList.setAdapter(adapter);
        playerList.setNumColumns(GridView.AUTO_FIT);
//        playerList.setColumnWidth(500);
//        playerList.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);

        btnVote.setOnClickListener(view -> {});

        playCheckHandler = new Handler(msg -> {
            if (msg.arg1 == -1 || msg.obj.equals("Вы не являетесь игроком!") ||
                    msg.obj.equals("Комната с таким названием и паролем не существует")) {
                Toast.makeText(this, "Ошибка: " + msg.obj, Toast.LENGTH_SHORT).show();
                return true;
            }
            PlayCheck playCheck = new Gson().fromJson((String) msg.obj, PlayCheck.class);
            switch (msg.what) {
                case 9:
                    playCycle(playCheck);
                    break;
            }
            return true;
        });

        gameInfo = new GameInfo();
        gameInfo.getPlay();

        roomRun = new Thread(() -> {
            while (true) {
                try {
                    mafia.playCheck(playCheckHandler, playerName, roomName, roomPassword);
                    TimeUnit.MILLISECONDS.sleep(500);
                    if (roomRun.isInterrupted()) return;
                } catch (InterruptedException e) {
                    Log.d("myLog", "Остановка thread: " + e.getLocalizedMessage());
                    return;
                }
            }
        });
        roomRun.start();
        Log.d("myLog", "111");
    }

    void playCycle(PlayCheck playCheck) {
        if (!playCheck.isLaunched.equals(gameInfo.isLaunched)) gameInfo.isLaunchedUpdate(playCheck.isLaunched);
        if (!playCheck.players.equals(gameInfo.players)) gameInfo.playersUpdate(playCheck.players);

        Handler handler = new Handler(msg -> {
            if (!playCheck.myRole.equals(gameInfo.myRole)) gameInfo.myRoleUpdate(playCheck.myRole);
            if (!playCheck.playersWithMyRole.equals(gameInfo.playersWithMyRole))
                gameInfo.playersWithMyRoleUpdate(playCheck.playersWithMyRole);
            if (!playCheck.phaseNumber.equals(gameInfo.phaseNumber))
                gameInfo.phaseNumberUpdate(playCheck.phaseNumber);
            if (!playCheck.sheriffIsRight.equals(gameInfo.sheriffIsRight))
                gameInfo.sheriffIsRightUpdate(playCheck.sheriffIsRight);
            if (!playCheck.winners.equals(gameInfo.winners))
                gameInfo.winnersUpdate(playCheck.winners, playCheck.players);
            return true;
        });
            handler.sendEmptyMessage(0);
    }

    private class MyAdapter extends BaseAdapter {

        ArrayList<HashMap<String, Object>> data;

        ArrayList<View> views = new ArrayList<>();

        LayoutInflater inflater = getLayoutInflater();

        int i = 0;

        MyAdapter(ArrayList<HashMap<String, Object>> list) {
            data = list;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = inflater.inflate(R.layout.player_item, parent, false);

            HashMap<String, Object> map = data.get(position);

            String name = (String) map.get("name");
            int image = (int) map.get("image");

            TextView textView = view.findViewById(R.id.textName);
            ImageView imageView = view.findViewById(R.id.imageRole);
            textView.setText(name);
            imageView.setImageResource(image);

            assert name != null;
            if (name.equals(playerName)) {
                playerView = view;
                textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.setTextColor(ContextCompat.getColor(PlayActivity.this, R.color.playerNameColor));
            }

            views.add(view);
            Log.d("myLog", "Добавляю view" + (views.size()-1) + ": " + view + ", textName: " + ((TextView) view.findViewById(R.id.textName)).getText());
            Log.d("myLog", "dataSize: " + data.size() + ", viewsSize: " + views.size());

            return view;
        }
    }
    
    private class GameInfo extends PlayCheck {
        int myRoleImg;

        void getPlay() {
            isLaunched = false;
            phaseNumber = -1;
            myRole = "Player";
            sheriffIsRight = false;
            winners = "none";
            players = new HashMap<>();
            playersWithMyRole = new ArrayList<>();
        }

        void isLaunchedUpdate(Boolean isLaunched) {
            this.isLaunched = isLaunched;
        }

        void phaseNumberUpdate(int phaseNumber) {
            this.phaseNumber = phaseNumber;
            String title = roomName;
            switch (phaseNumber) {
                case 0:
                    title += " (Вечер)";
                    btnVote.setEnabled(false);
                    textSheriffIsRight.setVisibility(View.GONE);
                    break;
                case 1:
                    title += " (Ночь, Мафия)";
                    if (myRole.equals("Мафия")) btnVote.setEnabled(true);
                    else  btnVote.setEnabled(false);
                    break;
                case 2:
                    title += " (Ночь, Доктор)";
                    if (myRole.equals("Доктор")) btnVote.setEnabled(true);
                    else  btnVote.setEnabled(false);
                    break;
                case 3:
                    title += " (Ночь, Шериф)";
                    if (myRole.equals("Шериф")) btnVote.setEnabled(true);
                    else  btnVote.setEnabled(false);
                    break;
                case 4:
                    title += " (Утро)";
                    btnVote.setEnabled(false);
                    textSheriffIsRight.setText("Шериф был " + (sheriffIsRight ? "" : "не ") + "прав");
                    textSheriffIsRight.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    title += " (День, Голосование)";
                    btnVote.setEnabled(true);
                    break;
            }
            setTitle(title);
        }

        void myRoleUpdate(String role) {
            myRole = role;
            ImageView playerImage = playerView.findViewById(R.id.imageRole);
            switch (myRole) {
                case "Мафия":
                    myRoleImg = R.drawable.ic_mafia;
                    break;
                case "Доктор":
                    myRoleImg = R.drawable.ic_doctor;
                    break;
                case "Шериф":
                    myRoleImg = R.drawable.ic_sheriff;
                    break;
                case "Мирный житель":
                    myRoleImg = R.drawable.ic_civilian;
                    break;
                default:
                    myRoleImg = R.drawable.ic_default;
                    break;
            }
            playerImage.setImageResource(myRoleImg);
//            Handler handler = new Handler(msg -> {
//                myRole = role;
//                ImageView playerImage = playerView.findViewById(R.id.imageRole);
//                switch (myRole) {
//                    case "Мафия":
//                        myRoleImg = R.drawable.ic_mafia;
//                        break;
//                    case "Доктор":
//                        myRoleImg = R.drawable.ic_doctor;
//                        break;
//                    case "Шериф":
//                        myRoleImg = R.drawable.ic_sheriff;
//                        break;
//                    case "Мирный житель":
//                        myRoleImg = R.drawable.ic_civilian;
//                        break;
//                    default:
//                        myRoleImg = R.drawable.ic_default;
//                        break;
//                }
//                playerImage.setImageResource(myRoleImg);
//                return true;
//            });
//            new Thread(() -> {
//                while (playerView == null) {
//                    try {TimeUnit.MILLISECONDS.sleep(100);}
//                    catch (InterruptedException e) {e.printStackTrace(); }
//                }
//                handler.sendEmptyMessage(0);
//            }).start();
        }

        void sheriffIsRightUpdate(Boolean sheriffIsRight) {
            this.sheriffIsRight = sheriffIsRight;
        }

        void winnersUpdate(String winners, HashMap<String, String> players) {
            this.winners = winners;
            this.players = players;
            if (winners.equals("Мирные жители")) {
                btnVote.setEnabled(false);
                textSheriffIsRight.setVisibility(View.VISIBLE);
                textSheriffIsRight.setText("Победили Мирные жители!");
                return;
            }
            if (winners.equals("Мафия")) {
                btnVote.setEnabled(false);
                textSheriffIsRight.setVisibility(View.VISIBLE);
                textSheriffIsRight.setText("Победила Мафия!");
                return;
            }
            for(int i = 0; i < adapter.data.size(); i++)
                for (Map.Entry<String, String> entry: players.entrySet())
                    if (adapter.data.get(i).get("name") == entry.getKey()) {
                        ImageView playerImage = findViewById(i).findViewById(R.id.imageRole);
                        switch (myRole) {
                            case "Мафия":
                                playerImage.setImageResource(R.drawable.ic_mafia);
                                break;
                            case "Доктор":
                                playerImage.setImageResource(R.drawable.ic_doctor);
                                break;
                            case "Шериф":
                                playerImage.setImageResource(R.drawable.ic_sheriff);
                                break;
                            case "Мирный житель":
                                playerImage.setImageResource(R.drawable.ic_civilian);
                                break;
                            default:
                                playerImage.setImageResource(R.drawable.ic_default);
                                break;
                        }
                    }
        }

        void playersUpdate(HashMap<String, String> players) {
            this.players = players;
            list = new ArrayList<>(players.size());
            HashMap<String, Object> map;
            for (Map.Entry<String, String> entry: players.entrySet()) {
                map = new HashMap<>();
                map.put("name", entry.getKey());
                map.put("image", R.drawable.ic_default);
                list.add(map);
            }
            adapter = new MyAdapter(list);
            playerList.setAdapter(adapter);

            Log.d("myLog", "Обновление списка игроков: " + adapter.data.size() + "; " + adapter.views.size() + "; " + playerList.getCount());
        }

        void playersWithMyRoleUpdate(ArrayList<String> playersWithMyRole) {
            this.playersWithMyRole = playersWithMyRole;
            for (View view: adapter.views)
                for (String pName : playersWithMyRole)
                    if (((TextView) (view.findViewById(R.id.textName))).getText().equals(pName))
                        ((ImageView) view.findViewById(R.id.imageRole)).setImageResource(myRoleImg);
//            Handler handler = new Handler(msg -> {
//                for (View view: adapter.views)
//                    for (String pName : playersWithMyRole)
//                        if (((TextView) (view.findViewById(R.id.textName))).getText().equals(pName))
//                            ((ImageView) view.findViewById(R.id.imageRole)).setImageResource(myRoleImg);
//                return true;
//            });
//            new Thread(() -> {
//                while (adapter.views.size() == 0) {
//                    try {TimeUnit.MILLISECONDS.sleep(100);}
//                    catch (InterruptedException e) {e.printStackTrace(); }
//                }
//                handler.sendEmptyMessage(0);
//            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        roomRun.interrupt();
        Log.d("myLog", "onDestroy");
        super.onDestroy();
    }
}
