# Sid User Guide

Sid is a personal task management chatbot that helps you track your todos, deadlines, and events. It provides a simple command-line interface to manage your daily tasks efficiently.

## Quick Start

Type commands in the format: `command <arguments>`

For help at any time, try typing an invalid command to see available options.

## Available Commands

### Adding Tasks

#### `todo` - Add a simple todo task
Add a basic task without any time constraints.

**Usage:** `todo <description>`

**Example:** `todo read book`

**Expected Output:**
```
Alright! I've got that down for you:
[T][ ] read book
```

#### `deadline` - Add a task with deadline
Add a task that must be completed by a specific date and time.

**Usage:** `deadline <description> /by <date>`

**Date Formats:**
- `yyyy-MM-dd HHmm` (e.g., `2024-12-25 1800`)
- `yyyy-MM-dd` (defaults to 00:00)
- `d/M/yyyy HHmm` (e.g., `25/12/2024 1800`)
- ISO format: `yyyy-MM-ddTHH:mm`

**Example:** `deadline submit assignment /by 2024-12-15 2359`

**Expected Output:**
```
Successfully added
Deadline: [D][ ] submit assignment (by: Dec 15 2024, 11:59 PM)
```

#### `event` - Add a time-blocked event
Add an event that occurs within a specific time period.

**Usage:** `event <description> /from <start-date> /to <end-date>`

**Example:** `event team meeting /from 2024-12-10 1400 /to 2024-12-10 1500`

**Expected Output:**
```
Got it! I've blocked out this time for you:
[E][ ] team meeting (from: Dec 10 2024, 2:00 PM to: Dec 10 2024, 3:00 PM)
```

### Managing Tasks

#### `list` - View all tasks
Display all tasks in your list with their status.

**Usage:** `list`

**Expected Output:**
```
Here's what's keeping you busy:
1. [T][ ] read book
2. [D][X] submit assignment (by: Dec 15 2024, 11:59 PM)
3. [E][ ] team meeting (from: Dec 10 2024, 2:00 PM to: Dec 10 2024, 3:00 PM)
```

#### `mark` - Mark task as completed
Mark a specific task as done using its number from the list.

**Usage:** `mark <task-number>`

**Example:** `mark 2`

**Expected Output:**
```
Sweet! Marking this one as done:
[D][X] submit assignment (by: Dec 15 2024, 11:59 PM)
```

#### `unmark` - Mark task as not completed
Unmark a specific task using its number from the list.

**Usage:** `unmark <task-number>`

**Example:** `unmark 2`

**Expected Output:**
```
Oops, not done yet? I've unmarked:
[D][ ] submit assignment (by: Dec 15 2024, 11:59 PM)
```

#### `delete` - Remove a task
Permanently remove a task from your list using its number.

**Usage:** `delete <task-number>`

**Example:** `delete 1`

**Expected Output:**
```
Deleted your task:
[T][ ] read book
```

### Finding Tasks

#### `find` - Search for tasks
Find tasks that contain specific keywords in their description.

**Usage:** `find <keyword>`

**Example:** `find assignment`

**Expected Output:**
```
Found some matches! Here's what I dug up:
1. [D][ ] submit assignment (by: Dec 15 2024, 11:59 PM)
```

### Exiting

#### `bye` - Exit the application
Quit Sid and end your session.

**Usage:** `bye`

**Expected Output:**
```
Byebye! See you next time!
```

## Task Status Symbols

- `[T]` - Todo task
- `[D]` - Deadline task
- `[E]` - Event task
- `[ ]` - Not completed
- `[X]` - Completed

## Navigation

#### Command History
Use the **up arrow** and **down arrow** keys to navigate through your previously entered commands in the current session. This allows you to quickly repeat or modify recent commands without retyping them.

## Tips

- Task numbers change when you delete tasks, so always check the current list before marking/unmarking/deleting
- Date parsing is flexible - use the format that works best for you
- All commands are case-insensitive
- Empty commands are ignored
- Events cannot end before they start
- Past dates are not allowed for new tasks