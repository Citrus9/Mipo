package pl.pwr.mipo.mipoo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by CitrusPanc on 5/16/2015.
 */
public class ScannedProduct implements Serializable {

    private String productName;
    private String storeName;
    private float price;
    private String priceString;
    private long barcode;
    private Date date;

    public ScannedProduct(){

    }

    public ScannedProduct(String productName, String storeName, float price, String priceString, long barcode, Date date) {
        this.productName = productName;
        this.storeName = storeName;
        this.price = price;
        this.priceString = priceString;
        this.barcode = barcode;
        this.date = date;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPriceString() {
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
