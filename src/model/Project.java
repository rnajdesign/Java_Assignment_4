package model;

public class Project extends Task {
    private String description;

    public Project(String title, String dueDate, String description) {
        super(title, dueDate);
        this.description = description;
    }

    @Override
    public String getType() { return "Project"; }

    public String getDescription() { return description; }
}
