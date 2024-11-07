package managers.history;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private static final int MAX_SIZE = 10;
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public String toString() {
        return "InMemoryHistoryManager{historyTasks=" + historyMap + '}';
    }

    @Override
    public void add(Task task) {
        if (task == null) throw new IllegalArgumentException("History: task null");

        if (historyMap.containsKey(task.getId())) remove(task.getId());

        if (historyMap.size() == MAX_SIZE) removeFirst();

        if (task instanceof Subtask subtask &&
                historyMap.get(subtask.getIdEpic()) != null) {
            ((Epic) historyMap.get(subtask.getIdEpic()).data).addSubtask(subtask);
        }

        Node<Task> newNode = new Node<>(task);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        historyMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.get(id);
        if (node == null)
            throw new IllegalArgumentException("ID not founded");
        remove(node);
    }

    @Override
    public void removeFirst() {
        if (head == null)
            throw new IllegalArgumentException("First node null");
        historyMap.remove(head.data.getId());
        head = head.next;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
    }

    @Override
    public void removeLast() {
        if (tail == null)
            throw new IllegalArgumentException("Last node null");
        historyMap.remove(tail.data.getId());
        tail = tail.prev;
        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }
    }

    private void remove(Node<Task> node) {
        if (node.data instanceof Epic epic && epic.getSubtaskList() != null) {
            epic.getSubtaskList().keySet().forEach(this::remove);
        }
        if (node.equals(head)) {
            removeFirst();
        } else if (node.equals(tail)) {
            removeLast();
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        historyMap.remove(node.data.getId());
    }


    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(historyMap, that.historyMap) && Objects.equals(head, that.head) && Objects.equals(tail, that.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(historyMap, head, tail);
    }

    private static class Node<T extends Task> {
        private final T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
}
