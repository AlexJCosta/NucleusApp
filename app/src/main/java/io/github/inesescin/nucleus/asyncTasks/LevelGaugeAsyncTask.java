package io.github.inesescin.nucleus.asyncTasks;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;

import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.util.Constants;
import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by danielmaida on 01/03/16.
 */
public class LevelGaugeAsyncTask extends AsyncTask<String, Void, String> {

    private CustomGauge oilLevelGauge;
    private TextView oilLevelText;

    public LevelGaugeAsyncTask(CustomGauge customGauge, TextView textView)
    {
        this.oilLevelGauge = customGauge;
        this.oilLevelText = textView;
    }

    @Override
    protected String doInBackground(String... params) {

        FiwareConnection fiwareConnection = new FiwareConnection();
        String entityId = params[0];

        String oilLevel = "";

        try
        {
            oilLevel = fiwareConnection.getEntityAttributeValue("level", entityId, Constants.FIWARE_ADDRESS, "value");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return oilLevel;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null && !result.equals(""))
        {
            oilLevelText.setText(result);
            oilLevelGauge.setValue(Integer.parseInt(result));
        }
    }
}
