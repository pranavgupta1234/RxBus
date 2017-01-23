package pranav.apps.amazing.rxbus.model;

/**
 * Created by Sidiq on 26/01/2016.
 */
public class PostItem {
    private String name;
    private int totalView;

    public PostItem(String name,int totalView){
        this.name = name;
        this.totalView = totalView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }
}
