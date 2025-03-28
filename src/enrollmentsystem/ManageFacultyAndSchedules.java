package enrollmentsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ManageFacultyAndSchedules extends JFrame {
    private JTable facultyTable, scheduleTable;
    private DefaultTableModel facultyModel, scheduleModel;
    private JTextField txtFacultyName, txtRoom, txtTime;
    private JComboBox<String> cmbDepartment, cmbCourse, cmbSubject, cmbFaculty, cmbDay;
    private JButton btnAddFaculty, btnDeleteFaculty, btnAddSchedule, btnDeleteSchedule;

    public ManageFacultyAndSchedules() {
        setTitle("Manage Faculty & Schedules");
        setSize(1320, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 1));

        // Faculty Management
        JPanel facultyPanel = new JPanel(new BorderLayout());
        facultyPanel.setBorder(BorderFactory.createTitledBorder("Faculty Management"));
        
        facultyModel = new DefaultTableModel(new String[]{"ID", "Name", "Department"}, 0);
        facultyTable = new JTable(facultyModel);
        facultyPanel.add(new JScrollPane(facultyTable), BorderLayout.CENTER);
        
        JPanel facultyInput = new JPanel();
        txtFacultyName = new JTextField(15);
        cmbDepartment = new JComboBox<>();
        btnAddFaculty = new JButton("Add Faculty");
        btnDeleteFaculty = new JButton("Delete Faculty");
        
        facultyInput.add(new JLabel("Name:"));
        facultyInput.add(txtFacultyName);
        facultyInput.add(new JLabel("Department:"));
        facultyInput.add(cmbDepartment);
        facultyInput.add(btnAddFaculty);
        facultyInput.add(btnDeleteFaculty);
        
        facultyPanel.add(facultyInput, BorderLayout.SOUTH);
        panel.add(facultyPanel);

        // Schedule Management
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Class Schedule Management"));
        
        scheduleModel = new DefaultTableModel(new String[]{"ID", "Course", "Subject", "Faculty", "Room", "Time", "Day"}, 0);
        scheduleTable = new JTable(scheduleModel);
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        
        JPanel scheduleInput = new JPanel();
        cmbCourse = new JComboBox<>();
        cmbSubject = new JComboBox<>();
        cmbFaculty = new JComboBox<>();
        txtRoom = new JTextField(10);
        txtTime = new JTextField(10);
        cmbDay = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        btnAddSchedule = new JButton("Add Schedule");
        btnDeleteSchedule = new JButton("Delete Schedule");

        scheduleInput.add(new JLabel("Course:"));
        scheduleInput.add(cmbCourse);
        scheduleInput.add(new JLabel("Subject:"));
        scheduleInput.add(cmbSubject);
        scheduleInput.add(new JLabel("Faculty:"));
        scheduleInput.add(cmbFaculty);
        scheduleInput.add(new JLabel("Room:"));
        scheduleInput.add(txtRoom);
        scheduleInput.add(new JLabel("Time:"));
        scheduleInput.add(txtTime);
        scheduleInput.add(new JLabel("Day:"));
        scheduleInput.add(cmbDay);
        scheduleInput.add(btnAddSchedule);
        scheduleInput.add(btnDeleteSchedule);
        
        schedulePanel.add(scheduleInput, BorderLayout.SOUTH);
        panel.add(schedulePanel);

        add(panel, BorderLayout.CENTER);
        
        // Load Data
        loadDepartments();
        loadCourses();
        loadFaculty();
        loadSubjects();
        loadSchedules();

        // Action Listeners
        btnAddFaculty.addActionListener(this::addFaculty);
        btnDeleteFaculty.addActionListener(this::deleteFaculty);
        btnAddSchedule.addActionListener(this::addSchedule);
        btnDeleteSchedule.addActionListener(this::deleteSchedule);
    }

    // Load Departments into ComboBox
    private void loadDepartments() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM departments")) {
            while (rs.next()) {
                cmbDepartment.addItem(rs.getString("department_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load Courses into ComboBox
    private void loadCourses() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
            while (rs.next()) {
                cmbCourse.addItem(rs.getString("course_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load Faculty
    private void loadFaculty() {
        facultyModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM faculty")) {
            while (rs.next()) {
                facultyModel.addRow(new Object[]{
                    rs.getInt("faculty_id"), rs.getString("faculty_name"), rs.getInt("department_id")
                });
                cmbFaculty.addItem(rs.getString("faculty_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load Subjects into ComboBox
    private void loadSubjects() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM subjects")) {
            while (rs.next()) {
                cmbSubject.addItem(rs.getString("subject_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load Schedules
    private void loadSchedules() {
        scheduleModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM class_schedules")) {
            while (rs.next()) {
                scheduleModel.addRow(new Object[]{
                    rs.getInt("schedule_id"), rs.getInt("course_id"), rs.getInt("subject_id"),
                    rs.getInt("faculty_id"), rs.getString("room"), rs.getString("time"), rs.getString("day")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
// Delete Faculty
private void deleteFaculty(ActionEvent evt) {
    int selectedRow = facultyTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a faculty to delete.");
        return;
    }

    int facultyId = (int) facultyModel.getValueAt(selectedRow, 0);
    String sql = "DELETE FROM faculty WHERE faculty_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, facultyId);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Faculty Deleted!");
        loadFaculty();  // Reload faculty data
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Delete Schedule
private void deleteSchedule(ActionEvent evt) {
    int selectedRow = scheduleTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a schedule to delete.");
        return;
    }

    int scheduleId = (int) scheduleModel.getValueAt(selectedRow, 0);
    String sql = "DELETE FROM class_schedules WHERE schedule_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, scheduleId);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Schedule Deleted!");
        loadSchedules();  // Reload schedule data
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    // Add Schedule
private void addSchedule(ActionEvent evt) {
    String course = (String) cmbCourse.getSelectedItem();
    String subject = (String) cmbSubject.getSelectedItem();
    String faculty = (String) cmbFaculty.getSelectedItem();
    String room = txtRoom.getText();
    String time = txtTime.getText();
    String day = (String) cmbDay.getSelectedItem();

    if (course == null || subject == null || faculty == null || room.isEmpty() || time.isEmpty() || day == null) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }

    String sql = "INSERT INTO class_schedules (course_id, subject_id, faculty_id, room, time, day) VALUES " +
            "((SELECT course_id FROM courses WHERE course_name = ?), " +
            "(SELECT subject_id FROM subjects WHERE subject_name = ?), " +
            "(SELECT faculty_id FROM faculty WHERE faculty_name = ?), ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, course);
        stmt.setString(2, subject);
        stmt.setString(3, faculty);
        stmt.setString(4, room);
        stmt.setString(5, time);
        stmt.setString(6, day);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Schedule Added!");
        loadSchedules();  // Reload schedule data
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    // Add Faculty
    private void addFaculty(ActionEvent evt) {
        String facultyName = txtFacultyName.getText();
        String department = (String) cmbDepartment.getSelectedItem();
        if (facultyName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter faculty name.");
            return;
        }

        String sql = "INSERT INTO faculty (faculty_name, department_id) VALUES (?, (SELECT department_id FROM departments WHERE department_name = ?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, facultyName);
            stmt.setString(2, department);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Faculty Added!");
            loadFaculty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add Schedule, Delete Faculty, Delete Schedule Methods... (Implement similar to `addFaculty`)

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageFacultyAndSchedules().setVisible(true));
    }
}
