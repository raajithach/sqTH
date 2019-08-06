package square.takehome.employeedirectory;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import square.takehome.employeedirectory.model.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for Employee.
 */
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>
{
    private List<Employee> mEmployeeList = new ArrayList<>();
    private ImageLoader mImageLoader;

    EmployeeAdapter(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    @VisibleForTesting
    EmployeeAdapter(ImageLoader imageLoader, List<Employee> employeeList) {
        mImageLoader = imageLoader;
        mEmployeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new EmployeeViewHolder(
                inflater.inflate(R.layout.employee_item_layout, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder viewHolder, int i)
    {
        viewHolder.bind(mEmployeeList.get(i));
    }

    @Override
    public void onViewRecycled(@NonNull EmployeeViewHolder holder)
    {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    @Override
    public int getItemCount()
    {
        return mEmployeeList.size();
    }

    /**
     * Update list of employees.
     * @param employeeList list of updated employees.
     */
    public void setEmployeeList(@NonNull final List<Employee> employeeList) {
        mEmployeeList.clear();
        mEmployeeList.addAll(employeeList);
        notifyDataSetChanged();
    }

    public class EmployeeViewHolder extends ViewHolder {
        private ImageLoader.ImageLoadedCallback mImageLoadedCallback;
        private Handler mHandler;

        EmployeeViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mHandler = new Handler();
        }

        void bind(final Employee employee) {
            final ImageView imageView = itemView.findViewById(R.id.imageView);
            if (mImageLoadedCallback != null) {
                // cancel the previous callback.
                mImageLoader.cancelPendingCallback(mImageLoadedCallback);
            }

            mImageLoadedCallback = new ImageLoader.ImageLoadedCallback()
            {
                @Override
                public void onImageLoaded(final Bitmap bitmap)
                {
                    if (bitmap != null) {
                        mHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                imageView.setImageBitmap(bitmap);

                            }
                        });
                    }
                }
            };
            mImageLoader.getImage(employee.getPhoto_url_small(), mImageLoadedCallback);

            TextView name = itemView.findViewById(R.id.name);
            name.setText(employee.getFull_name());

            TextView team = itemView.findViewById(R.id.team);
            team.setText(employee.getTeam());
        }

        void unbind() {
            mImageLoader.cancelPendingCallback(mImageLoadedCallback);
        }
    }
}
