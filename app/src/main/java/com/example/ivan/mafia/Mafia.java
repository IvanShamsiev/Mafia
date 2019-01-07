package com.example.ivan.mafia;

import android.os.Handler;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


class Mafia {

    private static Mafia mafia;

    static Mafia getInstance() {
        if (mafia == null) mafia = new Mafia();
        return mafia;
    }
    
    void configInterceptor() {
        // Настраиваем слежение за запросами
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .build();
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
        public void onSubscribe(Disposable d) { }

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

    private interface MafiaAPI {
        @GET("checkAccount.php") // 1
        Observable<String> checkAccount(@Query("name") String name, @Query("password") String password);

        @GET("checkRooms.php") // 2
        Observable<String> checkRooms();

        @GET("createRoom.php") // 3
        Observable<String> createRoom(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("deleteRoom.php") // 4
        Observable<String> deleteRoom(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("gameOver.php") // 5
        Observable<String> gameOver(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("gameStart.php") // 6
        Observable<String> gameStart(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword,
                                     @Query("playersCount") int playersCount, @Query("mafiaCount") int mafiaCount, @Query("doctorCount") int doctorCount,
                                     @Query("sheriffCount") int sheriffCount, @Query("civilianCount") int civilianCount);

        @GET("joinRoom.php") // 7
        Observable<String> joinRoom(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("nextPhase.php") // 8
        Observable<String> nextPhase(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("playCheck.php") // 9
        Observable<String> playCheck(@Query("playerName") String playerName, @Query("roomName") String roomName, @Query("roomPassword") String roomPassword);

        @GET("register.php") // 10
        Observable<String> register(@Query("name") String name, @Query("password") String password);

        @GET("signIn.php") // 11
        Observable<String> signIn(@Query("name") String name, @Query("password") String password);

        @GET("vote.php") // 12
        Observable<String> vote(@Query("playerName") String playerName, @Query("roomName") String roomName,
                                @Query("roomPassword") String roomPassword, @Query("vote") String vote);
    }

}
