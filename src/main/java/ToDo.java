public class ToDo extends Task {

    public ToDo(String task) {
        // Call Task constructor
        super(task);
    }

    @Override
    public String toString() {
        return String.format("[T] %s", super.toString());
    }
}
