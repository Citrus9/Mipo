package pl.pwr.mipo.mipoo;

/**
 * Created by CitrusPanc on 4/6/2015.
 */
public class Product {

    private long id;
    private String name;
    private int complete;
    private int position;

    public Product(long id, String name, int complete, int position) {
        this.id = id;
        this.name = name;
        this.complete = complete;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
