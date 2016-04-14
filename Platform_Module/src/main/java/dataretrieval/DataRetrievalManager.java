package dataretrieval;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import interfaces.Manager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import parsing.DataRetrievalOptions;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pedro on 3/17/16.
 */
public class DataRetrievalManager extends Manager {
    private DataRetrievalOptions datRetOpts;
    private HttpServer server = null;
    public DataRetrievalManager(DataRetrievalOptions dataRetrievalOptions, Logger logger) {
        this.datRetOpts = dataRetrievalOptions;
        this.setLogger(logger);
        this.configure();
    }
    @Override
    public boolean start() {

        try {
            server = HttpServer.create(new InetSocketAddress(8002), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Set up the receiving point for getting a request for data
        server.createContext("/getdata", new GetData());
        server.createContext("/senddata", new SendData());
        server.setExecutor(null); // creates a default executor
        server.start();
        return true;
    }
    @Override
    public boolean stop() {
        try {
            server.stop(0);
        }
        catch (Exception e){
            this.getLogger().log(Level.SEVERE,"Problem shutting down server:"+e.toString());
            return false;
        }
        return true;
    }
    @Override
    public boolean configure() {
        return true;
    }



    private static class SendData implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            File outputfile = new File("Results.log");
            synchronized (outputfile){
                //Initialize the file printer
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile, true)));
                //Append the body
                BufferedReader input = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                String bodyargument = "";
                while (true){
                    String current = input.readLine();
                    if(current == null){
                        break;
                    }
                    bodyargument += current;
                }
                out.append("Anomaly detected: {"+bodyargument+"}\n");
                out.flush();
                out.close();
            }
            String response = "Success";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    private static class GetData implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            try {
                //Forward the request to the appropriate location

                HttpClient httpclient = HttpClients.createDefault();
                BufferedReader input = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                String bodyargument = "";
                while (true){
                    String current = input.readLine();
                    if(current == null){
                        break;
                    }
                    bodyargument += current;
                }

                bodyargument = "http://localhost:9200/_kql?limit=10000&kql="+ URLEncoder.encode( bodyargument, "UTF-8");
//                bodyargument = new String(bodyargument.getBytes("UTF-8"), "ISO-8859-1");
                URI address = new URI(bodyargument);
                HttpGet httppost = new HttpGet(address);

                                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream instream = entity.getContent();
                    try {
                        // do something useful
                        input = new BufferedReader(new InputStreamReader(instream));

                        String forwardedresponse = "";
                        while (true){
                            String current = input.readLine();
                            if(current == null){
                                break;
                            }
                            forwardedresponse += current;
                        }
                        t.sendResponseHeaders(200, forwardedresponse.length());
                        OutputStream os = t.getResponseBody();
                        os.write(forwardedresponse.getBytes());
                        os.close();
                    } finally {
                        instream.close();
                    }
                } else {
                    //Problem with the request, send error message
                    String forwardedresponse = "There was a problem forwarding the response...";
                    t.sendResponseHeaders(500, forwardedresponse.length());
                    OutputStream os = t.getResponseBody();
                    os.write(forwardedresponse.getBytes());
                    os.close();
                }
            }
            catch (Exception e){
                String forwardedresponse = e.toString();
                t.sendResponseHeaders(500, forwardedresponse.length());
                OutputStream os = t.getResponseBody();
                os.write(forwardedresponse.getBytes());
                os.close();
            }

        }
    }
}
