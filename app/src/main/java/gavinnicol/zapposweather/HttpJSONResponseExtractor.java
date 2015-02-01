package gavinnicol.zapposweather;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Extracts the HttpResponse stream into a String
 */
public class HttpJSONResponseExtractor extends AsyncTask<HttpResponse, Void, String> {

    public String doInBackground(HttpResponse... response) {
        InputStream jsonStream = null;
        try {
            jsonStream = response[0].getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractStreamToString(jsonStream);
    }

    private String extractStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String nextLine;
        try {
            while ((nextLine = reader.readLine()) != null) {
                nextLine += "\n";
                builder.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}