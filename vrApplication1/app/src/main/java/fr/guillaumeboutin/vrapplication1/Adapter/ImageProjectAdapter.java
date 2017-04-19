package fr.guillaumeboutin.vrapplication1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import java.util.List;

import fr.guillaumeboutin.vrapplication1.Classes.Image;
import fr.guillaumeboutin.vrapplication1.R;

/**
 * Created by guillaumeboutin on 01/04/2017.
 */

public class ImageProjectAdapter  extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Image> images = null;

    public ImageProjectAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Image> images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        if (images == null) {
            return 0;
        }
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        if (images == null || images.get(position) == null) {
            return null;
        }
        return images.get(position);
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

        Image image = images.get(position);

        if (image != null) {
            int id = currentView.getResources().getIdentifier("fr.guillaumeboutin.vrapplication1:drawable/" + image.getName(), null, null);
            ((ImageButton) currentView.findViewById(R.id.imageButton)).setImageDrawable(currentView.getResources().getDrawable(id));
        }

        return currentView;
    }
}
