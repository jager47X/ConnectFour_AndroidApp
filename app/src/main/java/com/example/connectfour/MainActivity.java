package com.example.connectfour;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CardView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int COLS_SIZE = 7;
    private static final int ROWS_SIZE = 6;
    private static final int PLAYER_1 = R.drawable.red_chip;
    private static final int PLAYER_2 = R.drawable.yellow_chip;

    private GridLayout gridLayout;
    private boolean player1Turn = true;
    private int winner = -1;
    private boolean nextTurn = false;

    private static final String BASE_URL = "https://your-api-base-url.com/";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Create the ApiService
        apiService = retrofit.create(ApiService.class);

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

    // -------------------------
    //       UI SETUP
    // -------------------------
    private void initializeBoard() {
        gridLayout = findViewById(R.id.board_gridlayout);
        for (int i = 0; i < ROWS_SIZE; i++) {
            for (int j = 0; j < COLS_SIZE; j++) {
                // CardView
                CardView cardView = new CardView(this);
                ViewGroup.LayoutParams viewCardParams = new ViewGroup.LayoutParams(130, 130);
                cardView.setLayoutParams(viewCardParams);

                // LinearLayout
                LinearLayout linearLayout = new LinearLayout(this);
                ViewGroup.LayoutParams linearLayoutParams = new ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(linearLayoutParams);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.boardColor));

                // ImageView
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.empty);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        GridLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                imageView.setLayoutParams(params);

                // Compose views
                linearLayout.addView(imageView);
                cardView.addView(linearLayout);
                gridLayout.addView(cardView);

                // Attach click listener
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

    /**
     * Called when user taps a cell.
     * 1. We try to place the user’s chip.
     * 2. If successful, we send the move to the server.
     * 3. Check winner. If none, request AI move from server.
     */
    private void onCellClick(int row, int col) {
        if (winner != -1) {
            // Game already finished, do nothing
            return;
        }

        // Current player's chip
        int imageResource = player1Turn ? PLAYER_1 : PLAYER_2;
        nextTurn = updateCell(row, col, imageResource);

        if (nextTurn) {
            // Send move to server if cell update is valid
            sendPlayerMove(col);  // <-- EDIT: Add missing semicolon
            if (checkForWinner()) {
                Log.d("Game status", "Game over");
                return; // no need to get AI move if the game just ended
            }
            // Switch turn to AI
            player1Turn = !player1Turn;
            updatePlayerTurnTextView();
            // Now request AI move
            getAIMoveFromServer();
        } 
    }

    /**
     * Attempt to place a chip from top in the given column.
     * It will find the lowest empty row in that column.
     *
     * @param row           The row user clicked (unused except for logging).
     * @param col           The column user clicked.
     * @param imageResource Red/Yellow chip resource.
     * @return true if the cell was updated; false otherwise.
     */
    private boolean updateCell(int row, int col, int imageResource) {
        int position = -1;
        int counter = 0;
        while (position == -1) {
            position = findAvailablePositionInColumn(col);
            counter++;
            if (counter >= ROWS_SIZE) {
                Log.e("Invalid:", position + ", row:" + row + ", col: " + col);
                return false;
            }
        }

        Log.d("Position:", position + ", row:" + row + ", col: " + col);
        ImageView imageView = findImageViewAtPosition(position);
        if (imageView != null) {
            imageView.setImageResource(imageResource);
            return true;
        } else {
            Log.e("UpdateCell", "ImageView is null for row: " + row + ", col: " + col);
            return false;
        }
    }

    /**
     * Return the first empty position (from bottom) in the given column.
     *
     * @param col Column index
     * @return GridLayout child index for the empty slot or -1 if column is full
     */
    private int findAvailablePositionInColumn(int col) {
        for (int row = ROWS_SIZE - 1; row >= 0; row--) {
            int position = row * COLS_SIZE + col;
            ImageView imageView = findImageViewAtPosition(position);
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null
                        && drawable.getConstantState() != null
                        && drawable.getConstantState().equals(
                                getResources().getDrawable(R.drawable.empty).getConstantState())) {
                    return position;
                }
            }
        }
        return -1; // Column is full
    }

    /**
     * Helper method to get the ImageView for a particular child index in the GridLayout.
     */
    private ImageView findImageViewAtPosition(int position) {
        if (position >= 0 && position < gridLayout.getChildCount()) {
            View childview = gridLayout.getChildAt(position);
            if (childview instanceof CardView) {
                CardView cardView = (CardView) childview;
                LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
                return (ImageView) linearLayout.getChildAt(0);
            }
        }
        return null;
    }

    private void updatePlayerTurnTextView() {
        TextView playerTurnTextView = findViewById(R.id.playerTurnTextView);
        if (winner == 1 || winner == 2 || winner == 0) {
            playerTurnTextView.setText("Game Over!");
        } else {
            playerTurnTextView.setText(player1Turn ? "Player 1's Turn" : "Player 2's Turn");
        }
    }

    private void resetGame() {
        // Clear the board
        clearBoard();
        // Reset game variables
        player1Turn = true;
        winner = -1;

        // Clear the "Game Over" message
        TextView gameOver = findViewById(R.id.gameOver);
        gameOver.setText("");

        // Update the player turn text
        updatePlayerTurnTextView();

        // Notify the server
        sendReset();
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

    // -------------------------
    //  WIN CHECKING
    // -------------------------
    private boolean checkForWinner() {
        // Rows
        for (int row = 0; row < ROWS_SIZE; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row, col + 1, row, col + 2, row, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }
        // Cols
        for (int col = 0; col < COLS_SIZE; col++) {
            for (int row = 0; row < ROWS_SIZE - 3; row++) {
                if (checkChips(row, col, row + 1, col, row + 2, col, row + 3, col)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }
        // Diagonals (bottom-left to top-right)
        for (int row = 3; row < ROWS_SIZE; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row - 1, col + 1, row - 2, col + 2, row - 3, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }
        // Diagonals (top-left to bottom-right)
        for (int row = 0; row < ROWS_SIZE - 3; row++) {
            for (int col = 0; col < COLS_SIZE - 3; col++) {
                if (checkChips(row, col, row + 1, col + 1, row + 2, col + 2, row + 3, col + 3)) {
                    setWinner(player1Turn ? 1 : 2);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkChips(int row1, int col1, int row2, int col2,
                               int row3, int col3, int row4, int col4) {
        ImageView chip1 = findImageViewAtPosition(row1 * COLS_SIZE + col1);
        ImageView chip2 = findImageViewAtPosition(row2 * COLS_SIZE + col2);
        ImageView chip3 = findImageViewAtPosition(row3 * COLS_SIZE + col3);
        ImageView chip4 = findImageViewAtPosition(row4 * COLS_SIZE + col4);

        Drawable d1 = (chip1 != null) ? chip1.getDrawable() : null;
        Drawable d2 = (chip2 != null) ? chip2.getDrawable() : null;
        Drawable d3 = (chip3 != null) ? chip3.getDrawable() : null;
        Drawable d4 = (chip4 != null) ? chip4.getDrawable() : null;

        if (d1 == null || d2 == null || d3 == null || d4 == null) return false;

        // Compare the four drawables
        if (d1.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState())) {
            return false; // skip if empty
        }
        return d1.getConstantState().equals(d2.getConstantState())
                && d2.getConstantState().equals(d3.getConstantState())
                && d3.getConstantState().equals(d4.getConstantState());
    }

    private void setWinner(int player) {
        winner = player;
        TextView gameOver = findViewById(R.id.gameOver);
        if (winner == 0) {
            gameOver.setText("Withdraw!");
        } else {
            gameOver.setText("Player " + player + " Wins!");
        }
    }

    // -------------------------
    //   API METHODS
    // -------------------------

    /**
     * Send the player's move (column) to the server.
     */
    private void sendPlayerMove(int col) {
        PlayerMove playerMove = new PlayerMove(col);
        apiService.sendPlayerMove(playerMove).enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameResponse gameResponse = response.body();
                    Log.d("API", "Player move sent successfully");
                    // Optionally update local board from server state
                    updateBoardUI(gameResponse.getBoard());
                } else {
                    Log.e("API Error", "Failed to send player move.");
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }

    /**
     * Request the AI move from the server.
     * The server response should tell us which column the AI chose.
     * Then we place the AI's chip.
     */
    private void getAIMoveFromServer() {
        apiService.getAIMove().enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameResponse gameResponse = response.body();
                    Log.d("API", "AI move retrieved successfully");

                    // If your API response includes which column AI chose, place the chip:
                    int aiChosenCol = gameResponse.getAiChosenColumn(); // e.g. parse from your JSON
                    updateCell(0, aiChosenCol, PLAYER_2); 
                    
                    // Optionally update local board from server response
                    updateBoardUI(gameResponse.getBoard());

                    // Check if AI’s move caused a win
                    if (checkForWinner()) {
                        Log.d("Game status", "Game over");
                        return;
                    }

                    // If still no winner, switch turn back to player
                    player1Turn = true;
                    updatePlayerTurnTextView();
                } else {
                    Log.e("API Error", "Failed to fetch AI move.");
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }

    /**
     * Reset the game on the server side, then reset locally.
     */
    private void sendReset() {
        apiService.sendReset().enqueue(new Callback<GameStatus>() {
            @Override
            public void onResponse(Call<GameStatus> call, Response<GameStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameStatus gameStatus = response.body();
                    Log.d("API", "Game reset successfully");
                    updateBoardUI(gameStatus.getBoard()); // If the server returns the new empty board
                } else {
                    Log.e("API Error", "Failed to reset the game.");
                }
            }

            @Override
            public void onFailure(Call<GameStatus> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }

    /**
     * Whenever the server returns a board state, you can optionally refresh your UI.
     */
    private void updateBoardUI(List<List<Integer>> board) {
        if (board == null) return;
        // Example logic to update your UI with the latest server state
        // For a 2D board of size 6x7, board.get(row).get(col) might be 0=empty,1=red,2=yellow
        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.get(row).size(); col++) {
                int cellValue = board.get(row).get(col);
                int position = row * COLS_SIZE + col;
                ImageView imageView = findImageViewAtPosition(position);
                if (imageView == null) continue;

                switch (cellValue) {
                    case 0:
                        imageView.setImageResource(R.drawable.empty);
                        break;
                    case 1:
                        imageView.setImageResource(PLAYER_1);
                        break;
                    case 2:
                        imageView.setImageResource(PLAYER_2);
                        break;
                    default:
                        // Handle unexpected value
                        break;
                }
            }
        }
    }
}
