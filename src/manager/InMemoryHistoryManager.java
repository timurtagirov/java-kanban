package manager;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node<Task>> taskHistory = new HashMap<>();
    Node<Task> firstNode;
    Node<Task> lastNode;

    public void linkLast(Task task) {
        Node<Task> newNode = new Node(task);
        taskHistory.put(task.getId(), newNode);
        if (taskHistory.size() > 1) {
            lastNode.next = newNode;
            newNode.prev = lastNode;
        } else {
            firstNode = newNode;
        }
        lastNode = newNode;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (taskHistory.isEmpty()) return tasks;
        Node<Task> node = firstNode;
        tasks.add(node.data);
        while (node.next != null) {
            node = node.next;
            tasks.add(node.data);
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        if (!taskHistory.containsKey(node.data.getId())) return;  // Проверяем наличие такого узла по id задачи
        if (taskHistory.size() == 1) {
            taskHistory.remove(node.data.getId());
            firstNode = null;
            lastNode = null;
            return;
        }
        if (node.data.getId() == firstNode.data.getId()) {  // Если удаляем первый узел
            firstNode.next.prev = null;
            firstNode = firstNode.next;
        } else if (node.data.getId() == lastNode.data.getId()) {  // Если удаляем последний узел
            lastNode.prev.next = null;
            lastNode = lastNode.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        taskHistory.remove(node.data.getId());
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        Task newTask = task.copy();
        if (taskHistory.containsKey(task.getId())) {
            removeNode(taskHistory.get(task.getId()));
        }
        linkLast(newTask);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (!taskHistory.containsKey(id)) return;
        Node<Task> deletedNode = taskHistory.get(id);
        removeNode(deletedNode);
    }
}
