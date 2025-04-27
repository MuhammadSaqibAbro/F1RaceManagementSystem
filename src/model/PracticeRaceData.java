/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author SAQIB
 */


public class PracticeRaceData {

    private String circuit;
    private int laps;
    private String conditions;
    private String sessionType;

    public PracticeRaceData(String circuit, int laps, String conditions, String sessionType) {
        this.circuit = circuit;
        this.laps = laps;
        this.conditions = conditions;
        this.sessionType = sessionType;
    }

    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    @Override
    public String toString() {
        return circuit; // Display circuit name in JList
    }
}
