package com.example.connectfour;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int COLS_SIZE = 7;
    private static final int ROWS_SIZE = 6;
    private static final int PLAYER_1 = R.drawable.red_chip;
    private GridLayout gridLayout;
    private static final int PLAYER_2 = R.drawable.yellow_chip;

    private boolean player1Turn = true;

    private int winner = -1;
    private int turn = 1;

    private boolean nextTurn = false;

    private static final String BASE_URL = "https://your-api-base-url.com/";
    private ApiService apiService;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create the ApiService
        apiService = retrofit.create(ApiService.class);

        // Make API calls as needed
        getDataFromApi();
        postDataToApi();

        initializeBoard();

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        updatePlayerTurnTextView();
    }

    
//UI
    private void initializeBoard() {

        gridLayout = findViewById(R.id.board_gridlayout);
        for (int i = 0; i < ROWS_SIZE; i++) {
            for (int j = 0; j < COLS_SIZE; j++) {
                //cardview
                CardView cardView=new CardView(this);
                LayoutParams viewCard_params= new LayoutParams(130,130);
                cardView.setLayoutParams(viewCard_params);


                //linearlayout
                LinearLayout linearLayout=new LinearLayout(this);
                ViewGroup.LayoutParams linearLayout_params=new ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(linearLayout_params);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.boardColor));

                //imageview
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.empty);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
               //params.setGravity(Gravity.CENTER);
                imageView.setLayoutParams(params);


                linearLayout.addView(imageView);
                cardView.addView(linearLayout);
                gridLayout.addView(cardView);
                final int row = i;
                final int col = j;

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick(row, col);
                    }
                });


            }
        }

    }

    private void onCellClick(int row, int col) {
        // Handle the cell click logic (update the board, check for a win, etc.)
        // Implement your game logic here

        // Example: Update the image based on the player's turn

        int imageResource = player1Turn ? R.drawable.red_chip : R.drawable.yellow_chip;
        nextTurn= updateCell(row, col, imageResource);

       // isRowFull(col);//check board is full;
        if (checkForWinner()) {
            Log.d("Game status","game over");
        }else{
            Log.d("Game status","On going");
        }
        if(nextTurn==true){
            player1Turn = !player1Turn;
            nextTurn=false;
        }
        // Update the playerTurnTextView
        updatePlayerTurnTextView();
    }

    private boolean updateCell(int row, int col, int imageResource) {

        //if col is taken col next
        //check if the cell below is clear, stuck up from bottom.

        int  position = -1;
        int counter=0;
        while(position==-1){
            position=CheckRow(col);
            counter++;
            if(counter>=ROWS_SIZE){
                Log.e("Invalid:", position+", row:" + row + ", col: " + col);
                return false;
            }
        }

        Log.d("position:", position+", row:" + row + ", col: " + col);
        ImageView imageView = findImageViewAtPosition(position);

        if (imageView != null) {
                imageView.setImageResource(imageResource);
                //send API to server
                return true;
        } else {
            Log.e("UpdateCell", "ImageView is null for row: " + row + ", col: " + col);
            return false;
        }


    }

    private ImageView findImageViewAtPosition(int position) {
        if (position >= 0 && position < gridLayout.getChildCount()) {
            View childview = gridLayout.getChildAt(position);
            if (childview instanceof CardView) {
                CardView cardView = (CardView)childview;
                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
                return (ImageView) linearLayout.getChildAt(0);
            }
        }
        return null;
    }

    private void updatePlayerTurnTextView() {
        TextView playerTurnTextView = findViewById(R.id.playerTurnTextView);
        if(winner==1||winner==2||winner==0){
                playerTurnTextView.setText("Game Over!");
        }else{
            playerTurnTextView.setText(player1Turn ? "Player 1's Turn" : "Player 2's Turn");
        }

    }

    private void resetGame() {
        // Clear the board
        clearBoard();

        // Reset game variables
        player1Turn = true;
        winner = -1;

        // Update the player turn text view
        updatePlayerTurnTextView();

        // Reset any other game-specific variables if needed

        // Clear the "Game Over" message
        TextView gameOver = findViewById(R.id.gameOver);
        gameOver.setText("");
    }

    private void clearBoard() {
        // Iterate through all ImageViews in the GridLayout and reset them to the "empty" state
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView imageView = findImageViewAtPosition(i);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.empty);
            }
        }
    }



    private int CheckRow(int col) {
        int row = 0;
        for (int rows = ROWS_SIZE -1; rows >= 0; rows--) {//Row 0-5
            Log.d("Player Selected", "row:" + rows + ", col: " + col);
            int position = rows * COLS_SIZE + col;
            ImageView imageView = findImageViewAtPosition(position);
            Drawable drawable = imageView.getDrawable();

            if (drawable != null && drawable.getConstantState() != null&&drawable.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState())) {
                Log.d("This cell is empty", "row:" + rows + ", col: " + col);
                return position;
            }
            row = rows;
        }

        Log.d("This column is full", "row:"+row+", col: " + col);
        return -1;
    }

    private boolean checkForWinner() {
        // Check for a win in rows
        for (int row = 0; row < ROWS_SIZE; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row, col + 1, row, col + 2, row, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }

        // Check for a win in columns
        for (int col = 0; col < COLS_SIZE; col++) {
            for (int row = 0; row < ROWS_SIZE - 3; row++) {
                if (checkChips(row, col, row + 1, col, row + 2, col, row + 3, col)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }

        // Check for a win in diagonals (from bottom-left to top-right)
        for (int row = 3; row < ROWS_SIZE; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row - 1, col + 1, row - 2, col + 2, row - 3, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }

        // Check for a win in diagonals (from top-left to bottom-right)
        for (int row = 0; row < ROWS_SIZE - 3; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row + 1, col + 1, row + 2, col + 2, row + 3, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }

    // No winner found
    return false;
}

    private boolean checkChips(int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4) {
        ImageView chip1 = findImageViewAtPosition(row1 * COLS_SIZE + col1);
        ImageView chip2 = findImageViewAtPosition(row2 * COLS_SIZE + col2);
        ImageView chip3 = findImageViewAtPosition(row3 * COLS_SIZE + col3);
        ImageView chip4 = findImageViewAtPosition(row4 * COLS_SIZE + col4);

        Drawable drawable1 = chip1.getDrawable();
        Drawable drawable2 = chip2.getDrawable();
        Drawable drawable3 = chip3.getDrawable();
        Drawable drawable4 = chip4.getDrawable();

        return drawable1 != null && drawable2 != null && drawable3 != null && drawable4 != null &&
                drawable1.getConstantState().equals(drawable2.getConstantState()) &&
                drawable2.getConstantState().equals(drawable3.getConstantState()) &&
                drawable3.getConstantState().equals(drawable4.getConstantState()) &&
                !drawable1.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState());
    }

    private void setWinner(int player) {
        winner = player;
        TextView gameOver = findViewById(R.id.gameOver);
        if(winner==0){
            gameOver.setText("Withdraw!");
        }else{
            gameOver.setText("Player " + player + " Wins!");
        }

    }
    private void getDataFromApi() {
        Call<List<Connect4Dto>> call = apiService.getData();
        call.enqueue(new Callback<List<Connect4Dto>>() {
            @Override
            public void onResponse(Call<List<Connect4Dto>> call, Response<List<Connect4Dto>> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    List<Connect4Dto> data = response.body();
                    // Do something with the data
                } else {
                    // Handle error response
                    Log.e("API Error", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Connect4Dto>> call, Throwable t) {
                // Handle network errors
                Log.e("API Error", "Network error: " + t.getMessage());
            }
        });
    }

    private void postDataToApi() {
        Connect4Dto requestData = new Connect4Dto();
        // Set up your request data

        Call<Connect4Dto> call = apiService.postData(requestData);
        call.enqueue(new Callback<Connect4Dto>() {
            @Override
            public void onResponse(Call<Connect4Dto> call, Response<Connect4Dto> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Connect4Dto data = response.body();
                    // Do something with the data
                } else {
                    // Handle error response
                    Log.e("API Error", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Connect4Dto> call, Throwable t) {
                // Handle network errors
                Log.e("API Error", "Network error: " + t.getMessage());
            }
        });
    }

}// end of Main
