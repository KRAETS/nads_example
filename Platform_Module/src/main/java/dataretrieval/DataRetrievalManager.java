package dataretrieval;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import interfaces.Manager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import parsing.DataRetrievalOptions;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
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
            server = HttpServer.create(new InetSocketAddress(8000), 0);
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
        return false;
    }
    @Override
    public boolean configure() {
        return false;
    }



    private static class SendData implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            File outputfile = new File("Results");
            synchronized (outputfile){
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile, true)));
                out.append("Hi");
                out.append(t.getRequestBody().toString());
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
                HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

                // Request parameters and other properties.
                List<NameValuePair> params = new LinkedList<NameValuePair>();
                params.add(new BasicNameValuePair("kql", t.getRequestBody().toString()));
                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                //Execute and get the response.
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream instream = entity.getContent();
                    try {
                        // do something useful
                        String forwardedresponse = instream.toString();
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
