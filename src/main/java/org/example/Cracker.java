package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
//Team members: Kamakshi Prasanna, Carlos Pineda, Joe Park
public class Cracker {
    public static final String SHADOW_SIMPLE= "shadow";
    public static final String COMMON_PASSWD_FILE = "common-passwords.txt";

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

    public static String md5HashPasswd(String commonPasswd, String salt){
        try {
           return MD5Shadow.crypt(commonPasswd, salt);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        Cracker cracker = new Cracker();
        //read common password
        Set<String> commonPasswdList = new HashSet<>(cracker.readFile(COMMON_PASSWD_FILE));
        //read SHADOW_SIMPLE
        List<String> shadowSimpleList = cracker.readFile(SHADOW_SIMPLE);

        for (String userRecord: shadowSimpleList) {
            String[] userArr = userRecord.split(":");
            String user = userArr[0];
            String sHash = userArr[1];
            String[] sHashArr = sHash.split("\\$");
            //System.out.println(shasharr);
            String salt = sHashArr[2];
            String hash = sHashArr[3];
            //System.out.println(user +" " + salt +" " + hash);
            for (String commonPasswd : commonPasswdList) {
                String hashedCommonPasswd = md5HashPasswd(commonPasswd, salt);
                if (hash.equals(hashedCommonPasswd)) {
                    System.out.println(user + ":" + commonPasswd);
                    break;
                }
            }
        }
    }
}
