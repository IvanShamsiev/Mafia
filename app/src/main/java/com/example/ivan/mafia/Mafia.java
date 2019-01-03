package com.example.ivan.mafia;

import android.os.Handler;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Mafia {

    private static Mafia mafia;

    static Mafia getInstance() {
        if (mafia == null) mafia = new Mafia();
        return mafia;
    }


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

    
    
    void configInterceptor() {
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

    void checkAccount(Handler handler, String name, String password) {
        mafiaAPI.checkAccount(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(1, handler));
    }

    void checkRooms(Handler handler) {
        mafiaAPI.checkRooms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(2, handler));
    }

    void createRoom(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.createRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(3, handler));
    }

    void deleteRoom(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.deleteRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(4, handler));
    }

    void gameOver(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.gameOver(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(5, handler));
    }

    void gameStart(Handler handler, String playerName, String roomName, String roomPassword, Integer playersCount, Integer mafiaCount, Integer doctorCount, Integer sheriffCount, Integer civilianCount) {
        mafiaAPI.gameStart(playerName, roomName, roomPassword, playersCount, mafiaCount, doctorCount, sheriffCount, civilianCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(6, handler));
    }

    void joinRoom(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.joinRoom(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(7, handler));
    }

    void nextPhase(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.nextPhase(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(8, handler));
    }

    void playCheck(Handler handler, String playerName, String roomName, String roomPassword) {
        mafiaAPI.playCheck(playerName, roomName, roomPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(9, handler));
        /*Observable
                .interval(0, 1000, TimeUnit.MILLISECONDS)
                .flatMap((Function<Long, ObservableSource<?>>) aLong -> mafiaAPI.playCheck(playerName, roomName, roomPassword))
                .map(o -> (PlayCheck) o)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserverForJSON(9, playCheckHandler));*/
    }

    void register(Handler handler, String name, String password) {
        mafiaAPI.register(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(10, handler));
    }

    void signIn(Handler handler, String name, String password) {
        mafiaAPI.signIn(name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(11, handler));
    }

    void vote(Handler handler, String playerName, String roomName, String roomPassword, String vote) {
        mafiaAPI.vote(playerName, roomName, roomPassword, vote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver(12, handler));
    }


    private class MyObserver implements Observer<Object> {

        private int n; // Номер функции

        private Handler handler; // Куда отправлять результат

        private Object response; // Ответ сервера

        MyObserver(int n, Handler handler) {
            this.n = n;
            this.handler = handler;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Object o) {
            response = o;
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
