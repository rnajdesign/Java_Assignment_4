package model;

import java.io.Serializable;

public class Subject implements Serializable {
    private String name;
    private String teacher;

    public Subject(String name, String teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    public String getName() { return name; }
    public String getTeacher() { return teacher; }
}
