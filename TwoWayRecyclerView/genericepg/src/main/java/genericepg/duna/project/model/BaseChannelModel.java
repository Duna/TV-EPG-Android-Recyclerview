package genericepg.duna.project.model;

import android.net.Uri;

/**
 * Created by Marius Duna on 9/30/2016.
 */

//Extend your channel model from this base class
public class BaseChannelModel {
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
