package fun.noah.server.onlyup.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                        coins INT DEFAULT 100,
                        last_x DOUBLE,
                        last_y DOUBLE,
                        last_z DOUBLE
                    );
                """);

                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS perks (
                        uuid VARCHAR(36),
                        perk_name VARCHAR(24),
                        duration INT,
                        PRIMARY KEY(uuid, perk_name),
                        FOREIGN KEY (uuid) REFERENCES players(uuid) ON DELETE CASCADE
                        );
                        """);

                System.out.println("[DB] H2 Connected and Tables are ready.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("H2 DB Init Failed", e);
        }
    }

    public static void savePlayer(String uuid, String name, int coins, double x, double y, double z) {
        try(PreparedStatement ps = conn.prepareStatement("""
                MERGE INTO players(uuid, name, coins, last_x, last_y, last_z) VALUES(?, ?, ?, ?, ?, ?)""")) {
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setInt(3, coins);
            ps.setDouble(4, x);
            ps.setDouble(5, y);
            ps.setDouble(6, z);
            ps.executeUpdate();
            System.out.println("Saved a Player named " + name);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public static ResultSet loadPlayer2(String uuid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            ps.setString(1, uuid);
            System.out.println("Loaded a Player with UUID: " + uuid);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void savePerk(String uuid, String perkName, int duration) {
        try(PreparedStatement ps = conn.prepareStatement("""
                MERGE INTO perks (uuid, perk_name, duration) VALUES (?, ?, ?)""")) {
            ps.setString(1, uuid);
            ps.setString(2, perkName);
            ps.setInt(3, duration);
            ps.executeUpdate();
            System.out.println("Saved a Perk with name " + perkName + " to UUID " + uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadPerks(String uuid) {
        List<String> perks = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement("SELECT perk_name FROM perks WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                perks.add(rs.getString("perk_name"));
                System.out.println("Added a Perk with the Name " + rs.getString("perk_name") + " ...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perks;
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
