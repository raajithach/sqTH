package square.takehome.employeedirectory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.LruCache;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import square.takehome.employeedirectory.network.NetworkApi;

import java.util.ArrayList;
import java.util.List;

public class ImageLoader
{
    private static final String TAG = ImageLoader.class.getSimpleName();
    private static ImageLoader sImageLoader;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskImageCache mDiskImageCache;
    private NetworkApi mNetworkApi;
    private List<ImageLoadedCallback> mListOfPendingCallbacks;

    public static ImageLoader getInstance(Context context) {
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(context);
        }
        return sImageLoader;
    }

    public interface ImageLoadedCallback {
        void onImageLoaded(Bitmap bitmap);
    }

    private ImageLoader(Context context) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        Log.i(TAG, "mem cache size = " + cacheSize);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        mDiskImageCache = DiskImageCache.getInstance(context.getApplicationContext());
        mNetworkApi = new NetworkApi();
        mListOfPendingCallbacks = new ArrayList<>();
    }

    @VisibleForTesting
    ImageLoader(Context context, LruCache<String, Bitmap> memoryCache, DiskImageCache diskImageCache, NetworkApi networkApi) {
        mMemoryCache = memoryCache;
        mDiskImageCache = diskImageCache;
        mNetworkApi = networkApi;
    }

    private void fetchImage(final String imageUrl, final ImageLoadedCallback callback) {
        final Call<ResponseBody> responseBodyCall = mNetworkApi.getApi().downloadFileWithUrl(imageUrl);
        responseBodyCall.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                new FetchImageTask(imageUrl, mDiskImageCache, response, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e(TAG, "error downloading image", t);
            }
        });

    }

    public void getImage(final String key, final ImageLoadedCallback callback) {
        if (mMemoryCache.get(key) == null) {
            Bitmap image = mDiskImageCache.getImage(key);
            if(image == null) {
                fetchImage(key, new ImageLoadedCallback()
                {
                    @Override
                    public void onImageLoaded(Bitmap bitmap)
                    {
                        if (mListOfPendingCallbacks.contains(callback)) {
                            callback.onImageLoaded(bitmap);
                        }
                    }
                });
                mListOfPendingCallbacks.add(callback);
            } else {
                mMemoryCache.put(key, image);
                callback.onImageLoaded(image);
            }
        } else {
            callback.onImageLoaded(mMemoryCache.get(key));
        }
    }

    public void cancelPendingCallback(ImageLoadedCallback callback) {
        mListOfPendingCallbacks.remove(callback);
    }

    private static class FetchImageTask extends AsyncTask<Void, Void, Void> {
        private String mImageUrl;
        private DiskImageCache mDiskImageCache;
        private Response<ResponseBody> mResponse;
        private ImageLoadedCallback mImageLoadedCallback;

        FetchImageTask(String imageUrl, DiskImageCache diskImageCache, Response<ResponseBody> response, ImageLoadedCallback callback) {
            mImageUrl = imageUrl;
            mDiskImageCache = diskImageCache;
            mResponse = response;
            mImageLoadedCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            final Bitmap img = BitmapFactory.decodeStream(mResponse.body().byteStream());
            mImageLoadedCallback.onImageLoaded(img);
            mDiskImageCache.writeToDisk(mImageUrl, img);
            return null;
        }
    }
}
