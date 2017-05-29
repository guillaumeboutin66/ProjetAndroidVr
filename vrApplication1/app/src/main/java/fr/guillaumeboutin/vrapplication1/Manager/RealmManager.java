package fr.guillaumeboutin.vrapplication1.Manager;

import android.content.Context;

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
        } catch (ArrayIndexOutOfBoundsException e)
        {
            return 0;
        }
    }

    public RealmResults<Project> getProjects(){
        return realm.where(Project.class).findAll();
    }

}
