package edu.ualberta.med.biobank.test.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.test.TestDb;

public class TestBatchOpActionUtils extends TestDb {

    @Test
    public void getFileMetaData() throws IOException, NoSuchAlgorithmException {
        File original = File.createTempFile("original", ".tmp");
        original.deleteOnExit();

        FileWriter fw = new FileWriter(original, true);
        fw.append("hello, test world!");
        fw.close();

        FileData fileData = FileData.fromFile(original);

        session.beginTransaction();
        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(
            session, getGlobalAdmin(), fileData);
        session.getTransaction().commit();

        FileMetaData metaData = BatchOpActionUtil.getFileMetaData(session, batchOp.getId());
        Assert.assertNotNull(metaData);
        Assert.assertEquals(original.getName(), metaData.getName());
    }

}
