package org.example;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCracker {
    public static final String SHADOW_SIMPLE= "shadow-simple.txt";
    public static final String COMMON_PASSWD_FILE = "common-passwords.txt";

    public static List<String> shadowSimpleList = new ArrayList<>();
    public static List<String> commonPasswdList = new ArrayList<>();

    private final MessageDigest messageDigest;

    public SimpleCracker() throws Exception{
        messageDigest = MessageDigest.getInstance("MD5");
    }
    public static String toHex(byte[] bytes){
        BigInteger bi = new BigInteger( 1, bytes);
        return String.format("%0" + (bytes.length << 1) +"X", bi);
    }

    public List<String> readFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + fileName);
        }
        // Read the content of the InputStream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public String hashPassword(String salt, String password) {
        String saltPassword = salt + password;
        byte[] hashedPassword = messageDigest.digest(saltPassword.getBytes()); // Hash the password
        return toHex(hashedPassword);
    }

    public static void main(String[] args) throws Exception {
        SimpleCracker simpleCracker = new SimpleCracker();
        //read common password
        commonPasswdList = simpleCracker.readFile(COMMON_PASSWD_FILE);
        //read SHADOW_SIMPLE
        shadowSimpleList = simpleCracker.readFile(SHADOW_SIMPLE);
        for (String userRecord: shadowSimpleList) {
            String[] userArr = userRecord.split(":");
            String user = userArr[0];
            String salt = userArr[1];
            String hash = userArr[2];
            //System.out.println(user +" " + salt +" " + hash);
            for (String commonPasswd : commonPasswdList) {
                String hashedCommonPasswd = simpleCracker.hashPassword(salt, commonPasswd);
                if (hash.equals(hashedCommonPasswd)) {
                    System.out.println(user + ":" + commonPasswd);
                    break;
                }
            }
        }
    }
}
