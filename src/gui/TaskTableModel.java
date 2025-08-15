package gui;

import model.Task;
import model.Homework;
import model.Project;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {
    private final String[] columns = {"Type", "Title", "Due Date", "Completed"};
    private final Class<?>[] columnTypes = {String.class, String.class, String.class, Boolean.class};
    private final List<Task> tasks = new ArrayList<>();

    @Override public int getRowCount() { return tasks.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }
    @Override public Class<?> getColumnClass(int col) { return columnTypes[col]; }
    @Override public boolean isCellEditable(int row, int col) { return col == 3; }

    @Override
    public Object getValueAt(int row, int col) {
        Task t = tasks.get(row);
        return switch (col) {
            case 0 -> t.getType();
            case 1 -> t.getTitle();
            case 2 -> t.getDueDate();
            case 3 -> t.isCompleted();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (col == 3 && aValue instanceof Boolean) {
            tasks.get(row).setCompleted((Boolean) aValue);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addTask(Task t) {
        tasks.add(t);
        int idx = tasks.size() - 1;
        fireTableRowsInserted(idx, idx);
    }

    public void setTasks(List<Task> list) {
        tasks.clear();
        if (list != null) tasks.addAll(list);
        fireTableDataChanged();
    }

    public void clear() {
        tasks.clear();
        fireTableDataChanged();
    }

    public List<Task> snapshot() {
        return new ArrayList<>(tasks);
    }

    public Task get(int row) { return tasks.get(row); }
}
