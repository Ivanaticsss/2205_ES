package enrollmentsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageStudentEnrollment extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JButton btnEnroll, btnUnenroll, btnRefresh, btnSearch, btnReturn;
    private JTextField txtSearch;

    public ManageStudentEnrollment() {
        setTitle("Manage Student Enrollment");
        setSize(1320, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table Model with a smaller size
        String[] columnNames = {"ID", "SR Code", "Name", "Course", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25); // Smaller row height for better fit

        // Adjust column widths
        TableColumnModel columnModel = studentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(100); // SR Code
        columnModel.getColumn(2).setPreferredWidth(200); // Name
        columnModel.getColumn(3).setPreferredWidth(150); // Course
        columnModel.getColumn(4).setPreferredWidth(100); // Status

        scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(800, 400)); // Smaller table size
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnEnroll = new JButton("Enroll");
        btnUnenroll = new JButton("Unenroll");
        btnRefresh = new JButton("Refresh");
        btnReturn = new JButton("Return");

        panel.add(txtSearch);
        panel.add(btnSearch);
        panel.add(btnEnroll);
        panel.add(btnUnenroll);
        panel.add(btnRefresh);
        panel.add(btnReturn);
        add(panel, BorderLayout.SOUTH);

        // Load student data initially
        loadStudentData("");

        // Button Actions
        btnEnroll.addActionListener(e -> updateEnrollmentStatus("Regular"));
        btnUnenroll.addActionListener(e -> updateEnrollmentStatus("Irregular"));
        btnRefresh.addActionListener(e -> loadStudentData(""));
        btnSearch.addActionListener(e -> loadStudentData(txtSearch.getText()));
        btnReturn.addActionListener(e -> {
            new AdminStaff().setVisible(true);
            dispose();
        });
    }

    private void loadStudentData(String keyword) {
        tableModel.setRowCount(0); // Clear table before loading new data
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/es_db", "root", "")) {
            String query = "SELECT student_id, sr_code, name, course, status FROM students WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("student_id");
                String srCode = rs.getString("sr_code");
                String name = rs.getString("name");
                String course = rs.getString("course");
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{id, srCode, name, course, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading student data: " + ex.getMessage());
        }
    }

    private void updateEnrollmentStatus(String status) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/es_db", "root", "")) {
            String query = "UPDATE students SET status = ? WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student status updated to: " + status);
            loadStudentData(""); // Refresh table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating student status: " + ex.getMessage());
        }
    }
}