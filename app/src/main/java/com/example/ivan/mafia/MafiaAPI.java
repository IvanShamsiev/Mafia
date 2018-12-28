package com.example.ivan.mafia;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MafiaAPI {

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
