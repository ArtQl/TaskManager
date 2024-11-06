package managers.memory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TimeIntervalTracker {
    HashMap<Integer, Boolean> intervalMap = new LinkedHashMap<>();

    public TimeIntervalTracker() {
        for (int i = 0; i < 365 * 24 * 4; i++) {
            intervalMap.put(i, false);
        }
    }

    public int getIntervalIndex(LocalDateTime dateTime) {
        LocalDateTime startOfYear = LocalDateTime.of(dateTime.getYear(), 1,1,0, 0);

        long minSinceStartYear = ChronoUnit.MINUTES.between(startOfYear, dateTime);

        return (int) (minSinceStartYear / 15);
    }

    public void addTaskInInterval(LocalDateTime start, LocalDateTime end) {
        int startIndex = getIntervalIndex(start);
        int endIndex = getIntervalIndex(end);
        for (int i = startIndex; i <= endIndex; i++)
            intervalMap.put(i, true);
    }

    public boolean hasOverlap(LocalDateTime start, LocalDateTime end) {
        int startIndex = getIntervalIndex(start);
        int endIndex = getIntervalIndex(end);
        for (int i = startIndex; i <= endIndex; i++) {
            if (intervalMap.getOrDefault(i, false)) return true;
            // Пересечение найдено
        }
        return false;
    }

    public void removeTaskFromInterval(LocalDateTime start, LocalDateTime end) {
        int startIndex = getIntervalIndex(start);
        int endIndex = getIntervalIndex(end);
        for (int i = startIndex; i <= endIndex; i++)
            intervalMap.put(i, false);
    }
}
