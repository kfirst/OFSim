package csnet.openflow.logger;

import csnet.openflow.packet.model.Packet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

public class DatabaseLogger implements Logger {

    private Connection conn0;
    private Connection conn1;
    private String dbName;
    private PreparedStatement statementG;
    private PreparedStatement statementR;
    private PreparedStatement statementCI;
    private PreparedStatement statementCO;
    private PreparedStatement statementFI;
    private PreparedStatement statementFO;
    private LinkedHashMap<Long, PacketEntry> packetBuffer;
    private LinkedHashMap<Long, FlowEntry> flowBuffer;
    private LinkedHashMap<Long, ControllerEntry> controllerBuffer;
    private int maxBufferSize = 50000;
    private Set<String> dbNames;

    public DatabaseLogger(String user, String password) {
        try {
            conn0 = DriverManager.getConnection("jdbc:mysql://localhost:3306",
                    user, password);
            conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306",
                    user, password);
            packetBuffer = new LinkedHashMap<>();
            flowBuffer = new LinkedHashMap<>();
            controllerBuffer = new LinkedHashMap<>();
            dbNames = new TreeSet<>();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DatabaseLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void config(String dbName) {
        try {
            this.dbName = dbName;
            if (!dbNames.contains(dbName)) {
                createDatabaseAndTables();
                createStatements();
                dbNames.add(dbName);
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DatabaseLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createDatabaseAndTables() throws SQLException {
        Statement statement = conn0.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS `" + dbName + "`;");
        statement.execute("USE `" + dbName + "`");
        statement.execute("DROP TABLE IF EXISTS `flow`");
        statement.execute("CREATE TABLE `flow` ("
                + "`flow_index` bigint(20) NOT NULL,"
                + "`in_time` bigint(20) NOT NULL DEFAULT '0',"
                + "`out_time` bigint(20) NOT NULL DEFAULT '0',"
                + "`survival_time` bigint(20) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`flow_index`),"
                + "KEY `in_time` (`in_time`),"
                + "KEY `out_time` (`out_time`),"
                + "KEY `survival_time` (`survival_time`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        statement.execute("DROP TABLE IF EXISTS `packet`");
        statement.execute("CREATE TABLE `packet` ("
                + "`packet_index` bigint(20) NOT NULL,"
                + "`flow_index` bigint(20) NOT NULL DEFAULT '0',"
                + "`header_size` smallint(11) NOT NULL DEFAULT '0',"
                + "`packet_size` smallint(11) NOT NULL DEFAULT '0',"
                + "`in_time` bigint(20) NOT NULL DEFAULT '0',"
                + "`out_time` bigint(20) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`packet_index`),"
                + "KEY `in_time` (`in_time`),"
                + "KEY `out_time` (`out_time`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        statement.execute("DROP TABLE IF EXISTS `controller`");
        statement.execute("CREATE TABLE IF NOT EXISTS `controller` ("
                + "`packet_index` bigint(20) NOT NULL,"
                + "`in_time` bigint(20) NOT NULL DEFAULT '0',"
                + "`out_time` bigint(20) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`packet_index`),"
                + "KEY `in_time` (`in_time`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        statement.close();
        statement = conn1.createStatement();
        statement.execute("USE `" + dbName + "`");
        statement.close();
    }

    private void createStatements() throws SQLException {
        statementG = conn0.prepareStatement("INSERT INTO `packet`"
                + "(packet_index,flow_index,header_size,packet_size,in_time,out_time) VALUES (?,?,?,?,?,?);");
        statementR = conn0.prepareStatement("UPDATE `packet` SET "
                + "flow_index=?,out_time=? " + "WHERE packet_index=?;");
        statementCI = conn1.prepareStatement("INSERT INTO `controller`"
                + "(packet_index,in_time,out_time) VALUES (?,?,?);");
        statementCO = conn1.prepareStatement("UPDATE `controller` SET "
                + "out_time=? " + "WHERE packet_index=?;");
        statementFI = conn1.prepareStatement("INSERT INTO `flow`"
                + "(flow_index,in_time,out_time,survival_time) VALUES (?,?,?,?);");
        statementFO = conn1.prepareStatement("UPDATE `flow` SET "
                + "out_time=?,survival_time=? " + "WHERE flow_index=?;");
    }

    @Override
    public void logPacketOfGenerator(Packet packet) {
        try {
            PacketEntry entry = new PacketEntry();
            entry.packet_index = packet.statistic.packetIndex;
            entry.header_size = packet.getSize(Packet.SizeType.header);
            entry.total_size = packet.getSize(Packet.SizeType.total);
            entry.in_time = packet.getTimestamp();
            packetBuffer.put(entry.packet_index, entry);
            if (packetBuffer.size() > maxBufferSize) {
                clearPacketBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logPacketOfReceiver(Packet packet) {
        try {
            PacketEntry entry = packetBuffer.get(packet.statistic.packetIndex);
            if (entry == null) {
                entry = new PacketEntry();
                entry.operation = 1;
                entry.packet_index = packet.statistic.packetIndex;
                packetBuffer.put(entry.packet_index, entry);
            }
            entry.out_time = packet.getTimestamp();
            entry.flow_index = packet.statistic.flowIndex;
            if (packetBuffer.size() > maxBufferSize) {
                clearPacketBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearPacketBuffer() throws SQLException {
        statementG.clearBatch();
        statementR.clearBatch();
        boolean insert = false;
        boolean update = false;
        for (Map.Entry<Long, PacketEntry> pair : packetBuffer.entrySet()) {
            PacketEntry entry = pair.getValue();
            if (entry.operation == 0) {
                statementG.setLong(1, entry.packet_index);
                statementG.setLong(2, entry.flow_index);
                statementG.setLong(3, entry.header_size);
                statementG.setLong(4, entry.total_size);
                statementG.setLong(5, entry.in_time);
                statementG.setLong(6, entry.out_time);
                statementG.addBatch();
                insert = true;
            } else {
                statementR.setLong(1, entry.flow_index);
                statementR.setLong(2, entry.out_time);
                statementR.setLong(3, entry.packet_index);
                statementR.addBatch();
                update = true;
            }
        }
        if (insert) {
            statementG.executeBatch();
        }
        if (update) {
            statementR.executeBatch();
        }
        packetBuffer.clear();
    }

    @Override
    public void logPacketOfControllerIn(Packet packet) {
        try {
            ControllerEntry entry = new ControllerEntry();
            entry.packet_index = packet.statistic.packetIndex;
            entry.in_time = packet.getTimestamp();
            controllerBuffer.put(entry.packet_index, entry);
            if (controllerBuffer.size() > maxBufferSize) {
                clearControllerBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logPacketOfControllerOut(Packet packet) {
        try {
            ControllerEntry entry = controllerBuffer.get(packet.statistic.packetIndex);
            if (entry == null) {
                entry = new ControllerEntry();
                entry.operation = 1;
                entry.packet_index = packet.statistic.packetIndex;
                controllerBuffer.put(entry.packet_index, entry);
            }
            entry.out_time = packet.getTimestamp();
            if (controllerBuffer.size() > maxBufferSize) {
                clearControllerBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearControllerBuffer() throws SQLException {
        statementCI.clearBatch();
        statementCO.clearBatch();
        boolean insert = false;
        boolean update = false;
        for (Map.Entry<Long, ControllerEntry> pair : controllerBuffer.entrySet()) {
            ControllerEntry entry = pair.getValue();
            if (entry.operation == 0) {
                statementCI.setLong(1, entry.packet_index);
                statementCI.setLong(2, entry.in_time);
                statementCI.setLong(3, entry.out_time);
                statementCI.addBatch();
                insert = true;
            } else {
                statementCO.setLong(1, entry.out_time);
                statementCO.setLong(2, entry.packet_index);
                statementCO.addBatch();
                update = true;
            }
        }
        if (insert) {
            statementCI.executeBatch();
        }
        if (update) {
            statementCO.executeBatch();
        }
        controllerBuffer.clear();
    }

    @Override
    public void logFlowEntryCreate(Packet packet) {
        try {
            FlowEntry entry = new FlowEntry();
            entry.flow_index = packet.statistic.flowIndex;
            entry.in_time = packet.statistic.createTime;
            flowBuffer.put(entry.flow_index, entry);
            if (flowBuffer.size() > maxBufferSize) {
                clearFlowBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logFlowEntryRemove(Packet packet) {
        try {
            FlowEntry entry = flowBuffer.get(packet.statistic.flowIndex);
            if (entry == null) {
                entry = new FlowEntry();
                entry.operation = 1;
                entry.flow_index = packet.statistic.flowIndex;
                flowBuffer.put(entry.flow_index, entry);
            }
            entry.out_time = packet.statistic.deadline;
            entry.survival_time = packet.statistic.deadline - packet.statistic.createTime;
            if (flowBuffer.size() > maxBufferSize) {
                clearFlowBuffer();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFlowBuffer() throws SQLException {
        statementFI.clearBatch();
        statementFO.clearBatch();
        boolean insert = false;
        boolean update = false;
        for (Map.Entry<Long, FlowEntry> pair : flowBuffer.entrySet()) {
            FlowEntry entry = pair.getValue();
            if (entry.operation == 0) {
                statementFI.setLong(1, entry.flow_index);
                statementFI.setLong(2, entry.in_time);
                statementFI.setLong(3, entry.out_time);
                statementFI.setLong(4, entry.survival_time);
                statementFI.addBatch();
                insert = true;
            } else {
                statementFO.setLong(1, entry.out_time);
                statementFO.setLong(2, entry.survival_time);
                statementFO.setLong(3, entry.flow_index);
                statementFO.addBatch();
                update = true;
            }
        }
        if (insert) {
            statementFI.executeBatch();
        }
        if (update) {
            statementFO.executeBatch();
        }
        flowBuffer.clear();
    }

    @Override
    public void close() {
        try {
            clearPacketBuffer();
            statementG.close();
            statementR.close();

            clearControllerBuffer();
            statementCI.close();
            statementCO.close();

            clearFlowBuffer();
            statementFI.close();
            statementFO.close();

            conn0.close();
            conn1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class PacketEntry {

    long packet_index;
    int header_size;
    int total_size;
    long in_time;
    long out_time;
    long flow_index;
    int operation = 0;
}

class FlowEntry {

    long flow_index;
    long in_time;
    long out_time;
    long survival_time;
    int operation = 0;
}

class ControllerEntry {

    long packet_index;
    long in_time;
    long out_time;
    int operation = 0;
}