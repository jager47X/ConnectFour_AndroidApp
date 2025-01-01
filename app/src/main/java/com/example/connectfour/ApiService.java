ackage com.example.connectfour;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("get")
    Call<GameStatus> getGameStatus();

    @POST("send")
    Call<GameResponse> sendPlayerMove(@Body PlayerMove move);

    @GET("aiMove")
    Call<GameResponse> getAIMove();  // AI move endpoint

    @POST("reset")
    Call<GameStatus> sendReset();  // Reset game endpoint
}