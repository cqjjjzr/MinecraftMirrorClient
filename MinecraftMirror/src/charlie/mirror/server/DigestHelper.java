package charlie.mirror.server;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestHelper {
    public static String sha1(File file, long length) throws IOException, NoSuchAlgorithmException {
        if(Files.size(file.toPath()) != length){
            return "";
        }
        byte[] fc = Files.readAllBytes(file.toPath());
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
        digest.update(fc);
        byte messageDigest[] = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }
}
