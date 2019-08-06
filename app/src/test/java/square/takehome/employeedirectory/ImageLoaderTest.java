package square.takehome.employeedirectory;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import square.takehome.employeedirectory.network.NetworkApi;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ImageLoaderTest
{
    @Mock private Context mContext;
    @Mock private LruCache<String, Bitmap> mMockMemCache;
    @Mock private DiskImageCache mMockDiskCache;
    @Mock private NetworkApi mMockNetworkApi;

    private ImageLoader mUnderTest;

    @Before
    public void setup() {
        mUnderTest = new ImageLoader(mContext, mMockMemCache, mMockDiskCache, mMockNetworkApi);
    }

    /**
     * Tests that when image is present in memory cache, no calls to disk cache are made and the
     * image in memory cache is what is returned.
     */
    @Test
    public void testGetImageInMemoryCache() {
        String imageUrl = "some key";
        final Bitmap mockImage = mock(Bitmap.class);

        // Image is present in memory cache.
        given(mMockMemCache.get(imageUrl)).willReturn(mockImage);

        //act
        mUnderTest.getImage(imageUrl, new ImageLoader.ImageLoadedCallback()
        {
            @Override
            public void onImageLoaded(Bitmap bitmap)
            {
                Assert.assertEquals(mockImage, bitmap);
            }
        });

        verifyZeroInteractions(mMockDiskCache);
    }

    /**
     * Tests that when image is not in memory cache, call to fetch image from disk cache is made
     * and returned image from disk cache is what is returned.
     */
    @Test
    public void testGetImageNotInMemoryCacheChecksInDiskCache() {
        String imageUrl = "some key";
        final Bitmap mockImage = mock(Bitmap.class);

        // Image is first not present, and is later present after fetched from disk.
        given(mMockMemCache.get(imageUrl)).willReturn(null).willReturn(mockImage);

        // image is present in disk cache.
        given(mMockDiskCache.getImage(imageUrl)).willReturn(mockImage);

        //act
        mUnderTest.getImage(imageUrl, new ImageLoader.ImageLoadedCallback()
        {
            @Override
            public void onImageLoaded(Bitmap bitmap)
            {
                Assert.assertEquals(mockImage, bitmap);
            }
        });

        verify(mMockDiskCache).getImage(imageUrl);
        verify(mMockMemCache).put(imageUrl, mockImage);
    }
}
