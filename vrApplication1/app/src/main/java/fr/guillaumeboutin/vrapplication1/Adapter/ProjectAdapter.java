package fr.guillaumeboutin.vrapplication1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fr.guillaumeboutin.vrapplication1.Classes.Project;
import fr.guillaumeboutin.vrapplication1.R;

/**
 * Created by guillaumeboutin on 27/03/2017.
 */

public class ProjectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Project> projects = null;

    public ProjectAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public int getCount() {
        if (projects == null) {
            return 0;
        }
        return projects.size();
    }

    @Override
    public Object getItem(int position) {
        if (projects == null || projects.get(position) == null) {
            return null;
        }
        return projects.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.project_listitem, parent, false);
        }

        Project project = projects.get(position);

        if (project != null) {
            ((TextView) currentView.findViewById(R.id.nameProject)).setText(project.getName());
        }

        return currentView;
    }
}
