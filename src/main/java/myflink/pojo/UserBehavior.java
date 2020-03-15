package myflink.pojo;

public class UserBehavior {
    //用户ID
    public long userId;
    //商品ID
    public long itemId;
    //商品类目ID
    public  int categoryId;
    //用户行为，包括{"pv","buy","cart", "fav"}
    public String behavior;
    //行为发生的时间戳，单位秒
    public String ts;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getTimestamp() {
        return ts;
    }

    public void setTimestamp(String ts) {
        this.ts = ts;
    }
}
