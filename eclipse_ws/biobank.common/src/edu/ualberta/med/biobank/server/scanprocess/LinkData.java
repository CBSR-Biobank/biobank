package edu.ualberta.med.biobank.server.scanprocess;

import java.util.Map;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.util.linking.Cell;

public class LinkData implements ScanProcessData {
    public Map<RowColPos, Cell> cells;
    public boolean rescanMode;
    public User user;
}
