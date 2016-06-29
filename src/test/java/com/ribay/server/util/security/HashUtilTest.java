package com.ribay.server.util.security;

import com.ribay.server.material.User;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by CD on 29.06.2016.
 */
public class HashUtilTest {

    @Test
    public void testGenerateHash() {
        // user object
        User user1 = new User();
        user1.setUuid(new UUID(123L, 456L));
        user1.setName("testUser");
        user1.setEmailAddress(null);

        String hash1 = HashUtil.generateHash(user1::getUuid, user1::getName, user1::getEmailAddress);
        assertNotNull(hash1);

        // other user object with same contents
        User user2 = new User();
        user2.setUuid(new UUID(123L, 456L));
        user2.setName("testUser");
        user2.setEmailAddress(null);

        String hash2 = HashUtil.generateHash(user1::getUuid, user1::getName, user1::getEmailAddress);
        assertNotNull(hash2);

        assertEquals("when having same data the hash should be equal", hash1, hash2);
    }


}
