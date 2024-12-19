package ru.artq.task.managers.memory;

import ru.artq.task.model.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TimeIntervalTracker {
    private final HashMap<Integer, Boolean> intervalMap = new LinkedHashMap<>();

    public TimeIntervalTracker() {
        for (int i = 0; i < 365 * 24 * 4; i++) {
            intervalMap.put(i, false);
        }
    }

    public int getIntervalIndex(LocalDateTime dateTime) {
        LocalDateTime startOfYear = LocalDateTime.of(dateTime.getYear(), 1, 1, 0, 0);

        long minSinceStartYear = ChronoUnit.MINUTES.between(startOfYear, dateTime);

        return (int) (minSinceStartYear / 15);
    }

    public void addTaskInInterval(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty())
            return;
        int startIndex = getIntervalIndex(task.getStartTime().get());
        int endIndex = getIntervalIndex(task.getEndTime().get());
        for (int i = startIndex; i <= endIndex; i++)
            intervalMap.put(i, true);
    }

    public boolean hasOverlap(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty())
            return false;
        int startIndex = getIntervalIndex(task.getStartTime().get());
        int endIndex = getIntervalIndex(task.getEndTime().get());
        for (int i = startIndex; i <= endIndex; i++) {
            if (intervalMap.getOrDefault(i, false)) return true;
        }
        return false;
    }

    public void removeTaskFromInterval(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty())
            return;
        int startIndex = getIntervalIndex(task.getStartTime().get());
        int endIndex = getIntervalIndex(task.getEndTime().get());
        for (int i = startIndex; i <= endIndex; i++)
            intervalMap.put(i, false);
    }

    public HashMap<Integer, Boolean> getIntervalMap() {
        return intervalMap;
    }
}
