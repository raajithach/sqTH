package square.takehome.employeedirectory;

import android.content.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DiskImageCacheTest
{
    @Mock private Context mContext;
    private DiskImageCache mUnderTest;

    @Before
    public void setup() {
        given(mContext.getApplicationContext()).willReturn(mContext);
        given(mContext.getCacheDir()).willReturn(mock(File.class));
        mUnderTest = new DiskImageCache(mContext);
    }

    /**
     * Tests that getImage returns null if file does not exist.
     */
    @Test
    public void testNullForGetImage() {
        String imageKey = "some key";
        Assert.assertNull(mUnderTest.getImage(imageKey));
    }
}
