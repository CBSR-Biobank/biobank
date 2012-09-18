package edu.ualberta.med.biobank.common.action.util;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.activation.MimetypesFileTypeMap;

import edu.ualberta.med.biobank.model.Attachment;
import edu.ualberta.med.biobank.model.type.Hash.MD5Hash;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

public class AttachmentUtil {
    private static final MimetypesFileTypeMap mimetypesFileTypeMap =
        new MimetypesFileTypeMap();

    public static synchronized Attachment fromFile(File file)
        throws NoSuchAlgorithmException, IOException {
        Attachment attachment = new Attachment();

        attachment.setFileName(file.getName());
        attachment.setContentType(mimetypesFileTypeMap.getContentType(file));
        attachment.setSize(file.length());

        attachment.setMd5Hash(MD5Hash.fromFile(file));
        attachment.setSha1Hash(SHA1Hash.fromFile(file));

        return attachment;
    }
}
