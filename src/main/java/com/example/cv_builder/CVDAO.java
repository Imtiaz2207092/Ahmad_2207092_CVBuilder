package com.example.cv_builder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<CVModel> getAllCVs() {
        String sql = "SELECT * FROM cvs ORDER BY created_at DESC";
        List<CVModel> cvList = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cvList.add(mapResultSetToCV(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching CVs: " + e.getMessage());
            e.printStackTrace();
        }

        return cvList;
    }

    public CVModel getCVById(int id) {
        String sql = "SELECT * FROM cvs WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCV(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching CV by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
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

