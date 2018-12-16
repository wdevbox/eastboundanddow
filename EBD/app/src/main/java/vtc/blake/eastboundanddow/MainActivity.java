package vtc.blake.eastboundanddow;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String data;
    OkHttpClient client = new OkHttpClient();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //fillGraph(dp);
        final String url = "https://api.iextrading.com/1.0/stock/DIA/batch?types=chart&range=1m&last=1";
                //buildURL("DIA", "m");

            new Thread(){
                @Override
                public void run(){
                    try{
                        //data = getData(url);
                        URL url = new URL("https://api.iextrading.com/1.0/stock/DIA/batch?types=chart&range=1m&last=1");
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        try{
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            data = readStream(in);
                        }
                        finally{
                            urlConnection.disconnect();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }.start();
            new Thread(){
                @Override
                public void run(){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    data = sanitizeData(data);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){

                            ((TextView) findViewById(R.id.text_out)).setText(data);
                            try {
                                fillGraph(
                                        fillDPArray(parsePrices(data),parseDates(data))
                                );
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }.start();
    }

    private String sanitizeData(String data) {
        String sanitized_string;
        sanitized_string = data.replace("{","");
        sanitized_string = sanitized_string.replace("[","");
        sanitized_string = sanitized_string.replace("]","");
        sanitized_string = sanitized_string.replace("}","");
        sanitized_string = sanitized_string.replace("\"", "");
        sanitized_string = sanitized_string.replace("chart:", "");



        return sanitized_string;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    public String buildURL(String index_name, String range){//range can be either d or m
        String prefix = "https://api.iextrading.com/1.0";
        String suffix = "/stock/" + index_name + "/batch?";
        String params = "types=chart&range=1" + range + "&last=1";
        String url = prefix + suffix + params;
        return url;
    }
    public ArrayList<Double> parsePrices(String data){
        if(data==null)return new ArrayList<Double>();
        ArrayList<Double> prices = new ArrayList<>();
        for(String line: data.split(",")){
            if(line.contains("close")){
                prices.add(Double.valueOf(line.split(":")[1]));
            }
        }
        return prices;
    }
    public ArrayList<String> parseDates(String data){
        ArrayList<String> dates = new ArrayList<>();
        if(data==null){return dates;}
        for(String line: data.split(",")){
            if(line.contains("date")){
                dates.add(line.split(":")[1]);
            }
        }
        return dates;
    }
    public String getData(String url) throws IOException{

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        Log.d("Http response code:", Integer.toString(response.code()));
        return response.toString();
    }
    public void fillGraph(DataPoint[] dpArray){

        GraphView gv = (GraphView) findViewById(R.id.main_graph);

        //dummy values
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(int j = 0; j < dpArray.length; j++)if(dpArray[j]!=null)series.appendData(dpArray[j],true,1000,true);

        gv.addSeries(series);


        gv.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
        gv.getGridLabelRenderer().setNumHorizontalLabels(3);


        gv.getViewport().setScalable(true);
        gv.setTitle("DIA Stock");

        //gv.getViewport().setMaxX(dpArray[dpArray.length-1].getX());
        //gv.getViewport().setMinX(dpArray[0].getX());
        Log.d("X value for 0 is:", Double.toString(dpArray[0].getX()));
        gv.getViewport().setXAxisBoundsManual(true);
        //gv.getGridLabelRenderer().setHumanRounding(false);
        }



        DataPoint[] fillDPArray(ArrayList<Double> prices, ArrayList<String> dates) throws ParseException {
        DataPoint[] dp = new DataPoint[prices.size()];
        ArrayList<Long> d2 = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        for(int j = 0; j < dates.size(); j++){
            d2.add(sdf.parse(dates.get(j)).getTime());
        }
        Calendar t = Calendar.getInstance();
        t.add(Calendar.MONTH, -1);
        long base_time = t.getTimeInMillis();
        Log.d("Base time in MS:",Long.toString(base_time));
        Log.d("parsed base time in MS:",dates.get(1));
        for(int i = 0; i < prices.size(); i++){
            dp[i] = new DataPoint(d2.get(i),prices.get(i));
        }
        return dp;
        }


}
