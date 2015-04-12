package com.jakub.vaadin.sportstracker.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class RunningLog {

    private UUID id;
    @Min(1)
    @Max(7)
    @NotNull
    private int day;
    @Min(1)
    @Max(52)
    @NotNull
    private int week;
    @Min(15)
    @Max(99)
    @NotNull
    private int year;
    @NotNull
    private double distance;
    @NotNull
    private double actualTime;

    private double expectedTime;
    
    private String username;

    public RunningLog(int day, int week, int year, double distance, double actualTime, double expectedTime, String username){
        super();
        
        this.day = day;
        this.week = week;
        this.year = year;
        this.distance = distance;
        this.actualTime = actualTime;
        this.expectedTime = expectedTime;
        this.username = username;
    }

    public RunningLog(int day, int week, int year, double distance, double actualTime, double expectedTime) {
        super();
        this.day = day;
        this.week = week;
        this.year = year;
        this.distance = distance;
        this.actualTime = actualTime;
        this.expectedTime = expectedTime;
    }

    
    
    public RunningLog() {
    }

    @Override
    public String toString() {
        return "TODO";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the week
     */
    public int getWeek() {
        return week;
    }

    /**
     * @param week the week to set
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the actualTime
     */
    public double getActualTime() {
        return actualTime;
    }

    /**
     * @param actualTime the actualTime to set
     */
    public void setActualTime(double actualTime) {
        this.actualTime = actualTime;
    }

    /**
     * @return the expectedTime
     */
    public double getExpectedTime() {
        return expectedTime;
    }

    /**
     * @param expectedTime the expectedTime to set
     */
    public void setExpectedTime(double expectedTime) {
        this.expectedTime = expectedTime;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
	
}
