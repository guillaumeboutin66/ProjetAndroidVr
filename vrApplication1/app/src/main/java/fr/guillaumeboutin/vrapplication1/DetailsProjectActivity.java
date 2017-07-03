package fr.guillaumeboutin.vrapplication1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import fr.guillaumeboutin.vrapplication1.Adapter.ImageProjectAdapter;
import fr.guillaumeboutin.vrapplication1.Adapter.ProjectAdapter;
import fr.guillaumeboutin.vrapplication1.Classes.Picture;
import fr.guillaumeboutin.vrapplication1.Classes.Project;
import fr.guillaumeboutin.vrapplication1.Manager.RealmManager;

/**
 * Created by guillaumeboutin on 26/03/2017.
 */

public class DetailsProjectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private RealmManager rm;
    private Context ctx;
    private GridView mGridView;
    private ImageProjectAdapter mAdapter;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_project);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        ctx = this;
        rm = new RealmManager(ctx);
        TextView titleProject = (TextView) findViewById(R.id.titreProject);

        if(b!=null)
        {
            int position =(int) b.get("position");
            project = rm.getProjects().get(position);
            titleProject.setText(project.getName());
        }

        int MyVersion= Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        findViewById(R.id.button_select_picture)
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {

                        loadImage();
                    }
                });

        findViewById(R.id.button_go_vr)
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {

                        // in onCreate or any event where your want the user to
                        // select a file
                        Intent intent = new Intent(DetailsProjectActivity.this, VrActivity.class);
                        startActivity(intent);

                    }
                });
    }


    public void loadImage(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "U haven't granted any permission, App will not work", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "Image loaded", Toast.LENGTH_SHORT).show();
                String path=getPath(data.getData());
                Bitmap bm= BitmapFactory.decodeFile(path);
                if(bm==null) {
                    Toast.makeText(this, "Unable to decode stream", Toast.LENGTH_SHORT).show();
                }else {
                    Log.e("Path", "Path picture : "+path);

                    rm.getRealm().beginTransaction();
                    Picture pict =  rm.getRealm().createObject(Picture.class);;
                    pict.setName("Image test");
                    pict.setId(rm.getNextKeyPicture());
                    pict.setUrl(path);
                    pict.setIdProject(project.getId());
                    rm.getRealm().commitTransaction();

                    List<Picture> pictures2 = rm.getRealm().where(Picture.class).findAll();
                    Log.e("Path", "count all picture : "+rm.getRealm().where(Picture.class).findAll().size());


                    Toast.makeText(this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    mGridView.setAdapter(mAdapter);
                    //myVr.loadImageFromBitmap(bm, null);
                }
            }
        }
    }
    private String getPath(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Load from file "cities.json" first time
        if(mAdapter == null && project!=null) {
            List<Picture> pictures = rm.getPicturesByProject(project.getId());


            Log.e("Number", "Count picture : "+pictures.size());

            //This is the GridView adapter
            mAdapter = new ImageProjectAdapter(this);
            mAdapter.setData(pictures);

            //This is the GridView which will display the list of cities
            mGridView = (GridView) findViewById(R.id.projects_details_list);

            mGridView.setAdapter(mAdapter);
            mGridView.setOnItemClickListener(DetailsProjectActivity.this);
            mAdapter.notifyDataSetChanged();
            mGridView.invalidate();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
