package io.github.inesescin.nucleus.asyncTasks;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import io.github.inesescin.nucleus.MainActivity;
import io.github.inesescin.nucleus.NucleusMapActivity;
import io.github.inesescin.nucleus.R;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.models.Nucleus;

/**
 * Created by danielmaida on 03/03/16.
 */
public class MapMarkingAsyncTask extends AsyncTask<GoogleMap, Void, ArrayList<Nucleus>> {

    private String siteAddress;
    private GoogleMap map;

    public MapMarkingAsyncTask(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    @Override
    protected ArrayList<Nucleus> doInBackground(GoogleMap... params) {

        map = params[0];
        FiwareConnection fiwareConnection = new FiwareConnection();
        ArrayList<Nucleus> ecopoints = new ArrayList<>();
        try {
            String stringResponse = fiwareConnection.getEntityByType(siteAddress, "Nucleus");
            ecopoints = parseJsonToNucleusArray(ecopoints, stringResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NucleusMapActivity.ecopoints = ecopoints;
        return ecopoints;
    }

    private ArrayList<Nucleus> parseJsonToNucleusArray(ArrayList<Nucleus> ecopoints, String stringResponse) throws JSONException {
        JSONObject response = new JSONObject(stringResponse);
        JSONArray contextResponse = response.getJSONArray("contextResponses");
        for (int i = 0; i < contextResponse.length(); i++) {
            Nucleus nucleus = new Nucleus();
            JSONObject currentEntityResponse = contextResponse.getJSONObject(i);
            JSONObject contextElement = currentEntityResponse.getJSONObject("contextElement");
            nucleus.setId(contextElement.getString("id"));
            JSONArray attributes = contextElement.getJSONArray("attributes");
            nucleus.setCoordinates(attributes.getJSONObject(0).getString("value"));
            nucleus.setValue(Double.parseDouble(attributes.getJSONObject(1).getString("value")));
            ecopoints.add(nucleus);
        }
        return ecopoints;
    }

    @Override
    protected void onPostExecute(ArrayList<Nucleus> ecopoints) {
        super.onPostExecute(ecopoints);

        for (int i = 0; i < ecopoints.size(); i++) {
            double level = ecopoints.get(i).getValue();
            if (level > 70) {
                map.addMarker(new MarkerOptions().title(ecopoints.get(i).getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_red_48dp)).position(new LatLng(ecopoints.get(i).getLatitude(), ecopoints.get(i).getLongitude())));
            } else if (level >= 50) {
                map.addMarker(new MarkerOptions().title(ecopoints.get(i).getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_yellow_48dp)).position(new LatLng(ecopoints.get(i).getLatitude(), ecopoints.get(i).getLongitude())));
            } else {
                map.addMarker(new MarkerOptions().title(ecopoints.get(i).getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_green_48dp)).position(new LatLng(ecopoints.get(i).getLatitude(), ecopoints.get(i).getLongitude())));
            }
        }

    }
}
