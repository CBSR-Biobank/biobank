
package edu.ualberta.med.biobank.scanlib;

import org.eclipse.core.runtime.Assert;

public class ScanLibFactory {

    public static ScanLib getScanLib() {
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")) {
            return new ScanLibWin32();
        }
        else if (osname.startsWith("Linux")) {
            return new ScanLibLinux();
        }
        Assert.isTrue(false, "No ScanLib for OS " + osname);
        return null;
    }

}
