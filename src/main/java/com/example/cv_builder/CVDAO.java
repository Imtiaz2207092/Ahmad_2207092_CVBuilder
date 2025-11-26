package com.example.cv_builder;

import java.sql.*;

public class CVDAO {
    private final DatabaseManager dbManager;

    public CVDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean insertCV(CVModel cv) {
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
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting CV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

