package vtc.blake.eastboundanddow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataPoint[] dp = generateArray(10);
        fillGraph(dp);
    }
    public void fillGraph(DataPoint[] dpArray){

        GraphView gv = (GraphView) findViewById(R.id.main_graph);

        //dummy values
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(int j = 0; j < dpArray.length; j++)if(dpArray[j]!=null)series.appendData(dpArray[j],true,1000,true);

        gv.getViewport().setScalable(true);
        gv.getViewport().setMaxX(dpArray[dpArray.length-1].getX());
        gv.getViewport().setMinX(dpArray[0].getX());
        gv.addSeries(series);

        }
        DataPoint[] generateArray(int size){
            ArrayList<Double> timelist = new ArrayList<>();
            ArrayList<Double> pricelist = new ArrayList<>();
            DataPoint[] dp = new DataPoint[size]; int i = 0;
            timelist.add(1000d);
            timelist.add(1200d);
            timelist.add(1301d);
            timelist.add(1401d);
            timelist.add(1501d);
            timelist.add(1601d);
            timelist.add(1701d);
            timelist.add(1801d);
            timelist.add(1901d);
            timelist.add(2001d);
            pricelist.add(152.2d);
            pricelist.add(155.3d);
            pricelist.add(122.1d);
            pricelist.add(134.2d);
            pricelist.add(152.2d);
            pricelist.add(155.3d);
            pricelist.add(122.1d);
            pricelist.add(134.2d);
            pricelist.add(152.2d);
            pricelist.add(155.3d);
            while(!pricelist.isEmpty() && !timelist.isEmpty()){
                dp[i] = new DataPoint((double)timelist.get(0),(double)pricelist.get(0));
                timelist.remove(0); pricelist.remove(0);
                i++;
            }
        return dp;
        }


}
