package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final String url = "jdbc:mysql://localhost:3306/tasks";
    private final String user = "root";
    private final String password = "";

    public List<Task> loadTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                tasks.add( new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("is_done")
                ));
            }

            return tasks;
        }
    }

    public void addTask(String title, String desc, boolean isDone) throws SQLException {
        String sql = "INSERT INTO tasks(title, description, is_done) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setBoolean(3, isDone);

            ps.execute();
        }
    }
}
