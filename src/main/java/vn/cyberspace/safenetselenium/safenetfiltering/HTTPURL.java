/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium.safenetfiltering;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.text.Document;

/**
 *
 * @author thuylh
 */
public class HTTPURL {

    private final String USER_AGENT = "Mozilla/5.0";
    private PrintWriter writer;

    public HTTPURL(PrintWriter writer) {
        this.writer = writer;
    }

    public class Response {

        int resCode;
        String location;
    }

    public Response sendGet(String url) throws IOException {
        URL obj = new URL(url);
        Response res = new Response();

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        //con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("User-Agent", USER_AGENT);

        con.setInstanceFollowRedirects(false);
        con.connect();

        int responseCode = con.getResponseCode();
        String location = con.getHeaderField("Location");
        res.resCode = responseCode;
        res.location = location;
        //System.out.println("\nSending GET request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        writer.println("\nSending GET request to URL : " + url);
        writer.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        con.disconnect();
        return res;

    }

    public int SendPost(String url, String postData) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // set method http
        con.setRequestMethod("POST");

        //add request header
        //con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");

        //Send post request
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(postData);
        wr.close();

        int responseCode = con.getResponseCode();
        // System.out.println("\nSending POST request to URL : " + url);
        // System.out.println("Post parameters :" + postData);
        // System.out.println("Response Code : " + responseCode);

        writer.println("\nSending POST request to URL : " + url);
        writer.println("Post parameters :" + postData);
        writer.println("Response Code : " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        }

        con.disconnect();

        return responseCode;
    }

    public String getContent(String url) throws MalformedURLException, IOException {
        StringBuffer buffer = new StringBuffer();

        URL obj = new URL(url);

        InputStream inputstr = obj.openStream();
        int ptr = 0;
        while ((ptr = inputstr.read()) != -1) {
            buffer.append((char) ptr);
        }

        return buffer.toString();
    }

    public String getUnicodeContent(String urlString) throws MalformedURLException, IOException {
        BufferedReader reader = null;
        String result;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            reader.close();
            return buffer.toString();

        } finally {
            if (reader != null) {
                // System.out.println("...Transferation has finished.");
                reader.close();
            } else {
                //System.out.println("...Reading URL failed");
                return null;
            }
        }
    }

    public String getContentBySocket(String domain) throws IOException, InterruptedException, ExecutionException {
        BufferedReader reader = null;
        Socket connection = null;
        
        try {
            connection = new Socket(domain, 80);

            OutputStream out = connection.getOutputStream();
            InputStream in = connection.getInputStream();

            Callable<Void> requestSender = new Callable<Void>() {
                @Override
                public Void call()
                        throws IOException {
                    String request = "GET / HTTP/1.1\r\nHost: " + domain + "\r\n\r\n";
                    out.write(request.getBytes(StandardCharsets.US_ASCII));
                    return null;
                }
            };
            ExecutorService background = Executors.newSingleThreadExecutor();
            Future<?> request = background.submit(requestSender);
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            reader.close();

            request.get();
            background.shutdown();
            return buffer.toString();
        } catch (UnknownHostException e) {
            this.writer.println(e.toString());
            return null;
        } catch (ExecutionException | InterruptedException | IOException e) {
            this.writer.println(e.toString());
            return null;
        } finally {
            try {
                if(connection!= null)
                    connection.close();

            } catch (IOException e) {
                this.writer.println(e.toString());
                return null;
            }
        }

    }
}
