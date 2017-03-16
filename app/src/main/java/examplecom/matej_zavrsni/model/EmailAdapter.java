package examplecom.matej_zavrsni.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import examplecom.matej_zavrsni.R;

/**
 * Created by goran on 11/1/2016.
 */
public class EmailAdapter   extends ArrayAdapter<EmailModel> {

    public EmailAdapter(Context context, List<EmailModel> emailModels) {

        super(context, 0, emailModels);

    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        EmailModel emailModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.email_content, parent, false);

        }

        // Lookup view for data population

        TextView tvFrom = (TextView) convertView.findViewById(R.id.tvFrom);

        TextView tvSubject = (TextView) convertView.findViewById(R.id.tvSubject);

        // Populate the data into the template view using the data object

        tvFrom.setText("From: " + emailModel.getFrom());

        tvSubject.setText("Subject: " + emailModel.getSubject());

        // Return the completed view to render on screen

        return convertView;

    }

}
