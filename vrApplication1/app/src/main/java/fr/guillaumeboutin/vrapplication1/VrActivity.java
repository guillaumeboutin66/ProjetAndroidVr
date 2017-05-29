package fr.guillaumeboutin.vrapplication1;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class VrActivity extends AppCompatActivity {
    Context ctx;
    String namePrjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_previewvr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Title");

                final EditText input = new EditText(ctx);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        namePrjectText = input.getText().toString();
                        Log.e("", ""+namePrjectText);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        Bitmap pano = BitmapFactory.decodeResource(this.getResources(), R.drawable.pano2);
        pano = overlayCursor(pano);

        ImageView test = (ImageView) findViewById(R.id.imagetest);
        test.setImageBitmap(pano);

        VrPanoramaView vrPanoramaView = (VrPanoramaView) findViewById(R.id.myVRImage);
        vrPanoramaView.loadImageFromBitmap(pano, null);
        vrPanoramaView.resumeRendering();
    }

    private Bitmap overlayCursor(Bitmap bmp1) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Log.e("iciici ", ""+bmp1.getWidth());
        Bitmap cursor = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.pano2));

        //Bitmap result = Bitmap.createBitmap(1000, 1000, bmp1.getConfig());
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmp1);
        canvas.drawBitmap(cursor, 10, 10, null);
        return bmOverlay;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
