package com.github.Emcc13.MendingTools.Database;

import com.github.Emcc13.MendingTools.BookGUI.MendingBlueprint;
import com.github.Emcc13.MendingTools.BookGUI.MendingTool;
import com.github.Emcc13.MendingToolsMain;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBHandler {
    private Connection connection = null;
    private final String dbFile;
    private PreparedStatement playerTools = null;
    private PreparedStatement playerTools_limited = null;
    private PreparedStatement toolsEnchantments = null;
    private PreparedStatement idTool = null;
    private PreparedStatement allToolsSortedLimited = null;
    private PreparedStatement allTools = null;
    private PreparedStatement allToolsWithoutBPID = null;

    public DBHandler(MendingToolsMain main) {
        this.dbFile = "jdbc:sqlite:" + (new File(main.getDataFolder().toString(), "MendingTools.db"));
        connect();
    }

    private Connection connect() {
        try {
            if (this.connection != null && !this.connection.isClosed())
                return connection;
            this.connection = DriverManager.getConnection(this.dbFile);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.execute("PRAGMA foreign_key = ON;");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Tool " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "BlueprintID INTEGER, "+
                    "Material STRING, " +
                    "UUID STRING, " +
                    "Broken INTEGER, " +
                    "Restores INTEGER);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Enchantments " +
                    "(ID INTEGER, Name STRING, Level INTEGER, " +
                    "FOREIGN KEY(ID) REFERENCES Tool(ID), " +
                    "PRIMARY KEY(ID, Name));");
            statement.close();
            playerTools_limited = connection.prepareStatement(
                    "SELECT ID, BlueprintID, Material, Broken, Restores FROM Tool WHERE UUID=? " +
                            "ORDER BY BlueprintID ASC, ID ASC LIMIT ?, ?;");
            playerTools = connection.prepareStatement(
                    "SELECT ID, BlueprintID, Material, Broken, Restores FROM Tool WHERE UUID=?;");
            idTool = connection.prepareStatement(
                    "SELECT UUID, BlueprintID, Material, Broken, Restores FROM Tool WHERE ID=?;");
            toolsEnchantments = connection.prepareStatement(
                    "SELECT Name, Level FROM Enchantments WHERE ID=?;");
            allToolsSortedLimited = connection.prepareStatement(
                    "SELECT ID, BlueprintID, Material, Broken, Restores, UUID FROM Tool " +
                            "ORDER BY BlueprintID ASC, ID ASC LIMIT ?, ?;");
            allTools = connection.prepareStatement(
                    "SELECT ID, BlueprintID, Material, Broken, Restores, UUID FROM Tool " +
                            "ORDER BY BlueprintID ASC, ID ASC;");
            allToolsWithoutBPID = connection.prepareStatement(
                    "SELECT ID, Material FROM Tool WHERE BlueprintID=-1");
            return connection;
        } catch (SQLException e) {
            this.connection = null;
            e.printStackTrace();
            return null;
        }
    }

    public void close(){
        try {
            if (this.connection != null)
                this.connection.close();
        } catch (SQLException e) {
            return;
        }
    }

    private PreparedStatement getInsertTool() {
        if (connect() != null) {
            try {
                return connection.prepareStatement(
                        "INSERT INTO Tool(BluePrintID, Material, UUID, Broken, Restores) VALUES(?, ?, ?, 0, 0);",
                        Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getDeleteTool(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "DELETE FROM Tool where ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getInsertEnchantment() {
        if (connect() != null) {
            try {
                return connection.prepareStatement(
                        "INSERT INTO Enchantments(ID, Name, Level) VALUES (?, ?, ?);");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getDeleteToolEnchantment(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "DELETE FROM Enchantments where ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getTransferTool(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "UPDATE Tool SET UUID=? WHERE ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getBreakTool() {
        if (connect() != null) {
            try {
                return connection.prepareStatement(
                        "UPDATE Tool SET Broken=? WHERE ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getRestoreTool(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "UPDATE Tool SET Restores=Restores+1, Broken=? WHERE ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getPlayerTools() {
        if (connect() != null) {
            return playerTools;
        }
        return null;
    }

    private PreparedStatement getPlayerTools_limited() {
        if (connect() != null) {
            return playerTools_limited;
        }
        return null;
    }

    private PreparedStatement getIdTool() {
        if (connect() != null) {
            return idTool;
        }
        return null;
    }

    private PreparedStatement getToolsEnchantments() {
        if (connect() != null) {
            return toolsEnchantments;
        }
        return null;
    }

    private PreparedStatement getUpgradeEnchantment(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "INSERT OR REPLACE INTO Enchantments (Level, ID, Name) VALUES (?, ?, ?);");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private PreparedStatement getAllToolsSortedLimited(){
        if (connect() != null){
            return allToolsSortedLimited;
        }
        return null;
    }

    private PreparedStatement getAllToolsSorted_PS(){
        if (connect() != null){
            return allTools;
        }
        return null;
    }

    private PreparedStatement getAllToolsWithoutBPID_(){
        if (connect() != null){
            return allToolsWithoutBPID;
        }
        return null;
    }

    private PreparedStatement getUpdateBlueprintID(){
        if (connect() != null){
            try {
                return connection.prepareStatement(
                        "UPDATE Tool SET BlueprintID=?, Material=? WHERE ID=?;");
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    public long add_tool(MendingBlueprint blueprint, String uuid) {
        long tool_id;
        PreparedStatement tool = getInsertTool();
        PreparedStatement enchantment = getInsertEnchantment();
        if (tool == null || enchantment == null)
            return -3;
        try {
            tool.clearParameters();
            tool.setInt(1, blueprint.getID());
            tool.setString(2, blueprint.getMaterial());
            tool.setString(3, uuid);
            if (tool.executeUpdate() != 0) {
                ResultSet rs = tool.getGeneratedKeys();
                rs.next();
                tool_id = rs.getLong(1);
            } else {
                return -2;
            }
            for (MendingBlueprint.MTEnchantment mtEnchantment : blueprint.getEnchantments()){
                enchantment.clearParameters();
                enchantment.setLong(1, tool_id);
                enchantment.setString(2, mtEnchantment.getEnchantment());
                enchantment.setInt(3, mtEnchantment.getLevel());
                enchantment.executeUpdate();
            }
            return tool_id;
        } catch (SQLException sqle) {
            return -1;
        }
    }

    public long add_tool(ItemStack itemstack, String uuid, Integer blueprintID){
        long tool_id;
        PreparedStatement tool = getInsertTool();
        PreparedStatement enchantment = getInsertEnchantment();
        if (tool == null || enchantment == null)
            return -3;
        try {
            tool.clearParameters();
            tool.setInt(1, blueprintID);
            tool.setString(2, itemstack.getType().toString());
            tool.setString(3, uuid);
            if (tool.executeUpdate() != 0) {
                ResultSet rs = tool.getGeneratedKeys();
                rs.next();
                tool_id = rs.getLong(1);
            } else {
                return -2;
            }
            for (Map.Entry<Enchantment, Integer> entry : itemstack.getEnchantments().entrySet()) {
                enchantment.clearParameters();
                enchantment.setLong(1, tool_id);
                enchantment.setString(2, entry.getKey().getKey().getKey());
                enchantment.setInt(3, entry.getValue());
                enchantment.executeUpdate();
            }
            return tool_id;
        } catch (SQLException sqle) {
            return -1;
        }
    }
    public long add_tool(ItemStack itemstack, String uuid, MendingBlueprint mb){
        long tool_id;
        PreparedStatement tool = getInsertTool();
        PreparedStatement enchantment = getInsertEnchantment();
        if (tool == null || enchantment == null)
            return -3;
        try {
            tool.clearParameters();
            tool.setInt(1, mb.getID());
//            tool.setString(2, itemstack.getType().toString());
            tool.setString(2, mb.getMaterial());
            tool.setString(3, uuid);
            if (tool.executeUpdate() != 0) {
                ResultSet rs = tool.getGeneratedKeys();
                rs.next();
                tool_id = rs.getLong(1);
            } else {
                return -2;
            }
            for (Map.Entry<Enchantment, Integer> entry : itemstack.getEnchantments().entrySet()) {
                if (mb.getEnchantment(entry.getKey()) == null)
                    continue;
                enchantment.clearParameters();
                enchantment.setLong(1, tool_id);
                enchantment.setString(2, entry.getKey().getKey().getKey());
                enchantment.setInt(3, entry.getValue());
                enchantment.executeUpdate();
            }
            return tool_id;
        } catch (SQLException sqle) {
            return -1;
        }
    }

    public boolean delete_tool(long id){
        PreparedStatement deleteTool = getDeleteTool();
        if (deleteTool == null){
            return false;
        }
        if (!delete_enchantments(id))
            return false;
        try{
            deleteTool.clearParameters();
            deleteTool.setLong(1, id);
            deleteTool.executeUpdate();
        }catch (SQLException sqle){
            return false;
        }
        return true;
    }

    public boolean delete_enchantments(long id){
        PreparedStatement deleteToolEnchantment = getDeleteToolEnchantment();
        if (deleteToolEnchantment == null){
            return false;
        }
        try{
            deleteToolEnchantment.clearParameters();
            deleteToolEnchantment.setLong(1, id);
            deleteToolEnchantment.executeUpdate();
        }catch (SQLException sqle){
            return false;
        }
        return true;
    }

    public boolean transfer_tool(long id, String uuid){
        PreparedStatement transferTool = getTransferTool();
        if (transferTool == null){
            return false;
        }
        try{
            transferTool.clearParameters();
            transferTool.setString(1, uuid);
            transferTool.setLong(2, id);
            transferTool.executeUpdate();
        }catch (SQLException sqle){
            return false;
        }
        return true;
    }

    public boolean setBroken(long id, boolean broken) {
        PreparedStatement breaktool = broken?getBreakTool():getRestoreTool();
        if (breaktool == null) {
            return false;
        }
        try {
            breaktool.clearParameters();
            breaktool.setInt(1, broken ? 1 : 0);
            breaktool.setLong(2, id);
            breaktool.executeUpdate();
        } catch (SQLException sqle) {
            return false;
        }
        return true;
    }

    public void break_tool(long id) {
        setBroken(id, true);
    }

    public boolean restore_tool(long id) {
        return setBroken(id, false);
    }

    public List<MendingTool> getPlayerTools_limited(String uuid, int book) {
        List<MendingTool> result = new LinkedList<>();
        MendingTool mt;
        long id;
        PreparedStatement playerTools_limited = getPlayerTools_limited();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (playerTools_limited == null || toolsEnchantments == null){
            return null;
        }
        try {
            playerTools_limited.clearParameters();
            playerTools_limited.setString(1, uuid);
            playerTools_limited.setInt(2, book*20);
            playerTools_limited.setInt(3, 20);
            ResultSet rs_tool = playerTools_limited.executeQuery();
            ResultSet rs_enchantments;
            while (rs_tool.next()) {
                id = rs_tool.getLong(1);
                mt = new MendingTool(id,
                        rs_tool.getInt(2),
                        rs_tool.getString(3),
                        rs_tool.getInt(4) > 0,
                        rs_tool.getInt(5),
                        uuid);
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    mt.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
                result.add(mt);
            }
        } catch (SQLException sqle) {
            return null;
        }
        return result;
    }

    public List<MendingTool> getPlayerTools(String uuid) {
        List<MendingTool> result = new LinkedList<>();
        MendingTool mt;
        long id;
        PreparedStatement playerTools = getPlayerTools();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (playerTools == null || toolsEnchantments == null){
            return null;
        }
        try {
            playerTools.clearParameters();
            playerTools.setString(1, uuid);
            ResultSet rs_tool = playerTools.executeQuery();
            ResultSet rs_enchantments;
            while (rs_tool.next()) {
                id = rs_tool.getLong(1);
                mt = new MendingTool(id,
                        rs_tool.getInt(2),
                        rs_tool.getString(3),
                        rs_tool.getInt(4) > 0,
                        rs_tool.getInt(5),
                        uuid);
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    mt.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
                result.add(mt);
            }
        } catch (SQLException sqle) {
            return null;
        }
        return result;
    }

    public List<MendingTool> getAllToolsWithoutBPID() {
        List<MendingTool> result = new LinkedList<>();
        MendingTool mt;
        long id;
        PreparedStatement allToolsWithoutBPID = getAllToolsWithoutBPID_();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (allToolsWithoutBPID == null || toolsEnchantments == null){
            return null;
        }
        try {
            allToolsWithoutBPID.clearParameters();
            ResultSet rs_tool = allToolsWithoutBPID.executeQuery();
            ResultSet rs_enchantments;
            while (rs_tool.next()) {
                id = rs_tool.getLong(1);
                mt = new MendingTool(id,
                        -1,
                        rs_tool.getString(2),
                        false,
                        0,
                        null);
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    mt.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
                result.add(mt);
            }
        } catch (SQLException sqle) {
            return new LinkedList<>();
        }
        return result;
    }

    public List<MendingTool> getAllTools(){
        List<MendingTool> result = new LinkedList<>();
        MendingTool mt;
        long id;
        PreparedStatement allTools = getAllToolsSorted_PS();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (allTools == null || toolsEnchantments == null){
            return null;
        }
        try {
            allTools.clearParameters();
            ResultSet rs_tool = allTools.executeQuery();
            ResultSet rs_enchantments;
            while (rs_tool.next()) {
                id = rs_tool.getLong(1);
                mt = new MendingTool(id,
                        rs_tool.getInt(2),
                        rs_tool.getString(3),
                        rs_tool.getInt(4) > 0,
                        rs_tool.getInt(5),
                        rs_tool.getString(6)
                );
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    mt.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
                result.add(mt);
            }
        } catch (SQLException sqle) {
            return new LinkedList<>();
        }
        return result;
    }

    public MendingTool getTool(Long id) {
        MendingTool result = null;
        PreparedStatement idTool = getIdTool();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (idTool == null || toolsEnchantments == null){
            return null;
        }
        try {
            idTool.clearParameters();
            idTool.setLong(1, id);
            ResultSet rs_tool = idTool.executeQuery();
            while (rs_tool.next()) {
                result = new MendingTool(id,
                        rs_tool.getInt(2),
                        rs_tool.getString(3),
                        rs_tool.getInt(4) > 0,
                        rs_tool.getInt(5),
                        rs_tool.getString(1));
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                ResultSet rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    result.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
            }
        } catch (SQLException sqle) {
            return null;
        }
        return result;
    }

    public boolean upgradeToolEnchantment(long id, String enchantment, int level){
        PreparedStatement upgradeEnchantment = getUpgradeEnchantment();
        if (upgradeEnchantment == null){
            return false;
        }
        try{
            upgradeEnchantment.clearParameters();
            upgradeEnchantment.setInt(1, level);
            upgradeEnchantment.setLong(2, id);
            upgradeEnchantment.setString(3, enchantment);
            upgradeEnchantment.executeUpdate();
        }catch (SQLException sqle){
            return false;
        }
        return true;
    }

    public boolean updateBlueprintID(long tool_id, MendingBlueprint mb, Map<String, Integer> enchantments){
        PreparedStatement updateBlueprintID = getUpdateBlueprintID();
        PreparedStatement enchantment = getInsertEnchantment();
        if (updateBlueprintID == null || enchantment == null){
            return false;
        }
        if (!delete_enchantments(tool_id))
            return false;
        try {
            updateBlueprintID.clearParameters();
            updateBlueprintID.setInt(1, mb.getID());
            updateBlueprintID.setString(2, mb.getMaterial());
            updateBlueprintID.setLong(3, tool_id);
            updateBlueprintID.executeUpdate();
            for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
                if (mb.getEnchantment(entry.getKey()) == null)
                    continue;
                enchantment.clearParameters();
                enchantment.setLong(1, tool_id);
                enchantment.setString(2, entry.getKey());
                enchantment.setInt(3, entry.getValue());
                enchantment.executeUpdate();
            }
        }catch (SQLException sqle){
            return false;
        }

        return true;
    }

    public List<MendingTool> getToolsSorted(int book){
        List<MendingTool> result = new LinkedList<>();
        MendingTool mt;
        long id;
        PreparedStatement allTools = getAllToolsSortedLimited();
        PreparedStatement toolsEnchantments = getToolsEnchantments();
        if (allTools == null || toolsEnchantments == null){
            return null;
        }
        try {
            allTools.clearParameters();
            allTools.setInt(1, book*20);
            allTools.setInt(2, 20);
            ResultSet rs_tool = allTools.executeQuery();
            ResultSet rs_enchantments;
            while (rs_tool.next()) {
                id = rs_tool.getLong(1);
                mt = new MendingTool(id,
                        rs_tool.getInt(2),
                        rs_tool.getString(3),
                        rs_tool.getInt(4) > 0,
                        rs_tool.getInt(5),
                        rs_tool.getString(6)
                        );
                toolsEnchantments.clearParameters();
                toolsEnchantments.setLong(1, id);
                rs_enchantments = toolsEnchantments.executeQuery();
                while (rs_enchantments.next()) {
                    mt.addEnchantment(rs_enchantments.getString(1),
                            rs_enchantments.getInt(2));
                }
                result.add(mt);
            }
        } catch (SQLException sqle) {
            return null;
        }
        return result;
    }
}
