package com.example.ivan.mafia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class PlayActivity extends AppCompatActivity {

    String playerName, roomName, roomPassword, roomCreator;
    View playerView;
    Mafia mafia = Mafia.getInstance();
    ArrayList<HashMap<String, Object>> playersData = new ArrayList<>();

    Button btnVote, btnNext, btnGameOver;
    LinearLayout layoutRoomCreator;
    TextView textSheriffIsRight;
    GridView playerList;
    MyAdapter adapter;
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
        roomCreator = intent.getStringExtra("roomCreator");

        textSheriffIsRight = findViewById(R.id.textSheriffIsRight);
        playerList = findViewById(R.id.playerList);
        btnVote = findViewById(R.id.btnVote);
        btnNext = findViewById(R.id.btnNext);
        btnGameOver = findViewById(R.id.btnGameOver);
        layoutRoomCreator = findViewById(R.id.layoutRoomCreator);

        setTitle(roomName + " (Ожидание игроков)");

        playerList.setNumColumns(GridView.AUTO_FIT);

        Handler voteHandler = new Handler(msg -> {
            if (msg.arg1 == -1) {
                Toast.makeText(this, "Ошибка: " + msg.obj, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg.what) {
                case 12:
                    Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    btnVote.setEnabled(false);
                    break;
            }
            return true;
        });
        btnVote.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String[] players = new String[playersData.size()];
            for (int i = 0; i < playersData.size(); i++)
                players[i] = ((String) playersData.get(i).get("name"));
            builder.setSingleChoiceItems(players, 0, null);
            builder.setPositiveButton("Проголосовать", (dialog, which) -> mafia.vote(voteHandler, playerName, roomName, roomPassword, players[(((AlertDialog) dialog).getListView()).getCheckedItemPosition()]));
            builder.setNegativeButton("Отмена", null);
            builder.show();
        });
        btnVote.setEnabled(false);

        playCheckHandler = new Handler(msg -> {
            if (msg.arg1 == -1 ) {
                Toast.makeText(this, "Ошибка: " + msg.obj, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.obj.equals("Вы не являетесь игроком!") || msg.obj.equals("Комната с таким названием и паролем не существует")) {
                Toast.makeText(this, "Ошибка: " + msg.obj, Toast.LENGTH_SHORT).show();
                roomRun.interrupt();
                finish();
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

        if (playerName.equals(roomCreator)) notLaunched();

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
    }

    void playCycle(PlayCheck playCheck) {
//        if (!playCheck.isLaunched.equals(gameInfo.isLaunched)) gameInfo.isLaunchedUpdate(playCheck.isLaunched);
//        if (!playCheck.players.equals(gameInfo.players)) gameInfo.playersUpdate(playCheck.players);
//
//        Handler handler = new Handler(msg -> {
//            if (!playCheck.myRole.equals(gameInfo.myRole)) gameInfo.myRoleUpdate(playCheck.myRole);
//            if (!playCheck.playersWithMyRole.equals(gameInfo.playersWithMyRole))
//                gameInfo.playersWithMyRoleUpdate(playCheck.playersWithMyRole);
//            if (!playCheck.voted.equals(gameInfo.voted)) gameInfo.votedUpdate(playCheck.voted);
//            if (!playCheck.phaseNumber.equals(gameInfo.phaseNumber))
//                gameInfo.phaseNumberUpdate(playCheck.phaseNumber);
//            if (!playCheck.sheriffIsRight.equals(gameInfo.sheriffIsRight))
//                gameInfo.sheriffIsRightUpdate(playCheck.sheriffIsRight);
//            if (!playCheck.winners.equals(gameInfo.winners))
//                gameInfo.winnersUpdate(playCheck.winners, playCheck.players);
//            return true;
//        });
//        new Thread(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            handler.sendEmptyMessage(0);
//        }).start();


        if (!playCheck.isLaunched.equals(gameInfo.isLaunched)) gameInfo.isLaunchedUpdate(playCheck.isLaunched);
        if (!playCheck.players.equals(gameInfo.players)) gameInfo.playersUpdate(playCheck.players);
        if (!playCheck.myRole.equals(gameInfo.myRole)) gameInfo.myRoleUpdate(playCheck.myRole);
        if (!playCheck.playerRoles.equals(gameInfo.playerRoles))
            gameInfo.playerRolesUpdate(playCheck.playerRoles);
        if (!playCheck.phaseNumber.equals(gameInfo.phaseNumber))
            gameInfo.phaseNumberUpdate(playCheck.phaseNumber);
        if (!playCheck.sheriffIsRight.equals(gameInfo.sheriffIsRight))
            gameInfo.sheriffIsRightUpdate(playCheck.sheriffIsRight);
        if (!playCheck.winners.equals(gameInfo.winners)) gameInfo.winnersUpdate(playCheck.winners);
        if (!playCheck.voteCount.equals(gameInfo.voteCount)) gameInfo.voteCountUpdate(playCheck.voteCount);

    }

    private class MyAdapter extends BaseAdapter {

        LayoutInflater inflater = getLayoutInflater();

        int i = 0;

        MyAdapter(ArrayList<HashMap<String, Object>> list) {
            playersData = list;
        }

        @Override
        public int getCount() {
            return playersData.size();
        }

        @Override
        public Object getItem(int position) {
            return playersData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = inflater.inflate(R.layout.player_item, parent, false);

            HashMap<String, Object> map = playersData.get(position);

            String name = (String) map.get("name");
            String role = (String) map.get("role");
            boolean isAlive = !map.get("isAlive").equals("dead");
            Log.d("myTag", map.get("isAlive") + ", " + isAlive + ", ");

            int img;
            assert role != null;
            switch (role) {
                case "Мафия":
                    img = R.drawable.ic_mafia;
                    break;
                case "Доктор":
                    img = R.drawable.ic_doctor;
                    break;
                case "Шериф":
                    img = R.drawable.ic_sheriff;
                    break;
                case "Мирный житель":
                    img = R.drawable.ic_civilian;
                    break;
                default:
                    img = R.drawable.ic_default;
                    break;
            }

            TextView textView = view.findViewById(R.id.textName);
            ImageView imageView = view.findViewById(R.id.imageRole);
            textView.setText(name);
            imageView.setImageResource(img);

            textView.setTextColor(ContextCompat.getColor(PlayActivity.this, R.color.isAliveColor));

            assert name != null;
            if (name.equals(playerName)) {
                playerView = view;
                textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.setTextColor(ContextCompat.getColor(PlayActivity.this, R.color.playerNameColor));
            }
            if (!isAlive) textView.setTextColor(ContextCompat.getColor(PlayActivity.this, R.color.isNotAliveColor));

            return view;
        }
    }
    
    private class GameInfo extends PlayCheck {
        void getPlay() {
            isLaunched = false;
            phaseNumber = -1;
            myRole = "Player";
            sheriffIsRight = false;
            winners = "none";
            voteCount = 0;
            players = new HashMap<>();
            playerRoles = new HashMap<>();
        }

        void isLaunchedUpdate(Boolean isLaunched) {
            this.isLaunched = isLaunched;
            if (playerName.equals(roomCreator)) {
                layoutRoomCreator.setVisibility(View.VISIBLE);
                if (isLaunched) launched();
                else notLaunched();
            }
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
                    Log.d("myTag", "Ночь мафии");
                    if (myRole.equals("Мафия")) btnVote.setEnabled(true);
                    else btnVote.setEnabled(false);
                    break;
                case 2:
                    title += " (Ночь, Доктор)";
                    if (myRole.equals("Доктор")) btnVote.setEnabled(true);
                    else btnVote.setEnabled(false);
                    break;
                case 3:
                    title += " (Ночь, Шериф)";
                    if (myRole.equals("Шериф")) btnVote.setEnabled(true);
                    else  btnVote.setEnabled(false);
                    break;
                case 4:
                    title += " (Утро)";
                    textSheriffIsRight.setVisibility(View.VISIBLE);
                    btnVote.setEnabled(false);
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
            for (HashMap<String, Object> map: playersData)
                if (map.get("name").equals(playerName))
                    map.put("role", myRole);
            adapter = new MyAdapter(playersData);
            playerList.setAdapter(adapter);
        }

        void sheriffIsRightUpdate(Boolean sheriffIsRight) {
            this.sheriffIsRight = sheriffIsRight;
            textSheriffIsRight.setText("Шериф был " + (sheriffIsRight ? "" : "не ") + "прав");
        }

        void winnersUpdate(String winners) {
            this.winners = winners;
            if (winners.equals("Мирные жители")) {
                btnVote.setEnabled(false);
                textSheriffIsRight.setVisibility(View.VISIBLE);
                textSheriffIsRight.setText("Победили Мирные жители!");
            }
            if (winners.equals("Мафия")) {
                btnVote.setEnabled(false);
                textSheriffIsRight.setVisibility(View.VISIBLE);
                textSheriffIsRight.setText("Победила Мафия!");
            }
        }

        void voteCountUpdate(int voteCount) {
            this.voteCount = voteCount;
            if (playerName.equals(roomCreator)) btnVote.setText("Проголосовать (" + voteCount + ")");
        }

        void playersUpdate(HashMap<String, String> players) {
            this.players = players;
            playersData = new ArrayList<>(players.size());
            HashMap<String, Object> map;
            for (Map.Entry<String, String> entry: players.entrySet()) {
                map = new HashMap<>();
                map.put("name", entry.getKey());
                map.put("role", "Player");
                map.put("isAlive", entry.getValue());
                if (entry.getKey().equals(playerName)) map.put("role", myRole);
                playersData.add(map);
            }
            playerRolesUpdate(playerRoles);
            adapter = new MyAdapter(playersData);
            playerList.setAdapter(adapter);
        }

        void playerRolesUpdate(HashMap<String, String> playerRoles) {
            this.playerRoles = playerRoles;
            for (Map.Entry<String, String> entry: playerRoles.entrySet())
                for (HashMap<String, Object> map : playersData)
                    if (entry.getKey().equals(map.get("name")))
                        map.put("role", entry.getValue());
            adapter = new MyAdapter(playersData);
            playerList.setAdapter(adapter);
        }
    }

    void launched() {
        Handler nextHandler = new Handler(msg2 -> {
            if (msg2.arg1 == -1) {
                Toast.makeText(PlayActivity.this, "Ошибка: " + msg2.obj, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg2.what) {
                case 8:
                    Toast.makeText(PlayActivity.this, (String) msg2.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
        View.OnClickListener nextListener = view -> mafia.nextPhase(nextHandler, playerName, roomName, roomPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
        builder.setTitle("Завершить игру");
        builder.setMessage("Вы действительно хотите удалить комнату? Прогресс игры будет безвозвратно потерян.");
        builder.setPositiveButton("Да", (dialog, which) -> mafia.deleteRoom(deleteRoomHandler, playerName, roomName, roomPassword));
        builder.setNegativeButton("Нет", null);

        btnNext.setText("Следующая фаза");
        btnNext.setOnClickListener(nextListener);
        btnGameOver.setOnClickListener(view -> builder.show());
    }

    void notLaunched() {
        findViewById(R.id.layoutRoomCreator).setVisibility(View.VISIBLE);
        Handler gameStartHandler = new Handler(msg -> {
            if (msg.arg1 == -1 || !msg.obj.equals("Игра успешно запущена!")) {
                Toast.makeText(PlayActivity.this, "Ошибка: " + msg.obj, Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (msg.what) {
                case 6:
                    launched();
                    Toast.makeText(PlayActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
        View dialogView = getLayoutInflater().inflate(R.layout.create_dialog, null);
        DialogInterface.OnClickListener createListener = (dialog, which) -> {
            Integer playersCount = Integer.parseInt(((EditText) dialogView.findViewById(R.id.editPlayersCount)).getText().toString());
            Integer mafiaCount = Integer.parseInt(((EditText) dialogView.findViewById(R.id.editMafiaCount)).getText().toString());
            Integer doctorCount = Integer.parseInt(((EditText) dialogView.findViewById(R.id.editDoctorCount)).getText().toString());
            Integer sheriffCount = Integer.parseInt(((EditText) dialogView.findViewById(R.id.editSheriffCount)).getText().toString());
            Integer civilianCount = Integer.parseInt(((EditText) dialogView.findViewById(R.id.editCivilianCount)).getText().toString());
            mafia.gameStart(gameStartHandler, playerName, roomName, roomPassword, playersCount, mafiaCount, doctorCount, sheriffCount, civilianCount);
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
        builder.setTitle("Запуск игры");
        builder.setPositiveButton("Начать игру", createListener);
        builder.setNegativeButton("Отменить", null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        btnNext.setOnClickListener(view -> dialog.show());

        btnGameOver.setOnClickListener(view -> {
            AlertDialog.Builder gameOverDialogBuilder = new AlertDialog.Builder(PlayActivity.this);
            gameOverDialogBuilder.setTitle("Завершить игру");
            gameOverDialogBuilder.setMessage("Вы действительно хотите удалить комнату?");
            gameOverDialogBuilder.setPositiveButton("Да", (dial, which) -> mafia.deleteRoom(deleteRoomHandler, playerName, roomName, roomPassword));
            gameOverDialogBuilder.setNegativeButton("Нет", null);
        });
    }

    Handler deleteRoomHandler = new Handler(msg2 -> {
        if (msg2.arg1 == -1 || !msg2.obj.equals("Комната успешно удалена!")) {
            Toast.makeText(PlayActivity.this, "Ошибка: " + msg2.obj, Toast.LENGTH_SHORT).show();
            return true;
        }
        switch (msg2.what) {
            case 4:
                Toast.makeText(PlayActivity.this, (String) msg2.obj, Toast.LENGTH_SHORT).show();
                roomRun.interrupt();
                finish();
                break;
        }
        return true;
    });

    @Override
    protected void onDestroy() {
        roomRun.interrupt();
        Log.d("myLog", "onDestroy");
        super.onDestroy();
    }
}
