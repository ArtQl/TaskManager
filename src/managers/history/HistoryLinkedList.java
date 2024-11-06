package managers.history;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HistoryLinkedList<T extends Task> {
    private static final int MAX_SIZE = 10;
    private Node<T> head;
    private Node<T> tail;
    private final Map<Integer, Node<T>> historyMap = new HashMap<>();

    public void add(T task) {
        if (task == null)
            throw new IllegalArgumentException("History: task null");

        if (historyMap.containsKey(task.getId())) remove(task.getId());

        if (historyMap.size() == MAX_SIZE) removeFirst();

        if (task instanceof Subtask subtask &&
                historyMap.get(subtask.getIdEpic()) != null) {
            ((Epic) historyMap.get(subtask.getIdEpic()).getData()).addSubtask(subtask);
        }

        Node<T> newNode = new Node<>(task);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }

        historyMap.put(task.getId(), tail);
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }

    public void removeFirst() {
        if (head == null)
            throw new IllegalArgumentException("First node null");
        historyMap.remove(head.getData().getId());
        head = head.getNext();
        if (head != null) {
            head.setPrev(null);
        } else {
            tail = null;
        }
    }

    public void removeLast() {
        if (tail == null)
            throw new IllegalArgumentException("Last node null");
        historyMap.remove(tail.getData().getId());
        tail = tail.getPrev();
        if (tail != null) {
            tail.setNext(null);
        } else {
            head = null;
        }
    }

    private void remove(Node<T> node) {
        if (node.getData() instanceof Epic epic && epic.getSubtaskList() != null) {
            epic.getSubtaskList().keySet().forEach(this::remove);
        }
        if (node.equals(head)) {
            removeFirst();
        } else if (node.equals(tail)) {
            removeLast();
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        historyMap.remove(node.getData().getId());
    }

    public void remove(int id) {
        Node<T> node = historyMap.get(id);
        if (node == null)
            throw new IllegalArgumentException("ID not founded");
        remove(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryLinkedList<?> that = (HistoryLinkedList<?>) o;
        return Objects.equals(head, that.head) && Objects.equals(tail, that.tail) && Objects.equals(historyMap, that.historyMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail, historyMap);
    }
}

class Node<T> {
    private final T data;
    private Node<T> next;
    private Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, next, prev);
    }

    @Override
    public String toString() {
        return "managers.history.Node{" + data +
                '}';
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}