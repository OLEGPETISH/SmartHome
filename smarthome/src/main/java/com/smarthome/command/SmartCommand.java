package com.smarthome.command;


public interface SmartCommand {
    void execute();
    void undo();
    String getDescription();
}