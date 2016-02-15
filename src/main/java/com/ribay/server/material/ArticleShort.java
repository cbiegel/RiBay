package com.ribay.server.material;

public class ArticleShort
{

    private String id;
    private String name;
    private String image;
    private double price;
    private int quantity;

    public ArticleShort()
    {
        this(null, null, null, -1.0, -1);
    }

    public ArticleShort(String id, String name, String image, double price, int quantity)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

}
