package mm.pndaza.maghadeva.model;

public class SubCategory {
    int id;
    String name;
    int catId;

    public SubCategory(int id, String name, int catId) {
        this.id = id;
        this.name = name;
        this.catId = catId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }
}
