package com.jakub.vaadin.sportstracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jakub.vaadin.sportstracker.domain.RunningLog;

public class RunningLogManager {

    private static List<RunningLog> db = new ArrayList<>();

    private double averageTime = 0.0;

    public void addLog(RunningLog log, String username) {
        RunningLog p = new RunningLog();
        if (averageTime != 0.0) {
            p = new RunningLog(log.getDay(), log.getWeek(), log.getYear(), log.getDistance(), log.getActualTime(), averageTime, username);
        } else {
            p = new RunningLog(log.getDay(), log.getWeek(), log.getYear(), log.getDistance(), log.getActualTime(), log.getActualTime(), username);
        }

        p.setId(UUID.randomUUID());
        db.add(p);
        updateAverage();
    }

    public List<RunningLog> findAll() {
        return db;
    }

    public void delete(RunningLog person) {

        RunningLog toRemove = null;
        for (RunningLog p : db) {
            if (p.getId().compareTo(person.getId()) == 0) {
                toRemove = p;
                break;
            }
        }
        db.remove(toRemove);
        updateAverage();
    }

    public void updateLog(RunningLog log) {
        for(RunningLog p: db){
            if (p.getId().compareTo(log.getId()) == 0) {
                db.remove(p);
                break;
            }
        }
        db.add(log);
        updateAverage();
    }

    private void updateAverage() {
        Double sum = 0.0;
        for (RunningLog rl : db) {
            sum += rl.getActualTime();
        }
        averageTime = sum / db.size();
    }

    /**
     * @return the averageTime
     */
    public double getAverageTime() {
        return averageTime;
    }

    /**
     * @param averageTime the averageTime to set
     */
    public void setAverageTime(double averageTime) {
        this.averageTime = averageTime;
    }
    
    

}
