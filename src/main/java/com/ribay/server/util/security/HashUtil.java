package com.ribay.server.util.security;

import org.springframework.util.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

/**
 * Created by CD on 29.06.2016.
 */
public class HashUtil {

    private static final byte[] MY_SALT = {4, 8, 15, 16, 23, 42, 54, 74, 90, 20, 14};

    /**
     * Generates a hash for the specified suppliers using a fixed salt.
     *
     * @param suppliers the functions to generate the data that is used to build the hash
     * @return the md5 hash for the specified data as an hex string
     */
    public static String generateHash(Supplier<?>... suppliers) {
        if (suppliers == null) {
            throw new IllegalArgumentException("at least one supplier must be set!");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            for (Supplier<?> supplier : suppliers) {
                // serialize data
                Object o = supplier.get();
                oos.writeObject(o);
            }
            // add some salt
            oos.write(MY_SALT);
            oos.close(); // flush and close

            byte[] serialized = baos.toByteArray();
            String digested = DigestUtils.md5DigestAsHex(serialized);
            return digested;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating hash", e);
        }
    }

}
