package sid.messages;

/**
 * Centralized enum for all chatbot response messages.
 * This provides consistent messaging across commands and makes testing easier.
 */
public enum ResponseMessage {
    // Todo command messages
    TODO_SUCCESS("Alright! I've got that down for you:\n"),
    TODO_USAGE_ERROR("Usage: todo <description>"),

    // Mark command messages
    MARK_SUCCESS("Sweet! Marking this one as done:\n"),
    MARK_USAGE_ERROR("Usage: mark <task-number>"),
    MARK_INVALID_NUMBER("Please provide a valid number after 'mark'."),

    // Unmark command messages
    UNMARK_SUCCESS("Oops, not done yet? I've unmarked:\n"),
    UNMARK_USAGE_ERROR("Usage: unmark <task-number>"),
    UNMARK_INVALID_NUMBER("Please provide a valid number after 'unmark'."),

    // Delete command messages
    DELETE_SUCCESS("Deleted your task:\n"),
    DELETE_USAGE_ERROR("What do you want me to delete?\nUsage: delete <task-number>"),
    DELETE_INVALID_NUMBER("Please provide a valid number after 'delete'."),

    // List command messages
    LIST_EMPTY("Nothing on your agenda right now! Ready to get busy?"),
    LIST_WITH_TASKS("Here's what's keeping you busy:\n"),

    // Event command messages
    EVENT_SUCCESS("Got it! I've blocked out this time for you:\n"),
    EVENT_USAGE_ERROR("Usage: event <description> /from <yyyy-MM-dd[ HHmm]> /to <yyyy-MM-dd HHmm>"),
    EVENT_INVALID_TIME_ORDER("Event end must be on/after start."),

    // Deadline command messages
    DEADLINE_SUCCESS("Successfully added\nDeadline: "),
    DEADLINE_USAGE_ERROR("You typed it wrong!\nProper usage: deadline <description> /by <yyyy-MM-dd HHmm>"),

    // Find command messages
    FIND_SUCCESS("Found some matches! Here's what I dug up:\n"),
    FIND_NO_RESULTS("Hmm, I couldn't find any tasks matching that. Try a different keyword?"),
    FIND_USAGE_ERROR("Usage: find <keyword>"),

    // Bye command messages
    BYE_MESSAGE("Byebye! See you next time!"),

    // General error messages
    INVALID_TASK_NUMBER("Not a valid task number!"),
    NO_INPUT_PROVIDED("No input provided."),

    // Parser error messages
    UNKNOWN_COMMAND("Sorry! Can't understand you.\nTry: todo | deadline | event | list | mark <n> | "
        + "unmark <n> | delete <n> | bye"),
    HELP_MESSAGE("Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns the message with the provided object's toString() appended.
     * Useful for messages that include task details.
     */
    public String getMessageWith(Object obj) {
        return message + obj.toString();
    }
}

