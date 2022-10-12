package com.techelevator.model;

import java.time.LocalDate;

public class MonthlySchedule {

    private int id;
    private int projectId;
    private LocalDate monthYear;
    private String scheduleNotes;
    private int pctComplete;
    private Float scheduleProdIdx;
    private boolean samePrevMonth;
    private String whyOne;
    private String whyTwo;
    private String whyThree;
    private String whyFour;
    private String whyFive;
    private int tasksSubstantial;
    private int tasksConstruction;

    public MonthlySchedule() {
    }

    public MonthlySchedule(int id, int projectId, LocalDate monthYear, String scheduleNotes, int pctComplete, Float scheduleProdIdx, boolean samePrevMonth, String whyOne, String whyTwo, String whyThree, String whyFour, String whyFive, int tasksSubstantial, int tasksConstruction) {
        this.id = id;
        this.projectId = projectId;
        this.monthYear = monthYear;
        this.scheduleNotes = scheduleNotes;
        this.pctComplete = pctComplete;
        this.scheduleProdIdx = scheduleProdIdx;
        this.samePrevMonth = samePrevMonth;
        this.whyOne = whyOne;
        this.whyTwo = whyTwo;
        this.whyThree = whyThree;
        this.whyFour = whyFour;
        this.whyFive = whyFive;
        this.tasksSubstantial = tasksSubstantial;
        this.tasksConstruction = tasksConstruction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public LocalDate getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(LocalDate monthYear) {
        this.monthYear = monthYear;
    }

    public String getScheduleNotes() {
        return scheduleNotes;
    }

    public void setScheduleNotes(String scheduleNotes) {
        this.scheduleNotes = scheduleNotes;
    }

    public int getPctComplete() {
        return pctComplete;
    }

    public void setPctComplete(int pctComplete) {
        this.pctComplete = pctComplete;
    }

    public Float getScheduleProdIdx() {
        return scheduleProdIdx;
    }

    public void setScheduleProdIdx(Float scheduleProdIdx) {
        this.scheduleProdIdx = scheduleProdIdx;
    }

    public boolean isSamePrevMonth() {
        return samePrevMonth;
    }

    public void setSamePrevMonth(boolean samePrevMonth) {
        this.samePrevMonth = samePrevMonth;
    }

    public String getWhyOne() {
        return whyOne;
    }

    public void setWhyOne(String whyOne) {
        this.whyOne = whyOne;
    }

    public String getWhyTwo() {
        return whyTwo;
    }

    public void setWhyTwo(String whyTwo) {
        this.whyTwo = whyTwo;
    }

    public String getWhyThree() {
        return whyThree;
    }

    public void setWhyThree(String whyThree) {
        this.whyThree = whyThree;
    }

    public String getWhyFour() {
        return whyFour;
    }

    public void setWhyFour(String whyFour) {
        this.whyFour = whyFour;
    }

    public String getWhyFive() {
        return whyFive;
    }

    public void setWhyFive(String whyFive) {
        this.whyFive = whyFive;
    }

    public int getTasksSubstantial() {
        return tasksSubstantial;
    }

    public void setTasksSubstantial(int tasksSubstantial) {
        this.tasksSubstantial = tasksSubstantial;
    }

    public int getTasksConstruction() {
        return tasksConstruction;
    }

    public void setTasksConstruction(int tasksConstruction) {
        this.tasksConstruction = tasksConstruction;
    }
}



