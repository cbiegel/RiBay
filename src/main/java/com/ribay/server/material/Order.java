package com.ribay.server.material;

import com.ribay.server.exception.IncorrectHashException;
import com.ribay.server.util.security.HashUtil;

import java.util.function.Supplier;

/**
 * Created by CD on 25.06.2016.
 */
public class Order {

    private String id;
    private String userId;
    private Address address;
    private Cart cart;
    private String hash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void updateHash() {
        this.hash = HashUtil.generateHash(getSuppliers());
    }

    public void checkHash() throws IncorrectHashException {
        String found = this.hash;
        String expected = HashUtil.generateHash(getSuppliers());
        if (!found.equals(expected)) {
            throw new IncorrectHashException(expected, found);
        }
    }

    /**
     * @return data to use for geneating hash
     */
    private Supplier<?>[] getSuppliers() {
        Supplier<?>[] suppliers = {this::getId, this::getUserId, this::getAddress, this::getCart};
        return suppliers;
    }

}
