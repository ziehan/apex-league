package com.apexleague.backend.event;

import org.springframework.context.ApplicationEvent;

public class MatchSavedEvent extends ApplicationEvent {

    private final String username;
    private final int winsDelta;
    private final int goalsDelta;
    private final int savesDelta;
    private final int demosDelta;

    public MatchSavedEvent(Object source, String username, int winsDelta, int goalsDelta, int savesDelta, int demosDelta) {
        super(source);
        this.username = username;
        this.winsDelta = winsDelta;
        this.goalsDelta = goalsDelta;
        this.savesDelta = savesDelta;
        this.demosDelta = demosDelta;
    }

    public String getUsername() {
        return username;
    }

    public int getWinsDelta() {
        return winsDelta;
    }

    public int getGoalsDelta() {
        return goalsDelta;
    }

    public int getSavesDelta() {
        return savesDelta;
    }

    public int getDemosDelta() {
        return demosDelta;
    }
}

