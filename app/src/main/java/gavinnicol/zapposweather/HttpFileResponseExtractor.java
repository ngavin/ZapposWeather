package gavinnicol.zapposweather;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts the HttpResponse stream into a String
 */
public class HttpFileResponseExtractor extends AsyncTask<HttpResponse, Void, Drawable> {

    public Drawable doInBackground(HttpResponse... response) {
        InputStream fileStream = null;
        try {
            fileStream = response[0].getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable file = Drawable.createFromStream(fileStream, "file");
        return file;
    }
}