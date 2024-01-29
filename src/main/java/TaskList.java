import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Task getTask(int idx) {
        // 1-based indexing
        return this.tasks.get(idx - 1);
    }

    public List<Task> getAllTasks() {
        return this.tasks;
    }

    public int getNumberOfTasks() {
        return this.tasks.size();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(int taskIdx) {
        this.tasks.remove(taskIdx);
    }

    @Override
    public String toString() {
        if (this.tasks.isEmpty()) {
            return "All tasks completed!";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= this.tasks.size(); i++) {
            Task task = getTask(i);
            String title = String.format("%d. %s", i, task.toString());
            // do not add new line when for last task in list
            if (i == this.tasks.size()) {
                stringBuilder.append(title); 
            } else {
                stringBuilder.append(title + "\n");
            }
        }
        return stringBuilder.toString();
    }
}