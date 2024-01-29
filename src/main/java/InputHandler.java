import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import exceptions.BluException;
import exceptions.InvalidCommandException;
import exceptions.IllegalCommandException;

public class InputHandler {
    private static final String BY_PARAM = "/by";
    private static final String FROM_PARAM = "/from";
    private static final String TO_PARAM = "/to";
    private static final DateTimeFormatter INPUT_DATETIMEFORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private int findParamIdx(String[] tokens, String param) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(param)) {
                return i;
            }
        }
        return -1;
    }

    private String getParamValue(String[] tokens, int paramIdx, int endIdx) {
        String[] subArray = Arrays.copyOfRange(tokens, paramIdx + 1, endIdx);
        String paramVal = String.join(" ", subArray);
        return paramVal;
    }

    private boolean isParamEmpty(int startIdx, int endIdx) {
        if (startIdx + 1 == endIdx) {
            return true;
        }
        return false;
    }

    private boolean iskNumberOfParamCorrect(String[] tokens, int expectedNumber) {
        if (tokens.length == expectedNumber) {
            return true;
        }
        return false;
    }

    private CommandType parseCommandType(String cmdString) {
        // Solution below adapted by https://stackoverflow.com/questions/4936819/java-check-if-enum-contains-a-given-string
        try {
            return CommandType.valueOf(cmdString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    public void handleInput(String userInput, Chatbot bot) throws BluException {
        String[] tokens = userInput.trim().split(" ");
        CommandType cmd = parseCommandType(tokens[0]);
        switch (cmd) {
            case LIST:
                bot.displayTasks();
                break;
            case MARK:
                if (!iskNumberOfParamCorrect(tokens, 2)) {
                    throw new IllegalCommandException("Please specify a task number to mark\n"
                                                            + "Usage: mark <task_number>");
                }
                try {
                    int markTaskIdx = Integer.parseInt(tokens[1]);
                    bot.markTask(markTaskIdx);

                } catch (NumberFormatException e) {
                    throw new IllegalCommandException(tokens[1] + " is not a valid integer!\n" 
                                                            + "Usage: mark <task_number>");
                }
                break;
            case UNMARK:
                if (!iskNumberOfParamCorrect(tokens, 2)) {
                    throw new IllegalCommandException("Please specify a task number to unmark\n"
                                                            + "Usage: unmark <task_number>");
                }
                try {
                    int unmarkTaskIdx = Integer.parseInt(tokens[1]);
                    bot.unmarkTask(unmarkTaskIdx);
                } catch (NumberFormatException e) {
                    throw new IllegalCommandException(tokens[1] + " is not a valid integer!\n" 
                                                            + "Usage: unmark <task_number>");
                }
                break;
            case TODO:
                if (tokens.length < 2) {
                    throw new IllegalCommandException("Description of a todo cannot be empty.\n"
                                                            + "Usage: todo <task_title>");
                }
                String todoTitle = userInput.substring(cmd.toString().length() + 1);
                bot.addTask(new ToDo(todoTitle));
                break;
            case DEADLINE:
                int baseIdx = 0;
                int paramIdx = findParamIdx(tokens, BY_PARAM);
                if (paramIdx == -1) {
                    throw new IllegalCommandException(BY_PARAM + " parameter not found!\n"
                                                            + "Usage: deadline <task_title> /by <datetime>");
                } 
                if (isParamEmpty(baseIdx, paramIdx)) {
                    throw new IllegalCommandException("Description of a deadline cannot be empty.\n"
                    + "Usage: deadline <task_title> /by <datetime>");
                }
                String deadlineTitle = getParamValue(tokens, baseIdx, paramIdx);
                if (isParamEmpty(paramIdx, tokens.length)) {
                    throw new IllegalCommandException("Datetime of deadline cannot be empty.\n"
                                                            + "Usage: deadline <task_title> /by <datetime>");
                }
                String byStr = getParamValue(tokens, paramIdx, tokens.length);
                try {
                    LocalDateTime byDateTime = LocalDateTime.parse(byStr, INPUT_DATETIMEFORMAT);
                    bot.addTask(new Deadline(deadlineTitle, byDateTime));
                } catch (DateTimeParseException e) {
                    throw new IllegalCommandException("Invalid DateTime format.\n"
                                                        + "Please use dd-MM-yyyy format.");
                }
                break;
            case EVENT:
                baseIdx = 0;
                int fromParamIdx = findParamIdx(tokens, FROM_PARAM);
                int toParamIdx = findParamIdx(tokens, TO_PARAM);
                if (fromParamIdx == -1 || toParamIdx == -1) {
                    throw new IllegalCommandException(FROM_PARAM + " or " + TO_PARAM + " not found!\n" 
                                                            + "Usage: event <task_title> /from <datetime> /to <datetime>");            
                } 
                
                if (isParamEmpty(baseIdx, fromParamIdx)) {
                    throw new IllegalCommandException("Description of event cannot be empty.\n"
                                                            + "Usage: event <task_title> /from <datetime> /to <datetime>");

                }
                String eventTitle = getParamValue(tokens, 0, fromParamIdx);
                if (isParamEmpty(fromParamIdx, toParamIdx) || isParamEmpty(toParamIdx, tokens.length)) {
                    throw new IllegalCommandException("Datetimes of event cannot be empty.\n"
                                                            + "Usage: event <task_title> /from <datetime> /to <datetime>");
                }
                String fromStr = getParamValue(tokens, fromParamIdx, toParamIdx);
                try {
                    LocalDateTime fromDateTime = LocalDateTime.parse(fromStr, INPUT_DATETIMEFORMAT);
                    String toStr = getParamValue(tokens, toParamIdx, tokens.length);
                    LocalDateTime toDateTime = LocalDateTime.parse(toStr, INPUT_DATETIMEFORMAT);
                    bot.addTask(new Event(eventTitle, fromDateTime, toDateTime));
                    if (fromDateTime.isAfter(toDateTime)) {
                        throw new IllegalCommandException("From Datetime is later than To Datetime");
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalCommandException("Invalid DateTime format.\n"
                                                        + "Please use dd-MM-yyyy format.");
                }
                break;
            case DELETE:
                if (!iskNumberOfParamCorrect(tokens, 2)) {
                    throw new IllegalCommandException("Please specify a task number to delete\n"
                                                            + "Usage: delete <task_number>");
                }
                try {
                    int deleteTaskIdx = Integer.parseInt(tokens[1]);
                    bot.deleteTask(deleteTaskIdx);
                } catch (NumberFormatException e) {
                    throw new IllegalCommandException(tokens[1] + " is not a valid integer!\n" 
                                                            + "Usage: delete <task_number>");
                }
                break;
            default:
                throw new InvalidCommandException(tokens[0]);
        }
    }
}