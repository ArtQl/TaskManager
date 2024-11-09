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
                historyMap.containsKey(subtask.getIdEpic())) {
            ((Epic) historyMap.get(subtask.getIdEpic()).data).addSubtask(subtask);
        }

        Node<Task> newNode = new Node<>(task);

        if (head == null) {
            head = tail = newNode;
            head.nextNode = head.prevNode = head;
        } else {
            tail.nextNode = newNode;
            newNode.prevNode = tail;
            newNode.nextNode = head;
            tail = head.prevNode = newNode;
        }

        historyMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.get(id);
        if (node == null) throw new IllegalArgumentException("ID not founded");

        if (node.data instanceof Epic epic && epic.getSubtaskList() != null)  {
            epic.getSubtaskList().values().forEach(task -> remove(task.getId()));
            epic.getSubtaskList().clear();
        }

        if (node.equals(head)) {
            removeFirst();
        } else if (node.equals(tail)) {
            removeLast();
        } else {
            node.prevNode.nextNode = node.nextNode;
            node.nextNode.prevNode = node.prevNode;
        }
        historyMap.remove(node.data.getId());
    }

    @Override
    public void removeFirst() {
        if (head == null)
            throw new IllegalArgumentException("First node null");
        historyMap.remove(head.data.getId());

        if (head.nextNode.equals(head)) {
            head = tail = null;
            return;
        }
        head = head.nextNode;
        head.prevNode = tail;
        tail.nextNode = head;
    }

    @Override
    public void removeLast() {
        if (tail == null)
            throw new IllegalArgumentException("Last node null");
        historyMap.remove(tail.data.getId());

        if (tail.nextNode.equals(tail)) {
            head = tail = null;
            return;
        }
        tail = tail.prevNode;
        tail.nextNode = head;
        head.prevNode = tail;
    }

    @Override
    public List<Task> getHistory() {
        if (historyMap.isEmpty()) return new ArrayList<>();
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> node = head;
        do {
            list.add(node.data);
            node = node.nextNode;
        } while (!node.equals(head));

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
        private Node<T> nextNode;
        private Node<T> prevNode;

        public Node(T data) {
            this.data = data;
            this.nextNode = null;
            this.prevNode = null;
        }
    }
}
