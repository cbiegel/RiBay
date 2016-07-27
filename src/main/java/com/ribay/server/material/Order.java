package com.ribay.server.material;

import com.ribay.server.exception.IncorrectHashException;
import com.ribay.server.util.security.HashUtil;

import java.util.function.Supplier;

/**
 * Created by CD on 25.06.2016.
 */
public class Order {

    private String id;
    private String sessionId;
    private String userId;
    private Address address;
    private Cart cart;
    private long dateStarted;
    private String hash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public long getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(long dateStarted) {
        this.dateStarted = dateStarted;
    }

    /**
     * Only for junit tests
     *
     * @return the hash for this order
     */
    public String getHash() {
        return hash;
    }

    public void updateHash() {
        this.hash = HashUtil.generateHash(getSuppliers());
    }

    public void checkHash() throws IncorrectHashException {
        String found = this.hash;
        String expected = HashUtil.generateHash(getSuppliers());
        if (!expected.equals(found)) {
            throw new IncorrectHashException(expected, found);
        }
    }

    /**
     * @return data to use for geneating hash
     */
    private Supplier<?>[] getSuppliers() {
        // use every field except the ones that the user has to fill ('address') and except the hash itself
        Supplier<?>[] suppliers = {this::getId, this::getSessionId, this::getUserId, this::getCart, this::getDateStarted};
        return suppliers;
    }

}
