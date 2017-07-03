package fr.guillaumeboutin.vrapplication1.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.List;

import fr.guillaumeboutin.vrapplication1.Classes.Picture;
import fr.guillaumeboutin.vrapplication1.R;

/**
 * Created by guillaumeboutin on 01/04/2017.
 */

public class ImageProjectAdapter  extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Picture> pictures = null;

    public ImageProjectAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        if (pictures == null) {
            return 0;
        }
        return pictures.size();
    }

    @Override
    public Object getItem(int position) {
        if (pictures == null || pictures.get(position) == null) {
            return null;
        }
        return pictures.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.image_listitem, parent, false);
        }

        Picture picture = pictures.get(position);

        if (picture != null) {
            //int id = currentView.getResources().getIdentifier("fr.guillaumeboutin.vrapplication1:drawable/" + picture.getName(), null, null);
            VrPanoramaView vr = ((VrPanoramaView) currentView.findViewById(R.id.imageButton));
            vr.loadImageFromBitmap(BitmapFactory.decodeFile(picture.getUrl()), null);

            vr.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }

        return currentView;
    }
}
