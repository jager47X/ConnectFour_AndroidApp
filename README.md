# Connect Four Game Documentation

## Overview
The Connect Four game is a classic two-player connection game where players choose a color and take turns dropping colored discs into a vertically suspended grid. The objective is to connect four discs of the same color consecutively in a line, horizontally, vertically, or diagonally.

## MainActivity Class
### Constants
- COLS_SIZE: The number of columns in the game grid (7).
- ROWS_SIZE: The number of rows in the game grid (6).
- PLAYER_1: Resource ID for the red chip.
- PLAYER_2: Resource ID for the yellow chip.

### Fields
- gridLayout: The GridLayout representing the game board.
- player1Turn: A boolean indicating whether it's Player 1's turn.
- winner: An integer indicating the winner (-1 for no winner, 0 for a draw, 1 for Player 1, 2 for Player 2).
- turn: The current turn number.
- nextTurn: A boolean indicating whether the next turn is ready.

### Methods
- **onCreate(Bundle savedInstanceState):**
  - Initializes the game board.
  - Sets up the reset button click listener.
  - Updates the player turn text view.
- **initializeBoard():**
  - Initializes the game board with a GridLayout, CardViews, and ImageViews.
- **onCellClick(int row, int col):**
  - Handles logic when a cell is clicked.
  - Updates the cell with the corresponding player's chip.
  - Checks for a winner or a draw.
  - Updates the player turn text view.
- **updateCell(int row, int col, int imageResource):**
  - Updates a specific cell with the provided image resource (chip).
- **findImageViewAtPosition(int position):**
  - Finds and returns the ImageView at the specified position on the game board.
- **updatePlayerTurnTextView():**
  - Updates the player turn text view based on the game status.
- **resetGame():**
  - Resets the game by clearing the board and resetting game variables.
- **clearBoard():**
  - Clears the game board by resetting all ImageViews to the "empty" state.
- **CheckRow(int col):**
  - Checks the first available row in a specific column.
- **checkForWinner():**
  - Checks for a winner by examining rows, columns, and diagonals.
- **checkChips(int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4):**
  - Checks if four chips in specified positions are of the same color.
- **setWinner(int player):**
  - Sets the winner and updates the "Game Over" message.

### UI Components
#### Layouts
- GridLayout (board_gridlayout): Represents the game board.
- LinearLayout: Represents a row in the game board.
- CardView: Wraps each cell with a card-like appearance.

#### Views
- ImageViews: Represent game chips (empty, red, yellow).
- TextView (playerTurnTextView): Displays the current player's turn.
- TextView (gameOver): Displays the game result (win, draw).

#### Buttons
- Button (resetButton): Resets the game.

### Game Logic
- Players take turns by clicking on a column to drop their chip.
- The game checks for a winner after each move.
- The game can end in a win, draw, or withdrawal (if the board is full).

### Error Handling
- Checks for invalid moves and logs errors if necessary.

## Conclusion
This documentation provides an overview of the Connect Four game implemented in the MainActivity class. It covers the structure of the code, UI components, game logic, and error handling. Developers can use this documentation to understand, modify, or extend the Connect Four game implementation.
