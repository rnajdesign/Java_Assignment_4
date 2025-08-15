package gui;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class LookAndFeelSetup {
    static void init() {
        try {
            Class<?> flat = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            flat.getMethod("setup").invoke(null);
            UIManager.put("Button.arc", 16);
            UIManager.put("Component.arc", 16);
            UIManager.put("TextComponent.arc", 12);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {}
        }
    }
}

public class AssignmentTrackerGUI extends JFrame {

    private final TaskTableModel tableModel;
    private final JTable table;
    private final boolean freshMode;
    private final String SAVE_FILE = System.getProperty("user.home") + File.separator + "tasks.dat";

    public AssignmentTrackerGUI(boolean freshMode) {
        this.freshMode = freshMode;

        setTitle("Homework & Assignment Tracker");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);

        // Toolbar setup
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(new EmptyBorder(10, 10, 10, 10));
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        JButton addBtn   = new JButton("➕ Add Task");
        JButton saveBtn  = new JButton("💾 Save");
        JButton shareBtn = new JButton("🔗 Share");
        JButton freshBtn = new JButton("🧹 Fresh Start");

        for (JButton b : new JButton[]{addBtn, saveBtn, shareBtn, freshBtn}) {
            b.putClientProperty("JButton.buttonType", "roundRect");
            b.setFocusable(false);
        }

        toolbar.add(addBtn);
        toolbar.add(saveBtn);
        toolbar.add(shareBtn);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(freshBtn);

        // Table setup
        tableModel = new TaskTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        TableColumn typeCol = table.getColumnModel().getColumn(0);
        typeCol.setPreferredWidth(110);
        TableColumn titleCol = table.getColumnModel().getColumn(1);
        titleCol.setPreferredWidth(420);
        TableColumn dateCol = table.getColumnModel().getColumn(2);
        dateCol.setPreferredWidth(140);
        TableColumn compCol = table.getColumnModel().getColumn(3);
        compCol.setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(6, 6, 6, 6));
        content.add(toolbar, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        setContentPane(content);

        // Button actions
        addBtn.addActionListener(e -> showAddTaskDialog());
        saveBtn.addActionListener(e -> saveTasks(true));
        shareBtn.addActionListener(e -> shareTasks());
        freshBtn.addActionListener(e -> {
            if (confirmLoseChanges()) tableModel.clear();
        });

        if (!freshMode) {
            loadTasks();
        }

        // Window close handler
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        AssignmentTrackerGUI.this,
                        "Do you want to save your tasks before exiting?",
                        "Save Tasks",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.CANCEL_OPTION) return;
                if (choice == JOptionPane.YES_OPTION) saveTasks(false);
                dispose();
                System.exit(0);
            }
        });
    }

    public TaskTableModel getTableModel() { return tableModel; }

    private void showAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Add Task", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Homework", "Project"});

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(22);

        JLabel dueDateLabel = new JLabel("Due Date:");
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        JLabel field1Label = new JLabel("Subject:");
        JTextField field1 = new JTextField(22);

        JLabel field2Label = new JLabel("Teacher (Homework only):");
        JTextField field2 = new JTextField(22);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; dialog.add(typeLabel, gbc);
        gbc.gridx = 1; dialog.add(typeCombo, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; dialog.add(titleLabel, gbc);
        gbc.gridx = 1; dialog.add(titleField, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; dialog.add(dueDateLabel, gbc);
        gbc.gridx = 1; dialog.add(dateSpinner, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; dialog.add(field1Label, gbc);
        gbc.gridx = 1; dialog.add(field1, gbc);

        y++; gbc.gridx = 0; gbc.gridy = y; dialog.add(field2Label, gbc);
        gbc.gridx = 1; dialog.add(field2, gbc);

        typeCombo.addActionListener(ae -> {
            boolean homework = "Homework".equals(typeCombo.getSelectedItem());
            field1Label.setText(homework ? "Subject:" : "Description:");
            field2Label.setEnabled(homework);
            field2.setEnabled(homework);
            if (!homework) field2.setText("");
        });
        typeCombo.setSelectedIndex(0);

        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("OK");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(cancel); btns.add(ok);

        y++; gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; dialog.add(btns, gbc);

        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String title = titleField.getText().trim();
            Date d = (Date) dateSpinner.getValue();
            LocalDate due = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title is required.", "Missing data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if ("Homework".equals(type)) {
                String subject = field1.getText().trim().isEmpty() ? "General" : field1.getText().trim();
                String teacher = field2.getText().trim();
                tableModel.addTask(new Homework(title, due.toString(), new Subject(subject, teacher)));
            } else {
                String desc = field1.getText().trim();
                tableModel.addTask(new Project(title, due.toString(), desc));
            }
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean confirmLoseChanges() {
        int c = JOptionPane.showConfirmDialog(
                this,
                "Clear the current list? (This does not delete your saved file.)",
                "Fresh Start",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return c == JOptionPane.OK_OPTION;
    }

    private void shareTasks() {
        StringBuilder sb = new StringBuilder();
        for (Task t : tableModel.snapshot()) {
            sb.append(t.getType()).append(": ").append(t.getTitle())
              .append(" (Due: ").append(t.getDueDate()).append(")")
              .append(t.isCompleted() ? " ✅" : "")
              .append("\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
               .setContents(new StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "Tasks copied to clipboard!", "Share", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveTasks(boolean showOk) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(new ArrayList<>(tableModel.snapshot()));
            if (showOk) {
                JOptionPane.showMessageDialog(this, "Saved to " + SAVE_FILE, "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Task> tasks = (List<Task>) ois.readObject();
            tableModel.setTasks(tasks);
        } catch (IOException | ClassNotFoundException e) {
            int c = JOptionPane.showConfirmDialog(
                    this,
                    "Saved data is incompatible or corrupted.\nDelete the saved file and start fresh?",
                    "Load Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (c == JOptionPane.YES_OPTION) {
                new File(SAVE_FILE).delete();
                tableModel.clear();
            }
        }
    }

    public static void main(String[] args) {
        LookAndFeelSetup.init();
        boolean fresh = args.length > 0 && "--fresh".equalsIgnoreCase(args[0]);
        SwingUtilities.invokeLater(() -> new AssignmentTrackerGUI(fresh).setVisible(true));
    }
}
