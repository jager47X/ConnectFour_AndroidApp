package com.example.connectfour;


import java.util.List;

public class Connect4Dto{

    List<Integer> action;

    public int getLastAction() {
        return action.get(action.size()-1);
    }
    public void addAction(int newAction) {
        action.add(newAction);
    }

}
