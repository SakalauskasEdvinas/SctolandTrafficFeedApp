package com.gcu.mobilecoursework.model;

// By Edvinas Sakalauskas - S1627176
public enum RoadworkType {
    PLANNED_ROADWORK("Planned Roadwork"), ROADWORK("Roadwork"), INCIDENT("Incident");

    public final String label;

    RoadworkType(String label) {
        this.label = label;
    }
}
