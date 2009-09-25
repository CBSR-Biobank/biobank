
package edu.ualberta.med.biobank.importer;

import java.security.Key;
import java.util.Arrays;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Bullzip Access To MySQL does not import the chr_nr field in the patient table
 * properly. Therefore, the table is exported to a CSV file and the CHR number
 * exported from there.
 * 
 */
public class BlowfishCipher {

    private static byte [] SECRET = new byte [] {
        (byte) 97, (byte) 68, (byte) 53, (byte) 94, (byte) 100, (byte) 85,
        (byte) 52, (byte) 102, (byte) 38, (byte) 97, (byte) 104, (byte) 71,
        (byte) 79, (byte) 106, (byte) 69, (byte) 43, (byte) 50, (byte) 52,
        (byte) 71, (byte) 78, (byte) 101 };

    private static final char HEX_DIGIT[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f' };

    private Cipher cipher;

    private String decryptErrorMsg;

    public static void main(String [] args) throws Exception {
        new BlowfishCipher();
    }

    public BlowfishCipher() throws Exception {
        Key key = new SecretKeySpec(SECRET, "Blowfish");
        cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
    }

    public String decode(byte [] msg) throws Exception {
        // showMem(msg, msg.length);

        try {
            String str = new String(cipher.doFinal(msg)).trim();

            if (str.length() < 17) {
                decryptErrorMsg = "decode result is too short";
                return null;
            }

            byte [] decrypted = str.getBytes();

            CRC32 crc = new CRC32();
            crc.update(decrypted, 0, decrypted.length - 8);
            long msgCrc = Long.parseLong(str.substring(str.length() - 8,
                str.length()), 16);
            if (crc.getValue() != msgCrc) {
                decryptErrorMsg = "invalid CRC";
                return null;
            }

            return new String(Arrays.copyOfRange(decrypted, 5,
                decrypted.length - 11));
        }
        catch (IllegalBlockSizeException e) {
            decryptErrorMsg = "could not decrypt";
            return null;
        }
    }

    @SuppressWarnings("unused")
    private void showMem(byte [] arr, int len) {
        int count, offset = 0;
        String buf;

        while (offset < len) {
            buf = new String();

            count = (len < 16) ? len : 16;
            for (int i = offset, n = offset + 16; i < n; ++i) {
                if (i < len) {
                    buf += HEX_DIGIT[(arr[i] & 0xf0) >> 4];
                    buf += HEX_DIGIT[arr[i] & 0x0f];
                    buf += " ";
                }
                else {
                    buf += "   ";
                }
            }
            for (int i = offset, n = offset + count; i < n; ++i) {
                if (i < len) {
                    buf += ((arr[i] >= 0x20) && (arr[i] <= 0x7E))
                        ? (char) arr[i] : '.';
                }
            }
            System.out.format("%05x: %s\n", offset, buf);
            offset += count;
        }
    }

    public String getDecryptErrorMsg() {
        return decryptErrorMsg;
    }

}