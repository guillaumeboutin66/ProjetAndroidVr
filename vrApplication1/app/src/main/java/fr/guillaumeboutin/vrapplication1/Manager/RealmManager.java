package fr.guillaumeboutin.vrapplication1.Manager;

import android.content.Context;
import android.system.ErrnoException;

import fr.guillaumeboutin.vrapplication1.Classes.Picture;
import fr.guillaumeboutin.vrapplication1.Classes.Project;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by guillaumeboutin on 27/03/2017.
 */

public class RealmManager {
    Realm realm;


    public RealmManager(Context ctx){
        Realm.init(ctx);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        // Use the config
        realm = Realm.getInstance(config);
    }

    public Realm getRealm(){
        return realm;
    }

    public int getNextKey()
    {
        try {
            return realm.where(Project.class).max("id").intValue() + 1;
        } catch (Exception e)
        {
            return 0;
        }
    }

    public int getNextKeyPicture()
    {
        try {
            return realm.where(Picture.class).max("id").intValue() + 1;
        } catch (Exception e)
        {
            return 0;
        }
    }
    public RealmResults<Project> getProjects(){
        return realm.where(Project.class).findAll();
    }

    public RealmResults<Picture> getPicturesByProject(int idProject){
        return realm.where(Picture.class).equalTo("idProject", idProject).findAll();
    }

}
