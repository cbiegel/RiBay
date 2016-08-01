package com.ribay.server.util.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayOutputStream;
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
        if ((suppliers == null) || (suppliers.length == 0)) {
            throw new IllegalArgumentException("at least one supplier must be set!");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // write values as json. this is safer than using java serialization
            ObjectWriter writer = new ObjectMapper().writer();

            // add some salt first
            writer.writeValue(baos, MY_SALT);

            for (Supplier<?> supplier : suppliers) {
                // serialize data
                Object o = supplier.get();
                writer.writeValue(baos, o);
            }

            baos.close();

            byte[] serialized = baos.toByteArray();
            String digested = DigestUtils.md5DigestAsHex(serialized);
            return digested;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating hash", e);
        }
    }

}
