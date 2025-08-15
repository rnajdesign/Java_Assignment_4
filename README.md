# Assignment Tracker

A simple Java Swing application for managing assignments and tasks.

## Features
- Add, save, and load tasks
- Differentiate Homework and Project tasks
- Mark tasks as completed
- Share tasks via clipboard

## Screenshots

### Home Screen
![Home](screenshots/Home.png)

### Add Task Dialog
![Add Task](screenshots/AddTask.png)

### Save Tasks
![Save](screenshots/Save.png)

## Requirements
- Java 17+

## How to Compile and Run

```bash
javac -d out $(find src -name "*.java")
java -cp out gui.AssignmentTrackerGUI
