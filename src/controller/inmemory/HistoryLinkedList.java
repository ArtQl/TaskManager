package controller.inmemory;

import model.Epic;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryLinkedList<T extends Task> {
    private static final int MAX_SIZE = 10;
    private Node<T> head;
    private Node<T> tail;
    private final Map<Integer, Node<T>> historyMap = new HashMap<>();

    public void add(T task) {
        if (task == null) return;
        if (historyMap.containsKey(task.getId())) {
            remove(historyMap.get(task.getId()));
        }
        if (historyMap.size() == MAX_SIZE) {
            removeFirst();
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
        if (head != null) {
            historyMap.remove(head.getData().getId());
            head = head.getNext();
            if (head != null) {
                head.setPrev(null);
            } else {
                tail = null;
            }
        }
    }

    public void removeLast() {
        if (tail != null) {
            historyMap.remove(tail.getData().getId());
            tail = tail.getPrev();
            if (tail != null) {
                tail.setNext(null);
            } else {
                head = null;
            }
        }
    }

    public void remove(Node<T> node) {
        if (node == null) return;

        if(node.getData() instanceof Epic epic && epic.getSubtaskList() != null) {
            for (Integer id : epic.getSubtaskList().keySet()) {
                remove(id);
            }
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
        if(node != null) {
            remove(node);
        } else {
            System.out.println("ID not founded");
        }
    }
}