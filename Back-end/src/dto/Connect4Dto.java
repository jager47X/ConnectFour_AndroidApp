package dto;

import target.Connect4;

import java.util.List;

public class Connect4Dto extends BaseDto{
    public Connect4Dto(Connect4 game) {
        super(game);
    }
    List<Integer> action;
    boolean isGameOver;

    public List<Integer> getAction() {
        return action;
    }
    public void clearAction(){
        this.action.clear();
    }
    public void setAction(List<Integer> action) {
        this.action = action;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public int getLastAction() {
        return action.get(action.size()-1);
    }
    public void addAction(int newAction) {
        action.add(newAction);
    }


}
