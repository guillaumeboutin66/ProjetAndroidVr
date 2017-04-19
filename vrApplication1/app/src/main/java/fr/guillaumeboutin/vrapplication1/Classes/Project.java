package fr.guillaumeboutin.vrapplication1.Classes;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by guillaumeboutin on 27/03/2017.
 */

public class Project extends RealmObject {
    private int              id;
    private String           name;
    private RealmList<Picture> pictures;

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

    public RealmList<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(RealmList<Picture> pictures) {
        this.pictures = pictures;
    }

    public void addImages(Picture picture) {
        this.pictures.add(picture);
    }
}
