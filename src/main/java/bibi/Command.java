package bibi;

import java.io.IOException;

import bibi.task.Deadline;
import bibi.task.Event;
import bibi.task.Task;
import bibi.task.TaskList;
import bibi.task.ToDo;

/**
 * Represents the object that contains the command to the executed, and their relevant parameters.
 * Command to be executed has differing behaviour depending on the command.
 */
public class Command {
    private String cmd;
    private String args;

    /**
     * Constructs a new Command with specified command and relevant parameters.
     *
     * @param cmd The command to execute.
     * @param args The additional information needed to execute the command.
     */
    public Command(String cmd, String args) {
        this.cmd = cmd;
        this.args = args;
    }

    /**
     * Returns a boolean value indicating whether the to exit the program or not.
     *
     * @return isExit
     */
    public boolean isExit() {
        return cmd.equals("bye");
    }

    /**
     * Executes the command based on the command type.
     *
     * @param tasks The list of tasks in the save file.
     * @param ui The Ui instance that is handling the inputs and outputs of the console.
     * @param storage The Storage instance handling modification of the save file.
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int index;
        // Preconfigured commands
        switch (cmd) {
        case "bye":
            // Exit
            ui.printExitMessage();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "list":
            ui.printListMessage(tasks);
            break;
        case "mark":
            ui.printHorizontalLine();
            if (!args.matches("\\d+")) {
                ui.printInvalidSyntaxMessage("Please use \"mark <int>\"");
            } else if ((index = Integer.parseInt(args)) - 1 >= tasks.getTaskCount() || index <= 0) {
                System.out.println("Invalid task index");
            } else {
                Task t = tasks.getTask(index - 1);
                t.markAsDone();
                ui.printTaskMarkedMessage(t);
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "unmark":
            ui.printHorizontalLine();
            if (!args.matches("\\d+")) {
                ui.printInvalidSyntaxMessage("Please use \"unmark <int>\"");
            } else if ((index = Integer.parseInt(args)) - 1 >= tasks.getTaskCount() || index <= 0) {
                System.out.println("Invalid task index");
            } else {
                Task t = tasks.getTask(index - 1);
                t.markAsNotDone();
                ui.printTaskUnmarkedMessage(t);
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "todo":
            ui.printHorizontalLine();
            if (!args.matches(".+")) {
                ui.printInvalidSyntaxMessage("Please use \"todo <description>\"");
            } else {
                ToDo td = new ToDo(args.stripIndent());
                tasks.addToTaskList(td);

                ui.printTaskAddedMessage(td, tasks.getTaskCount());
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "deadline":
            ui.printHorizontalLine();
            if (!args.matches(".+ /by .+")) {
                ui.printInvalidSyntaxMessage("Please use \"deadline <description> /by <deadline>\"");
            } else {
                String[] input = args.split(" /by ");
                Deadline dl = new Deadline(input[0].stripIndent(), input[1]);
                tasks.addToTaskList(dl);

                ui.printTaskAddedMessage(dl, tasks.getTaskCount());
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "event":
            ui.printHorizontalLine();
            if (!args.matches(".+ /from .+ /to .+")) {
                ui.printInvalidSyntaxMessage("Please use \"event <description> /from <time> /to <time>\"");
            } else {
                String[] input = args.split(" /from ");
                String[] interval = input[1].split(" /to ");
                Event e = new Event(input[0].stripIndent(), interval[0], interval[1]);
                tasks.addToTaskList(e);

                ui.printTaskAddedMessage(e, tasks.getTaskCount());
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "remove":
            ui.printHorizontalLine();
            if (!args.matches("\\d+")) {
                ui.printInvalidSyntaxMessage("Please use \"remove <index>\"");
            } else {
                Task t = tasks.removeFromTaskList(Integer.parseInt(args));
                if (t == null) {
                    System.out.println("Invalid task index");
                } else {
                    ui.printTaskRemovedMessage(t, tasks.getTaskCount());
                }
            }
            ui.printHorizontalLine();

            try {
                storage.writeToFile(tasks);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            break;
        case "find":
            ui.printHorizontalLine();

            // No pattern specified
            if (args.isEmpty()) {
                ui.printInvalidSyntaxMessage("Please use \"find <pattern>\"");
            } else {
                Task task;
                int count = 0;
                for (int i = 1; i <= tasks.getTaskCount(); i++) {
                    if ((task = tasks.getTask(i - 1)).getDescription().contains(args)) {
                        if (count == 0) {
                            System.out.println("Here are the matching tasks I found:");
                        }
                        System.out.printf("%d: %s%n", i, task);
                        count++;
                    }
                }

                // Message for when there are no matching tasks
                if (count == 0) {
                    System.out.println("No matching tasks found. Paranoid?");
                }
            }
            ui.printHorizontalLine();

            break;
        default:
            ui.printUnknownCommandMessage(cmd);
        }
    }
}
