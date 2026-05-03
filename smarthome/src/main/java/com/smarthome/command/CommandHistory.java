package com.smarthome.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

/**
 * Хранит историю команд, поддерживает undo/redo.
 * Invoker в терминах паттерна Command.
 */
public class CommandHistory {

    private static final CommandHistory INSTANCE = new CommandHistory();
    public static CommandHistory getInstance() { return INSTANCE; }

    private final Deque<SmartCommand> history = new ArrayDeque<>();
    private final Deque<SmartCommand> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;

    public void execute(SmartCommand command) {
        command.execute();
        history.push(command);
        redoStack.clear();          // новая команда сбрасывает redo
        if (history.size() > MAX_HISTORY) {
            // удаляем самую старую
            List<SmartCommand> tmp = new ArrayList<>(history);
            tmp.remove(tmp.size() - 1);
            history.clear();
            tmp.forEach(history::push);
        }
    }

    public boolean undo() {
        if (history.isEmpty()) return false;
        SmartCommand cmd = history.pop();
        cmd.undo();
        redoStack.push(cmd);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        SmartCommand cmd = redoStack.pop();
        cmd.execute();
        history.push(cmd);
        return true;
    }

    public boolean canUndo() { return !history.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public List<String> getHistoryDescriptions() {
        return history.stream().map(SmartCommand::getDescription).toList();
    }
}