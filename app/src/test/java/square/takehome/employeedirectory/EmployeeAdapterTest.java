package square.takehome.employeedirectory;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import square.takehome.employeedirectory.model.Employee;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeAdapterTest
{
    @Mock ImageLoader mImageLoader;
    private EmployeeAdapter mUnderTest;
    private Employee mEmployee = new Employee("uuid1", "mock emp", "123456789",
            "some@address.com", "sample", "url_small", "url_large",
            "team", Employee.EmployeeType.FULL_TIME);

    @Captor
    private ArgumentCaptor<ImageLoader.ImageLoadedCallback> mImageLoadedCallbackArgumentCaptor;

    @Before
    public void setup() {
        mUnderTest = new EmployeeAdapter(mImageLoader);
    }

    @Test
    public void testEmptyAdapter() {
        Assert.assertEquals(mUnderTest.getItemCount(), 0);
    }

    /**
     * Tests that bind on adapter calls bind on viewholder.
     */
    @Test
    public void testBind() {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(mEmployee);

        EmployeeAdapter.EmployeeViewHolder employeeViewHolder = mock(EmployeeAdapter.EmployeeViewHolder.class);

        mUnderTest = new EmployeeAdapter(mImageLoader, employeeList);
        mUnderTest.onBindViewHolder(employeeViewHolder, 0);
        verify(employeeViewHolder).bind(any(Employee.class));
    }

    /**
     * Tests that when view is recycled, it is passed on to the viewholder.
     */
    @Test
    public void testViewRecycled() {
        EmployeeAdapter.EmployeeViewHolder employeeViewHolder = mock(EmployeeAdapter.EmployeeViewHolder.class);
        mUnderTest.onViewRecycled(employeeViewHolder);

        verify(employeeViewHolder).unbind();
    }

    /**
     * Tests that on bind of view holder, fields are set on views.
     */
    @Test
    public void testBindViewHolder(){
        View itemView = mock(View.class);
        ImageView mockImageView = mock(ImageView.class);

        TextView mockNameView = mock(TextView.class);

        TextView mockTeamView = mock(TextView.class);

        given(itemView.findViewById(R.id.imageView)).willReturn(mockImageView);
        given(itemView.findViewById(R.id.name)).willReturn(mockNameView);
        given(itemView.findViewById(R.id.team)).willReturn(mockTeamView);

        EmployeeAdapter.EmployeeViewHolder employeeViewHolder = mUnderTest.new EmployeeViewHolder(itemView);

        employeeViewHolder.bind(mEmployee);

        then(mockNameView).should().setText(eq(mEmployee.getFull_name()));
        then(mockTeamView).should().setText(eq(mEmployee.getTeam()));
        then(mImageLoader).should().getImage(eq(mEmployee.getPhoto_url_small()), any(ImageLoader.ImageLoadedCallback.class));
    }

    /**
     * Test that unbind after bind cancels previous image load callback.
     */
    @Test
    public void testUnBind(){
        View itemView = mock(View.class);
        ImageView mockImageView = mock(ImageView.class);
        TextView mockNameView = mock(TextView.class);
        TextView mockTeamView = mock(TextView.class);

        given(itemView.findViewById(R.id.imageView)).willReturn(mockImageView);
        given(itemView.findViewById(R.id.name)).willReturn(mockNameView);
        given(itemView.findViewById(R.id.team)).willReturn(mockTeamView);

        EmployeeAdapter.EmployeeViewHolder employeeViewHolder = mUnderTest.new EmployeeViewHolder(itemView);

        employeeViewHolder.bind(mEmployee);
        employeeViewHolder.unbind();

        verify(mImageLoader).cancelPendingCallback(any(ImageLoader.ImageLoadedCallback.class));
    }

    /**
     * Test that 2 bind calls will cancel previous image load callback.
     */
    @Test
    public void testBindTwice(){
        View itemView = mock(View.class);
        ImageView mockImageView = mock(ImageView.class);
        TextView mockNameView = mock(TextView.class);
        TextView mockTeamView = mock(TextView.class);

        given(itemView.findViewById(R.id.imageView)).willReturn(mockImageView);
        given(itemView.findViewById(R.id.name)).willReturn(mockNameView);
        given(itemView.findViewById(R.id.team)).willReturn(mockTeamView);

        EmployeeAdapter.EmployeeViewHolder employeeViewHolder = mUnderTest.new EmployeeViewHolder(itemView);

        employeeViewHolder.bind(mEmployee);
        employeeViewHolder.bind(mEmployee);

        verify(mImageLoader).cancelPendingCallback(any(ImageLoader.ImageLoadedCallback.class));
    }
}
