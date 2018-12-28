package com.example.ivan.mafia;

import android.os.Handler;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Mafia {

    private static Mafia mafia = new Mafia();

    public static Mafia getInstance() {
        mafia.configInterceptor();
        return mafia;
    }

    private Handler handler = MainActivity.handler;

    /*private Retrofit retrofitJSON = new Retrofit.Builder()
            .baseUrl("https://themafia2281488.000webhostapp.com/") // Базовый адрес
            .addConverterFactory(GsonConverterFactory.create()) // Для преобразования JSON в List
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    Observable<List<RoomList>> playObservable = Observable
            .interval(0, 1000, TimeUnit.MILLISECONDS)
            .flatMap((Function<Long, ObservableSource<?>>) aLong -> mafiaJSONAPI.getPlayMessage(1234))
            .map(o -> (List<RoomList>) o)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    private MafiaAPI mafiaJSONAPI = retrofitJSON.create(MafiaAPI.class);

    Observer<List<RoomList>> playObserver = new Observer<List<RoomList>>() {
        @Override
        public void onSubscribe(Disposable d) {
            log("onSubscribe");
        }

        @Override
        public void onNext(List<RoomList> messageList) {
            log("onNext: " + messageList.size());
            messageList.forEach(message -> {
                log("PlayersCount: " + message.getPlayersCount());
                log("MyName: " + message.getMyName());
                log("MyRole: " + message.getMyRole());
                log("NumbersOfDay: " + message.getNumberOfDay());
            });
        }

        @Override
        public void onError(Throwable e) {
            log("onError: " + e.getMessage());
        }

        @Override
        public void onComplete() {
            log("onComplete");
        }
    };*/

    private void configInterceptor() {
        // Настраиваем слежение за запросами
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://themafia2281488.000webhostapp.com/") // Базовый адрес
                .addConverterFactory(ScalarsConverterFactory.create()) // Для преобразования в строку
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(client)
                .build();

        mafiaAPI = retrofit.create(MafiaAPI.class);
    }

    private MafiaAPI mafiaAPI;

    void checkAccount(String name, String password) {
        mafiaAPI.checkAccount(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(1));
    }

    void checkRooms() {
        mafiaAPI.checkRooms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(2));
    }

    void createRoom(String playerName, String roomName, String roomPassword) {
        mafiaAPI.createRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(3));
    }

    void deleteRoom(String playerName, String roomName, String roomPassword) {
        mafiaAPI.deleteRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(4));
    }

    void gameOver(String playerName, String roomName, String roomPassword) {
        mafiaAPI.gameOver(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(5));
    }

    void gameStart(String playerName, String roomName, String roomPassword, Integer playersCount, Integer mafiaCount, Integer doctorCount, Integer sheriffCount, Integer civilianCount) {
        mafiaAPI.gameStart(playerName, roomName, roomPassword, playersCount, mafiaCount, doctorCount, sheriffCount, civilianCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(6));
    }

    void joinRoom(String playerName, String roomName, String roomPassword) {
        mafiaAPI.joinRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(7));
    }

    void nextPhase(String playerName, String roomName, String roomPassword) {
        mafiaAPI.nextPhase(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(8));
    }

    void playCheck(String playerName, String roomName, String roomPassword) {
        mafiaAPI.playCheck(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(9));
    }

    void register(String name, String password) {
        mafiaAPI.register(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(10));
    }

    void signIn(String name, String password) {
        mafiaAPI.signIn(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(11));
    }

    void vote(String playerName, String roomName, String roomPassword, String vote) {
        mafiaAPI.vote(playerName, roomName, roomPassword, vote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(12));
    }



    private class MyObserver implements Observer<String> {

        private int n; // Номер функции

        private String response; // Ответ сервера

        MyObserver(int n) {
            this.n = n;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String s) {
            response = s;
        }

        @Override
        public void onError(Throwable e) {
            handler.sendMessage(handler.obtainMessage(n, -1, 0, e.getMessage()));
        } // arg1 = -1 => Ошибка

        @Override
        public void onComplete() {
            handler.sendMessage(handler.obtainMessage(n, 1, 0, response));
        } // arg1 = 1 => Всё хорошо
    }

}
