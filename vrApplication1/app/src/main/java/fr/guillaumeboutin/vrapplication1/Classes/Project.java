package fr.guillaumeboutin.vrapplication1.Classes;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by guillaumeboutin on 27/03/2017.
 */

public class Project extends RealmObject {
    private int              id;
    private String           name;
    private RealmList<Image> images;

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

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public void addImages(Image image) {
        this.images.add(image);
    }
}
