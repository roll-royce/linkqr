package com.linkqr.test;

import java.sql.Connection;

import com.linkqr.utils.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Connection is successful!");
        } else {
            System.out.println("❌ Connection failed!");
        }
    }
}
