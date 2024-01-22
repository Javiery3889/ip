import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public Task getTask(int idx) {
        // 1-based indexing
        return this.tasks.get(idx - 1);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= this.tasks.size(); i++) {
            Task task = getTask(i);
            String title = String.format("%d. %s", i+1, task.toString());
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
