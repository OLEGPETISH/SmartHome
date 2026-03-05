package com.smarthome.builder;
public class ScenarioDirector {

    private AutomationScenario.Builder builder;


    public ScenarioDirector(AutomationScenario.Builder builder) {
        this.builder = builder;
    }

    public void setBuilder(AutomationScenario.Builder builder) {
        this.builder = builder;
    }


    public AutomationScenario buildNightMode(String room) {
        System.out.println("[Director] Building Night Mode for: " + room);
        return builder
                .description("Ночной режим — тишина и безопасность")
                .triggerAtTime("22:30")
                .turnOffRoom(room)
                .setBrightness("LAMP_" + room, 10)
                .setTemperature("THERMO_" + room, 19.0)
                .withDelay(300)
                .build();
    }



    public AutomationScenario buildMorningMode(String room) {
        System.out.println("[Director] Building Morning Mode for: " + room);
        return builder
                .description("Утренний режим — бодрость и свет")
                .triggerAtTime("07:00")
                .turnOnRoom(room)
                .setBrightness("LAMP_" + room, 100)
                .setTemperature("THERMO_" + room, 22.0)
                .withDelay(500)
                .build();
    }


    public AutomationScenario buildAwayMode() {
        System.out.println("[Director] Building Away Mode");
        return builder
                .description("Режим отсутствия — безопасность и экономия")
                .triggerOnEvent("departure")
                .turnOffAll()
                .withDelay(200)
                .build();
    }


    public AutomationScenario buildMovieMode(String room) {
        System.out.println("[Director] Building Movie Mode for: " + room);
        return builder
                .description("Кино-режим — атмосфера кинотеатра")
                .triggerOnEvent("movie_start")
                .setBrightness("LAMP_" + room, 15)
                .setTemperature("THERMO_" + room, 21.0)
                .withDelay(100)
                .build();
    }


    public AutomationScenario buildMinimal() {
        System.out.println("[Director] Building minimal scenario");
        return builder
                .description("Базовый сценарий")
                .build();
    }
}
