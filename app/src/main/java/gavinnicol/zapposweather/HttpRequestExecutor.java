package gavinnicol.zapposweather;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

/**
 * Spawns a new thread to execute a HttpRequest
 */
public class HttpRequestExecutor extends AsyncTask<HttpUriRequest, Void, HttpResponse> {

    private AndroidHttpClient client;

    public HttpResponse doInBackground(HttpUriRequest... request) {

        client = AndroidHttpClient.newInstance(System.getProperty("user.agent"));
        HttpResponse response = null;

        try {
            response = client.execute(request[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
