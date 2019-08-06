package square.takehome.employeedirectory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiskImageCache
{
    private static final String TAG = DiskImageCache.class.getSimpleName();
    private static DiskImageCache sDiskImageCache;
    private static Context sContext;
    private static final String DISK_CACHE_SUBDIR = "smallImages";
    private static File sCacheDirectory;

    public static DiskImageCache getInstance(Context context) {
        if (sDiskImageCache == null) {
            sDiskImageCache = new DiskImageCache(context);
        }
        return sDiskImageCache;
    }

    @VisibleForTesting
    DiskImageCache(Context context) {
        sContext = context.getApplicationContext();
        sCacheDirectory = new File(sContext.getCacheDir().getPath(), DISK_CACHE_SUBDIR);
    }

    public void writeToDisk(@NonNull final String imageUrl, Bitmap bitmap) {
        new WriteToDiskTask(imageUrl, bitmap).execute();
    }

    public Bitmap getImage(String key){
        String updatedKey = key.replace('/','@');
        File file = new File(sCacheDirectory + File.separator + updatedKey);
        if (file.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            final int width = sContext.getResources().getDimensionPixelSize(R.dimen.small_image_width);
            final int height = sContext.getResources().getDimensionPixelSize(R.dimen.small_image_height);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height,true);
            return bitmap;
        }
        else {
            return null;
        }
    }

    private static class WriteToDiskTask extends AsyncTask<Void, Void, Void> {
        private String mImageUrl;
        private Bitmap mBitmap;

        WriteToDiskTask(@NonNull String imageUrl, Bitmap bitmap) {
            mImageUrl = imageUrl;
            mBitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if (!sCacheDirectory.exists()) {
                sCacheDirectory.mkdir();
            }

            String updatedImageUrl = mImageUrl.replace('/','@');
            try {
                File file = new File(sCacheDirectory + File.separator + updatedImageUrl);

                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream outputStream = null;

                try {
                    outputStream = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 85, outputStream);
                    outputStream.flush();
                } catch (IOException e) {
                    Log.e(TAG, "exception writing to disk", e);

                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "exception writing to disk", e);
            }
            return null;
        }
    }
}
