package pl.pwr.mipo.mipoo;

/**
 * Created by CitrusPanc on 4/6/2015.
 */
public class Product {

    private long id;
    private String name;
    private int complete;
    private long listid;
    private int position;
    private int count;

    public Product( String name, int complete, long listid, int position, int count) {
        this.name = name;
        this.complete = complete;
        this.listid = listid;
        this.position = position;
        this.count = count;

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

    public long getListid() {
        return listid;
    }

    public void setListid(long listid) {
        this.listid = listid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
