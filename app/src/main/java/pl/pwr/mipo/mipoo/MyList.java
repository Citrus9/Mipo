package pl.pwr.mipo.mipoo;

/**
 * Created by CitrusPanc on 6/7/2015.
 */
public class MyList {

    private long id;
    private String name;
    private int count;

    public MyList(long id, String name, int count) {
        this.id = id;
        this.name = name;
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

    public int getCount() { return count;}

    public void setCount( int count ) { this.count = count; }
}
