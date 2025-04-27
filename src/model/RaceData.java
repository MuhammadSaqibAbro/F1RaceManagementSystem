/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author SAQIB
 */


public class RaceData {

    private String circuit;
    private int laps;
    private String weather;
    private String raceType;

    public RaceData(String circuit, int laps, String weather, String raceType) {
        this.circuit = circuit;
        this.laps = laps;
        this.weather = weather;
        this.raceType = raceType;
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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRaceType() {
        return raceType;
    }

    public void setRaceType(String raceType) {
        this.raceType = raceType;
    }

    @Override
    public String toString() {
        return "RaceData{" +
                "circuit='" + circuit + '\'' +
                ", laps=" + laps +
                ", weather='" + weather + '\'' +
                ", raceType='" + raceType + '\'' +
                '}';
    }
}

