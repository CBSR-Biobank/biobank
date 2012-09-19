package edu.ualberta.med.biobank.test.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.test.TestDb;

public class TestFileData extends TestDb {
    @Test
    public void fromFile() throws IOException, NoSuchAlgorithmException {
        File original = File.createTempFile("original", ".tmp");
        original.deleteOnExit();

        FileWriter fw = new FileWriter(original, true);
        fw.append("hello, test world!");
        fw.close();

        FileData data = FileData.fromFile(original);

        File copy = File.createTempFile("copy", ".tmp");
        copy.deleteOnExit();
        FileOutputStream out = new FileOutputStream(copy);
        out.write(data.getBytes());
        out.close();

        Assert.assertTrue(
            isContentEqual(
                new FileInputStream(original),
                new FileInputStream(copy)));
    }

    @Test
    public void fromFileCheckCompression()
        throws IOException, NoSuchAlgorithmException {
        File original = File.createTempFile("original", ".tmp");
        original.deleteOnExit();

        FileWriter fw = new FileWriter(original, true);
        for (int i = 0; i < 100; i++) {
            fw.append("hello! ");
        }
        fw.close();

        FileData data = FileData.fromFile(original);
        Assert.assertEquals(new Long(data.getBytes().length),
            data.getMetaData().getSize());
        Assert.assertTrue(
            data.getBytes().length > data.getCompressedBytes().length);
    }

    private static boolean isContentEqual(InputStream in1, InputStream in2)
        throws IOException {
        if (!(in1 instanceof BufferedInputStream)) {
            in1 = new BufferedInputStream(in1);
        }
        if (!(in2 instanceof BufferedInputStream)) {
            in2 = new BufferedInputStream(in2);
        }

        int ch = in1.read();
        while (-1 != ch) {
            int ch2 = in2.read();
            if (ch != ch2) {
                return false;
            }
            ch = in1.read();
        }

        int ch2 = in2.read();
        return (ch2 == -1);
    }
}
