package com.example.connectfour;


import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int COLS_SIZE = 6;
    private static final int ROWS_SIZE = 7;
    private static final int  PLAYER_1 = R.drawable.red_chip;
    private  static final int  PLAYER_2 =  R.drawable.yellow_chip;
    class Tile
    {
        Tile() {
            OwnerOfTile = 0;
            turnOfTile = 0;
            locOfTile = 0;
        }


        void SetLocation(int colmn,int row) {//no usage
            row++;
            colmn++;
            String strR,strC;
            strR=Integer.toString(row);
            strC=Integer.toString(colmn);
            strR=strC+strR;
            this.locOfTile=Integer.parseInt(strR);
        }
        int GetLocation(){//no usage
            return locOfTile;
        }

        int locOfTile;
        int OwnerOfTile;
        int turnOfTile;//no usage
        //int winRatioOfTile;for expansion for more info on board **1
    };
    private Tile[][] Board = new Tile[ROWS_SIZE][COLS_SIZE];
    private boolean player1Turn = true;

    private  int winner = -1;
    private  int turn = 1;


    private Boolean WinCheck(int activePlayer)
    {
        for (int BOARD_COLUMN = COLS_SIZE-1; BOARD_COLUMN >=0 ; BOARD_COLUMN--) {
            for (int BOARD_ROW = ROWS_SIZE - 1; BOARD_ROW >= 0; BOARD_ROW--) {
                if (
                        (Board[BOARD_COLUMN][BOARD_ROW].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN-1][BOARD_ROW].OwnerOfTile== activePlayer) &&
                                (Board[BOARD_COLUMN-2][BOARD_ROW].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN-3][BOARD_ROW].OwnerOfTile == activePlayer)
                ) {
                    if (activePlayer ==PLAYER_1) {
                        winner = 1;
                    }
                    else if (activePlayer == PLAYER_2) {
                        winner = 2;
                    }
                    return true;
                }
                if (
                        (Board[BOARD_COLUMN][BOARD_ROW].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN ][BOARD_ROW-1].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN][BOARD_ROW-2].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN] [BOARD_ROW-3].OwnerOfTile == activePlayer)
                ) {
                    if (activePlayer == PLAYER_1) {
                        winner = 1;
                    }
                    else if (activePlayer == PLAYER_2) {
                        winner = 2;
                    }
                    return true;
                }
                if (
                        (Board[BOARD_COLUMN][BOARD_ROW].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN - 1][BOARD_ROW + 1].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN - 2][BOARD_ROW + 2].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN - 3][BOARD_ROW + 3].OwnerOfTile == activePlayer)
                ) {
                    if (activePlayer == PLAYER_1) {
                        winner = 1;
                    }
                    else if (activePlayer == PLAYER_2) {
                        winner = 2;
                    }
                    return true;
                }
                if (
                        (Board[BOARD_COLUMN][BOARD_ROW].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN + 1][BOARD_ROW - 1].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN + 2][BOARD_ROW - 2].OwnerOfTile == activePlayer) &&
                                (Board[BOARD_COLUMN + 3][BOARD_ROW - 3].OwnerOfTile == activePlayer)
                ) {
                    if (activePlayer == PLAYER_1) {
                        winner = 1;
                    }
                    else if (activePlayer == PLAYER_2) {
                        winner = 2;
                    }
                    return true;

                }

            }
        }

        winner=0;
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void initializeBoard() {
        GridLayout gridLayout = findViewById(R.id.board_layout_include).findViewById(R.id.board_gridlayout);

        for (int i = 0; i < ROWS_SIZE; i++) {
            for (int j = 0; j < COLS_SIZE; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.red_chip);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 50; // Set your cell width
                params.height = 50; // Set your cell height
                params.setGravity(Gravity.CENTER);
                imageView.setLayoutParams(params);
                final int row = i;
                final int col = j;

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick(row, col);
                    }
                });
                gridLayout.addView(imageView);
            }
        }

    }

    private void onCellClick(int row, int col) {
        // Handle the cell click logic (update the board, check for a win, etc.)
        TextView gameOver=(TextView) findViewById(R.id.gameOver);
        isBoardFull();//check booard is full
        if(winner==1){
            gameOver.setText("GameOver Player 1 Won" );
        }else if (winner==2){
            gameOver.setText("GameOver Player 2 Won" );
        }else if (winner==-1){
            gameOver.setText("GameOver Withdraw" );
        }

        // Implement your game logic here

        // Example: Update the image based on the player's turn
        int imageResource = player1Turn ? R.drawable.red_chip : R.drawable.yellow_chip;
        updateCell(row, col, imageResource);

        // Switch turns
        player1Turn = !player1Turn;

        // Update the playerTurnTextView
        updatePlayerTurnTextView();
    }

    private void updateCell(int row, int col, int imageResource) {
        GridLayout gridLayout = findViewById(R.id.board_layout_include).findViewById(R.id.board_gridlayout);
        ImageView imageView = (ImageView) gridLayout.getChildAt(row * COLS_SIZE + col);
        if (imageView != null) {
            imageView.setImageResource(imageResource);
        } else {
            Log.e("UpdateCell", "ImageView is null for row: " + row + ", col: " + col);
        }
    }

    private void updatePlayerTurnTextView() {
        TextView playerTurnTextView = findViewById(R.id.playerTurnTextView);
        playerTurnTextView.setText(player1Turn ? "Player 1's Turn" : "Player 2's Turn");
    }

    private void resetGame() {
        // Reset the game board
        for (int BOARD_COLUMN = 0;  BOARD_COLUMN<COLS_SIZE; BOARD_COLUMN++)
        {
            for (int BOARD_ROW = 0; BOARD_ROW <ROWS_SIZE; BOARD_ROW++)
            {

                Board[BOARD_COLUMN][BOARD_ROW].OwnerOfTile=0;
                Board[BOARD_COLUMN][BOARD_ROW].turnOfTile = 0;
            }
        }
        // Reset the game board and UI
        // Implement your reset logic here
        initializeBoard();
        player1Turn = true;
        updatePlayerTurnTextView();
    }
    private void isBoardFull() {

        int full = 0;
        for (int BOARD_ROW = 0; BOARD_ROW <= ROWS_SIZE+1; BOARD_ROW++) {
            if( Board[0][BOARD_ROW].OwnerOfTile!=0)
                full++;
        }
        if (full >= ROWS_SIZE) {
            winner=-1;
        }

    }
}
