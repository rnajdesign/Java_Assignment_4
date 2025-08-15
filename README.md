# Assignment Tracker

A simple Java Swing application for managing assignments and tasks.

---

## Features

- Add, save, and load tasks
- Differentiate Homework and Project tasks
- Mark tasks as completed
- Share tasks via clipboard
- Desktop shortcut support
- Clean, simple user interface

---

## Screenshots

### Home Screen

![Home](https://github.com/rnajdesign/Java_Assignment_4/blob/main/src/screenshots/Home.png)

### Add Task Dialog

![Add Task](https://github.com/rnajdesign/Java_Assignment_4/blob/main/src/screenshots/AddTask.png)

### Save Tasks

![Save](https://github.com/rnajdesign/Java_Assignment_4/blob/main/src/screenshots/Save.png)

---

## Requirements

- Java 17+

---

## Installation Instructions

1. **Install Java 17 or higher**  

   - [Download Java](https://adoptium.net/) and follow the installation steps for your operating system.  
   - Verify installation:  

     ```bash
     java -version
     ```

2. **Clone the repository**  

   ```bash
   git clone https://github.com/rnajdesign/Java_Assignment_4.git
   cd Java_Assignment_4

3. **Compile the application**

   ```bash
   javac -d out $(find src -name "*.java")

4. **Run the application**

   ```bash
   java -cp out gui.AssignmentTrackerGUI
