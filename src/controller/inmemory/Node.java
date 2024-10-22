package controller.inmemory;

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

        if (data != null ? !data.equals(node.data) : node.data != null)
            return false;
        if (next != null ? !next.equals(node.next) : node.next != null)
            return false;
        return prev != null ? prev.equals(node.prev) : node.prev == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (next != null ? next.hashCode() : 0);
        result = 31 * result + (prev != null ? prev.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node{" + data +
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