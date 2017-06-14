package fr.guillaumeboutin.vrapplication1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.util.List;

import fr.guillaumeboutin.vrapplication1.Adapter.ProjectAdapter;
import fr.guillaumeboutin.vrapplication1.Classes.Project;
import fr.guillaumeboutin.vrapplication1.Manager.RealmManager;
import io.realm.Realm;

/**
 * Created by guillaumeboutin on 26/03/2017.
 */

public class ProjectActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {
    private String m_Text = "";
    private Context ctx;
    private Realm realm;
    private RealmManager rm;
    private GridView mGridView;
    private ProjectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ctx = this;

        rm = new RealmManager(ctx);
        realm = rm.getRealm();

        findViewById(R.id.button_project)
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Title");

                        // Set up the input
                        final EditText input = new EditText(ctx);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();
                                realm.beginTransaction();
                                Project prjct = realm.createObject(Project.class);
                                prjct.setId(rm.getNextKey());
                                prjct.setName(m_Text);
                                realm.commitTransaction();
                                mAdapter.setData(rm.getProjects());
                                mAdapter.notifyDataSetChanged();
                                mGridView.setAdapter(mAdapter);
                            }
                        });
                        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load from file "cities.json" first time
        if(mAdapter == null) {
            List<Project> projects = rm.getProjects();

            //This is the GridView adapter
            mAdapter = new ProjectAdapter(this);
            mAdapter.setData(projects);

            //This is the GridView which will display the list of cities
            mGridView = (GridView) findViewById(R.id.projects_list);
            mGridView.setAdapter(mAdapter);
            mGridView.setOnItemClickListener(ProjectActivity.this);
            mAdapter.notifyDataSetChanged();
            mGridView.invalidate();

            mGridView.setOnItemClickListener(

                    new AdapterView.OnItemClickListener(){
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(ProjectActivity.this, DetailsProjectActivity.class);
                            i.putExtra("position", position);
                            startActivity(i);
                        }
                    }
            );

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
