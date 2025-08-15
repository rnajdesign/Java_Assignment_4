# Getting Started

## Requirements

- Java 17 or newer
- Basic knowledge of running Java programs from the command line or an IDE like VS Code / IntelliJ

## Installation

1. Clone or download this repository.
2. Make sure Java is installed:

   ```bash
   java -version
   ```

3. Compile the code:

   ```bash
   javac -d out $(find src -name "*.java")
   ```

4. Run the application:

   ```bash
   java -cp out gui.AssignmentTrackerGUI
   ```
