package edu.ualberta.med.scannerconfig.dmscanlib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestDmScanLibCommon.class,
    TestDmScanLibLinux.class,
    TestDmScanLibWindows.class
})
public class ScanLibSuite {
}
