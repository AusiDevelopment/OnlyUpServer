package fun.noah.server.onlyup.database;

import java.sql.*;

public class Database {

    private static Connection conn;

    public static void init() {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./onlyup;MODE=MySQL", "sa", "");
            try(Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS players (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(16),
                        max_y DOUBLE DEFAULT 0,
                        total_time BIGINT DEFAULT 0
                    )
                """);
                System.out.println("[DB] H2 Connected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("H2 DB Init Failed", e);
        }
    }

    public static void savePlayer(String uuid, String name, double maxY, long totalTime) {
        try (PreparedStatement stmt = conn.prepareStatement("""
            MERGE INTO players (uuid, name, max_y, total_time)
            VALUES (?, ?, ?, ?)
        """)) {
            stmt.setString(1, uuid);
            stmt.setString(2, name);
            stmt.setDouble(3, maxY);
            stmt.setLong(4, totalTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet loadPlayer(String uuid) {
        try {
            PreparedStatement stmt = conn.prepareStatement("""
                    SELECT * FROM players WHERE uuid = ?
                    """);
            stmt.setString(1, uuid);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
