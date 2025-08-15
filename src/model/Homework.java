package model;

public class Homework extends Task {
    private Subject subject;

    public Homework(String title, String dueDate, Subject subject) {
        super(title, dueDate);
        this.subject = subject;
    }

    @Override
    public String getType() { return "Homework"; }

    public Subject getSubject() { return subject; }
}
