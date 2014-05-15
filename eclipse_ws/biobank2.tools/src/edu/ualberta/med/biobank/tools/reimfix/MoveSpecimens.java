package edu.ualberta.med.biobank.tools.reimfix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.tools.GenericAppArgs;

/**
 * Moves the specimens held for the REIM study from site CBSR to site REIM.
 */
public class MoveSpecimens {

    private static final Logger log = LoggerFactory
        .getLogger(MoveSpecimens.class);

    private static final List<String> containersToMove;
    static {
        List<String> aList = Arrays.asList(
            "98AR01",
            "98AR02",
            "98AR03 ",
            "13A1103",
            "13D1101",
            "13D1102",
            "13D1103",
            "13D1105",
            "13D1106",
            "13D1107",
            "13E1101",
            "13E1102",
            "13E1103",
            "13F1101",
            "13F1102",
            "13F1103",
            "13F1104",
            "13F1105",
            "13F1106",
            "15A1101",
            "15A1102",
            "15A1103",
            "15A1104");
        containersToMove = Collections.unmodifiableList(aList);
    }

    private static final String containerQry =
        "SELECT container.id, ctype.id "
            + "FROM container "
            + "JOIN container_type ctype ON ctype.id=container.container_type_id "
            + "JOIN center ON center.id=container.site_id "
            + "WHERE container.label=? AND center.name_short='REIM'";

    private static final String siteIdQry =
        "SELECT id FROM center WHERE name_short='REIM' and discriminator='Site'";

    private static final String moveSpecimensQry =
        "UPDATE specimen spc "
            + "JOIN specimen_position pos ON pos.specimen_id=spc.id "
            + "JOIN container ct ON ct.id=pos.container_id "
            + "JOIN center ON center.id=ct.site_id "
            + "SET pos.container_id=?, pos.container_type_id=?, spc.current_center_id=? "
            + "WHERE label = ?";

    private final Connection dbCon;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.help) {
                System.out.println("invalida arguments");
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg);
                System.exit(-1);
            }
            new MoveSpecimens(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MoveSpecimens(GenericAppArgs args) throws SQLException {
        dbCon = DriverManager.getConnection("jdbc:mysql://" + args.hostname
            + ":3306/biobank", "dummy", "ozzy498");

        Integer reimSiteId = getReimSiteId();
        Map<String, ContainerInfo> reimContainerInfo = getReimContainerInfo();
        moveSpecimens(reimSiteId, reimContainerInfo);
    }

    private Integer getReimSiteId() throws SQLException {
        PreparedStatement ps = dbCon.prepareStatement(siteIdQry);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            rs.last();
            if (rs.getRow() != 1) {
                throw new IllegalStateException(
                    "query did not return single row");
            }
            int result = rs.getInt(1);
            log.debug("getReimSiteId: {}", result);
            return result;
        }
        throw new IllegalStateException("query did not return any results");
    }

    private Map<String, ContainerInfo> getReimContainerInfo()
        throws SQLException {
        Map<String, ContainerInfo> result =
            new HashMap<String, ContainerInfo>(containersToMove.size());
        for (String label : containersToMove) {
            PreparedStatement ps = dbCon.prepareStatement(containerQry);
            ps.setString(1, label);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ContainerInfo containerInfo = new ContainerInfo();
                containerInfo.containerId = rs.getInt(1);
                containerInfo.containerTypeId = rs.getInt(2);
                log.debug(
                    "getReimContainerInfo: containerId: {}, containerTypeId: {}",
                    containerInfo.containerId,
                    containerInfo.containerTypeId);
                result.put(label, containerInfo);
            }
        }
        if (result.size() <= 0) {
            throw new IllegalStateException("query did not return any results");
        }
        return result;
    }

    private void moveSpecimens(
        Integer reimSiteId,
        Map<String, ContainerInfo> reimContainerInfo) throws SQLException {
        for (Entry<String, ContainerInfo> entry : reimContainerInfo.entrySet()) {
            String label = entry.getKey();
            ContainerInfo containerInfo = entry.getValue();

            PreparedStatement ps = dbCon.prepareStatement(moveSpecimensQry);
            ps.setInt(1, containerInfo.containerId);
            ps.setInt(2, containerInfo.containerTypeId);
            ps.setInt(3, reimSiteId);
            ps.setString(4, label);
            log.debug("moveSpecimens: moving {}", label);
            ps.executeUpdate();
        }
    }

    private static class ContainerInfo {
        Integer containerId;
        Integer containerTypeId;
    }

}
