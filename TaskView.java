package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TaskView extends JFrame {
    private JTable taskList;
    private JButton loadTasksButton;
    private JButton addTaskButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private Controller controller = new Controller();
    private DefaultTableModel model;

    public TaskView() {
        setTitle("Task Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskList = new JTable();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Title", "Description", "Done"});
        taskList.setModel(model);

        loadTasksButton = new JButton("Load Tasks");
        addTaskButton = new JButton("Add a Task");
        statusLabel = new JLabel("Gotowe");

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        JPanel topPanel = new JPanel();
        topPanel.add(loadTasksButton);
        topPanel.add(addTaskButton);
        topPanel.add(statusLabel);
        topPanel.add(progressBar);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        loadTasksButton.addActionListener(e -> {
            statusLabel.setText("Ładowanie danych...");
            loadTasksButton.setEnabled(false);
            progressBar.setEnabled(true);

            new SwingWorker<List<Task>, Void>() {

                @Override
                protected List<Task> doInBackground() throws Exception {
                    Thread.sleep(4000);
                    return controller.loadTasks();
                }

                @Override
                protected void done() {
                    try {
                        List<Task> tasks = get();
                        model.setRowCount(0);
                        for (Task t : tasks) {
                            model.addRow(new Object[] {
                                    t.getId(),
                                    t.getTitle(),
                                    t.getDescription(),
                                    t.isDone()
                            });
                        }
                        statusLabel.setText("Gotowe. Wczytano " + tasks.size());
                    }catch (Exception ex) {
                        statusLabel.setText(ex.getMessage());
                    }
                }
            }.execute();
        });

        addTaskButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Title: ");
            String desc = JOptionPane.showInputDialog("Description: ");

            if (title == null || title.isEmpty()) return;

            statusLabel.setText("Dodawanie...");
            progressBar.setVisible(true);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        controller.addTask(title, desc, false);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            statusLabel.setText("Dodano!");
                            loadTasksButton.doClick();
                        }else{
                            statusLabel.setText("Błąd dodawania");
                        }
                    }catch (Exception e) {
                        statusLabel.setText("Błąd");
                    }
                    progressBar.setVisible(false);
                }
            }.execute();
        });
    }
}
