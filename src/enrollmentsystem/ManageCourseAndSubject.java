package enrollmentsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageCourseAndSubject extends JFrame {
    private JTextField txtCourseName, txtCourseCode;
    private JButton btnAddCourse, btnDeleteCourse, btnViewCourses, btnReturn;
    private JTable courseTable;
    private DefaultTableModel courseTableModel;

    public ManageCourseAndSubject() {
        setTitle("Manage Courses and Subjects");
        setSize(1320, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Manage Courses and Subjects", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        txtCourseName = new JTextField(15);
        txtCourseCode = new JTextField(10);
        btnAddCourse = new JButton("Add Course");
        btnDeleteCourse = new JButton("Delete Course");
        btnViewCourses = new JButton("View Courses");
        btnReturn = new JButton("Return to Dashboard");

        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(txtCourseName);
        inputPanel.add(new JLabel("Course Code:"));
        inputPanel.add(txtCourseCode);
        inputPanel.add(btnAddCourse);
        inputPanel.add(btnDeleteCourse);
        inputPanel.add(btnViewCourses);
        inputPanel.add(btnReturn);
        add(inputPanel, BorderLayout.SOUTH);

        // Table Panel
        courseTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Code"}, 0);
        courseTable = new JTable(courseTableModel);
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Button Actions
        btnAddCourse.addActionListener(e -> addCourse(txtCourseName.getText(), txtCourseCode.getText()));
        btnDeleteCourse.addActionListener(e -> deleteSelectedCourse());
        btnViewCourses.addActionListener(e -> viewCourses());
        btnReturn.addActionListener(e -> returnToDashboard());
    }

    private void addCourse(String courseName, String courseCode) {
        if (courseName.isEmpty() || courseCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        String sql = "INSERT INTO courses (course_name, course_code) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseName);
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Course Added Successfully!");
            viewCourses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewCourses() {
        courseTableModel.setRowCount(0);
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courseTableModel.addRow(new Object[]{rs.getInt("course_id"), rs.getString("course_name"), rs.getString("course_code")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.");
            return;
        }
        int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Course Deleted Successfully!");
            viewCourses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void returnToDashboard() {
        this.dispose(); // Close the current window
        new AdminStaff().setVisible(true); // Assuming AdminDashboard exists
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageCourseAndSubject().setVisible(true));
    }
}
