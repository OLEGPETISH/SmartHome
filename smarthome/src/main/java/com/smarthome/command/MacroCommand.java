package com.smarthome.command;

import java.util.ArrayList;
import java.util.List;


public class MacroCommand implements SmartCommand {
    private final String name;
    private final List<SmartCommand> commands = new ArrayList<>();

    public MacroCommand(String name) { this.name = name; }

    public MacroCommand add(SmartCommand cmd) { commands.add(cmd); return this; }

    @Override
    public void execute() { commands.forEach(SmartCommand::execute); }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
    }

    @Override
    public String getDescription() { return "Macro[" + name + "] (" + commands.size() + " cmds)"; }
}