package com.gregorune.minecartannoucer;

import com.gregorune.minecartannoucer.Bookparser.views.AnnouncmentVM;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class DatabaseHandler {
    private Connection dbConn;

    public void Connect() {
        try {
            var dataDirectory = MinecartAnnouncer.plugin.getDataFolder();
            File dbFile = new File(dataDirectory, "minecart.db");
            if (!dbFile.exists()) dataDirectory.mkdirs();

            dbConn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            Bukkit.getLogger().info("[MinecartAnnouncer] Connected to SQLite!");

            InitalizeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Disconnect() {
        if (dbConn != null) {
            try {
                dbConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void InitalizeDatabase()
    {
        try (Statement stmt = dbConn.createStatement()) {
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS messages (
                x INTEGER,
                y INTEGER,
                z INTEGER,
                world TEXT,
                message TEXT,
                PRIMARY KEY (x, y, z, world)
            );
        """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void InsertMessage(int x, int y, int z, String world, String message) {
        try (PreparedStatement ps = dbConn.prepareStatement("""
        INSERT OR REPLACE INTO messages (x, y, z, world, message)
        VALUES (?, ?, ?, ?, ?);
    """)) {
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, world);
            ps.setString(5, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashSet<Block> GetAllAssignedBlocks() {
        HashSet<Block> list = new HashSet<>();
        try (PreparedStatement ps = dbConn.prepareStatement("""
        SELECT DISTINCT x, y, z, world FROM messages;
    """)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String world = rs.getString("world");

                World w = Bukkit.getWorld(world);
                if (w != null) list.add(new Location(w, x, y, z).getBlock());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Nullable
    public AnnouncmentVM GetMessageAt(Block block)
    {
        ArrayList<String> messages = new ArrayList<>();
        try (PreparedStatement ps = dbConn.prepareStatement("""
        SELECT message FROM messages
        WHERE x = ? AND y = ? AND z = ? AND world = ?;
    """)) {
            ps.setInt(1, block.getX());
            ps.setInt(2, block.getY());
            ps.setInt(3, block.getZ());
            ps.setString(4, block.getWorld().getName());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(messages.isEmpty()) return null;
        return new AnnouncmentVM(messages.get(0));
    }

    public void DeleteMessageFromBlock(Block block) {
        try (PreparedStatement stmt = dbConn.prepareStatement(
                     "DELETE FROM messages WHERE world = ? AND x = ? AND y = ? AND z = ?;")) {

            stmt.setString(1, block.getWorld().getName());
            stmt.setInt(2, block.getX());
            stmt.setInt(3, block.getY());
            stmt.setInt(4, block.getZ());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
