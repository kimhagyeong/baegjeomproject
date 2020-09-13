package com.example.testtesttest;

import java.io.File;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class SingleMediaScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;
    private String path;

    public SingleMediaScanner(Context context, String s) {
        path = s;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(path, null);
        Log.e("scanning","스캔중");
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
        Log.e("scanning","스캔끝");
    }

}
