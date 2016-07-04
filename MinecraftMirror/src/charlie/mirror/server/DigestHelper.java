package charlie.mirror.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestHelper {
    public static String sha1(File file, long length) throws IOException, NoSuchAlgorithmException {
        if(FileUtils.sizeOf(file) != length){
            return "";
        }
        return sha1(FileUtils.readFileToByteArray(file));
    }

    public static String sha1(File file) throws IOException, NoSuchAlgorithmException {
        return sha1(FileUtils.readFileToByteArray(file));
    }

    public static String sha1(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
        digest.update(content);
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

    public static String md5(File file, long length) throws IOException, NoSuchAlgorithmException {
        if(FileUtils.sizeOf(file) != length){
            return "";
        }
        return md5(FileUtils.readFileToByteArray(file));
    }

    public static String md5(File file) throws IOException, NoSuchAlgorithmException {
        return md5(FileUtils.readFileToByteArray(file));
    }

    public static String md5(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(content);
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

    public enum Digest{
        MD5, SHA1
    }
}
