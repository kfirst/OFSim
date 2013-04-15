package csnet.openflow.viewer.dataLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseLoader implements DataLoader {

    private Connection conn;
    private PreparedStatement statementTSIP;
    private PreparedStatement statementTHSPI;
    private PreparedStatement statementTNPI;
    private PreparedStatement statementTNFM;
    private PreparedStatement statementFTS;
    private PreparedStatement statementTNFST;
    private PreparedStatement statementTNP;
    private PreparedStatement statementTNLP;

    public DatabaseLoader(String tableName, String user, String password) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306",
                    user, password);
            Statement statement = conn.createStatement();
            statement.execute("USE `" + tableName + "`");
            statement.close();
            statementTSIP = conn.prepareStatement("SELECT sum(`packet_size`) as sum FROM `packet` "
                    + "WHERE `in_time`>=? AND `in_time`<?");
            statementTHSPI = conn.prepareStatement("SELECT sum(`header_size`) as sum FROM `packet` AS `p` "
                    + "INNER JOIN `controller` AS `c` ON `p`.`packet_index`=`c`.`packet_index` AND "
                    + "`c`.`in_time`>=? AND `c`.`in_time`<?");
            statementTNPI = conn.prepareStatement("SELECT count(`packet_index`) as count FROM `controller` "
                    + "WHERE `in_time`>=? AND `in_time`<?");
            statementTNFM = conn.prepareStatement("SELECT count(`flow_index`) as count FROM `flow` "
                    + "WHERE `in_time`>=? AND `in_time`<? AND `flow_index`<>0");
            statementFTS = conn.prepareStatement("SELECT count(`flow_index`) as count FROM `flow` "
                    + "WHERE `in_time`<=? AND (`out_time`>? OR `out_time`=0) AND `flow_index`<>0");
            statementTNFST = conn.prepareStatement("SELECT count(`flow_index`) as count FROM `flow` "
                    + "WHERE `survival_time`>? AND `survival_time`<=? AND `flow_index`<>0");
            statementTNP = conn.prepareStatement("SELECT count(`packet_index`) as count FROM `packet` "
                    + "WHERE `in_time`>=? AND `in_time`<?");
            statementTNLP = conn.prepareStatement("SELECT count(`packet_index`) as count FROM `packet` "
                    + "WHERE `in_time`>=? AND `in_time`<? AND `out_time`=0");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getStartTime() {
        long ret = -1;
        try {
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery("SELECT min(`in_time`) as min FROM `packet`");
            set.first();
            ret = set.getLong("min");
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public long getEndTime() {
        long ret = -1;
        try {
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery("SELECT max(`in_time`) as max FROM `packet`");
            set.first();
            ret = set.getLong("max");
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalSizeOfInputPacket(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTSIP.setLong(1, startTime);
            statementTSIP.setLong(2, endTime);
            ResultSet set = statementTSIP.executeQuery();
            set.first();
            ret = set.getInt("sum");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalHeaderSizeOfPacketIn(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTHSPI.setLong(1, startTime);
            statementTHSPI.setLong(2, endTime);
            ResultSet set = statementTHSPI.executeQuery();
            set.first();
            ret = set.getInt("sum");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalNumOfPacketIn(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTNPI.setLong(1, startTime);
            statementTNPI.setLong(2, endTime);
            ResultSet set = statementTNPI.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalNumOfFlowMod(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTNFM.setLong(1, startTime);
            statementTNFM.setLong(2, endTime);
            ResultSet set = statementTNFM.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getFlowTableSize(long time) {
        int ret = -1;
        try {
            statementFTS.setLong(1, time);
            statementFTS.setLong(2, time);
            ResultSet set = statementFTS.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getMaxFlowSurvivalTime() {
        int ret = -1;
        try {
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery("SELECT max(`survival_time`) as max FROM `flow`");
            set.first();
            ret = set.getInt("max");
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalNumOfFlowSurvivalTimeBetween(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTNFST.setLong(1, startTime);
            statementTNFST.setLong(2, endTime);
            ResultSet set = statementTNFST.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalNumOfPacket(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTNP.setLong(1, startTime);
            statementTNP.setLong(2, endTime);
            ResultSet set = statementTNP.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public int getTotalNumOfLostPacket(long startTime, long endTime) {
        int ret = -1;
        try {
            statementTNLP.setLong(1, startTime);
            statementTNLP.setLong(2, endTime);
            ResultSet set = statementTNLP.executeQuery();
            set.first();
            ret = set.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public void close() {
        try {
            conn.close();
            statementTSIP.close();
            statementTHSPI.close();
            statementTNPI.close();
            statementTNFM.close();
            statementFTS.close();
            statementTNFST.close();
            statementTNP.close();
            statementTNLP.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
