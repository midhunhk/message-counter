package com.ae.apps.messagecounter.models;

/**
 * Represents a cycle model
 */
public class Cycle {
    long startDateIndex;

    long endDateIndex;

    public Cycle(long startDateIndex, long endDateIndex) {
        this.startDateIndex = startDateIndex;
        this.endDateIndex = endDateIndex;
    }

    public long getStartDateIndex() {
        return startDateIndex;
    }

    public void setStartDateIndex(long startDateIndex) {
        this.startDateIndex = startDateIndex;
    }

    public long getEndDateIndex() {
        return endDateIndex;
    }

    public void setEndDateIndex(long endDateIndex) {
        this.endDateIndex = endDateIndex;
    }

}