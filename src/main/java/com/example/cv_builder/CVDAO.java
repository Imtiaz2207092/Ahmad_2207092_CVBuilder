package com.example.cv_builder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CVDAO {
    private final DatabaseManager dbManager;

    public CVDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Task<Integer> insertCV(CVModel cv) {
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                String sql = """
                    INSERT INTO cvs (name, email, phone, address, education, skills, experience, projects, photo_path)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

                try (Connection conn = dbManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, cv.getName());
                    pstmt.setString(2, cv.getEmail());
                    pstmt.setString(3, cv.getPhone());
                    pstmt.setString(4, cv.getAddress());
                    pstmt.setString(5, cv.getEducation());
                    pstmt.setString(6, cv.getSkills());
                    pstmt.setString(7, cv.getExperience());
                    pstmt.setString(8, cv.getProjects());
                    pstmt.setString(9, cv.getPhotoPath());

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating CV failed, no rows affected.");
                    }

                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        } else {
                            throw new SQLException("Creating CV failed, no ID obtained.");
                        }
                    }
                }
            }
        };
        return task;
    }

    public Task<Boolean> updateCV(CVModel cv) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = """
                    UPDATE cvs 
                    SET name = ?, email = ?, phone = ?, address = ?, education = ?, 
                        skills = ?, experience = ?, projects = ?, photo_path = ?
                    WHERE id = ?
                    """;

                try (Connection conn = dbManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, cv.getName());
                    pstmt.setString(2, cv.getEmail());
                    pstmt.setString(3, cv.getPhone());
                    pstmt.setString(4, cv.getAddress());
                    pstmt.setString(5, cv.getEducation());
                    pstmt.setString(6, cv.getSkills());
                    pstmt.setString(7, cv.getExperience());
                    pstmt.setString(8, cv.getProjects());
                    pstmt.setString(9, cv.getPhotoPath());
                    pstmt.setInt(10, cv.getId());

                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;
                }
            }
        };
        return task;
    }

    public Task<Boolean> deleteCV(int id) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String sql = "DELETE FROM cvs WHERE id = ?";

                try (Connection conn = dbManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;
                }
            }
        };
        return task;
    }

    public Task<CVModel> getCVById(int id) {
        Task<CVModel> task = new Task<CVModel>() {
            @Override
            protected CVModel call() throws Exception {
                String sql = "SELECT * FROM cvs WHERE id = ?";

                try (Connection conn = dbManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        return mapResultSetToCV(rs);
                    }
                    return null;
                }
            }
        };
        return task;
    }

    public Task<ObservableList<CVModel>> getAllCVs() {
        Task<ObservableList<CVModel>> task = new Task<ObservableList<CVModel>>() {
            @Override
            protected ObservableList<CVModel> call() throws Exception {
                String sql = "SELECT * FROM cvs ORDER BY created_at DESC";
                List<CVModel> cvList = new ArrayList<>();

                try (Connection conn = dbManager.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    while (rs.next()) {
                        cvList.add(mapResultSetToCV(rs));
                    }
                }

                return FXCollections.observableArrayList(cvList);
            }
        };
        return task;
    }

    private CVModel mapResultSetToCV(ResultSet rs) throws SQLException {
        return new CVModel(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("education"),
            rs.getString("skills"),
            rs.getString("experience"),
            rs.getString("projects"),
            rs.getString("photo_path")
        );
    }
}

