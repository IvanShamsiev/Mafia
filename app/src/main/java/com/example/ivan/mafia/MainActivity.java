package com.example.ivan.mafia;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";

    public static final int myToken = 421928;

    public static Handler handler;

    Mafia mafia;
    Button btnCreate, btnConnect, btnSettings, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreate = findViewById(R.id.btnCreate);
        btnConnect = findViewById(R.id.btnConnect);
        btnSettings = findViewById(R.id.btnSettings);
        btnAbout = findViewById(R.id.btnAbout);

        btnCreate.setOnClickListener(view -> mafia.checkRooms());
        btnConnect.setOnClickListener(view -> mafia.checkAccount("Иван", "123"));

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

        mafia = Mafia.getInstance();

//        handler = new Handler(msg -> {
//            String message = (String) msg.obj;
//            if (message.equals("Игра ещё не запущена"))
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//            else mafia.connect(myToken);
//            return true;
//        });


        // Настраиваем слежение за запросами
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .build();
//
//        Retrofit retrofitString = new Retrofit.Builder()
//                .baseUrl("https://themafia2281488.000webhostapp.com/") // Базовый адрес
//                .addConverterFactory(ScalarsConverterFactory.create()) // Для преобразования в строку
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(client)
//                .build();
//        MafiaAPI mafiaStringAPI = retrofitString.create(MafiaAPI.class);
//
//        Observer<String> checkObserver = new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                log("onSubscribe");
//            }
//
//            @Override
//            public void onNext(String s) {
//                log("onNext: " + s);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                log("onError: " + e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//                log("onComplete");
//            }
//        };
//
//            String response;
//            Observable<String> listOfMessages = mafiaStringAPI.checkGame(421928);
//            listOfMessages
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(checkObserver);









//        new Thread(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(3);
//                Observable<List<RoomList>> listOfMessages2 = mafiaAPI.getPlayMessage(22222);
//                listOfMessages
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(messageList -> log("Ку!"));
//            } catch (InterruptedException e) {e.printStackTrace();}
//        }).start();

    }


    class myClass {
        public List<RoomList> getList() {
            return list;
        }

        public void setList(List<RoomList> list) {
            this.list = list;
        }

        List<RoomList> list;
    }

    void log(String txt) {
        Log.d(TAG, txt);
    }

}
