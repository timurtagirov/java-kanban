package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node<Task>> taskHistory = new HashMap<>();
    int firstNode = -1;
    int lastNode = -1;
    public static final int MAX_HISTORY_SIZE = 10;

    public void linkLast(Task task) {
        taskHistory.put(task.getId(), new Node(task));
        if (taskHistory.size() > 1) {
            taskHistory.get(lastNode).next = taskHistory.get(task.getId());
            taskHistory.get(task.getId()).prev = taskHistory.get(lastNode);
        } else {
            firstNode = task.getId();
        }
        lastNode = task.getId();
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (taskHistory.isEmpty()) return tasks;
        Node<Task> node = taskHistory.get(firstNode);
        tasks.add(node.data);
        boolean check1 = node.next != null;
        boolean check2 = taskHistory.get(firstNode).next != null;
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
            firstNode = -1;
            lastNode = -1;
            return;
        }
        if (taskHistory.get(node.data.getId()).prev == null) {
            taskHistory.get(node.data.getId()).next.prev = null;
            firstNode = taskHistory.get(node.data.getId()).next.data.getId();
            taskHistory.remove(node.data.getId());
        } else if (taskHistory.get(node.data.getId()).next == null) {
            taskHistory.get(node.data.getId()).prev.next = null;
            lastNode = taskHistory.get(node.data.getId()).prev.data.getId();
            taskHistory.remove(node.data.getId());
        }  else {
            taskHistory.get(node.data.getId()).prev.next = taskHistory.get(node.data.getId()).next;
            taskHistory.get(node.data.getId()).next.prev = taskHistory.get(node.data.getId()).prev;
            taskHistory.remove(node.data.getId());
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        Task newTask = task.copy();
        removeNode(new Node<Task>(newTask));
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
