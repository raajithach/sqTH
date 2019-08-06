package square.takehome.employeedirectory;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import square.takehome.employeedirectory.model.Employee;
import square.takehome.employeedirectory.network.EmployeeViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private EmployeeAdapter mEmployeeAdapter;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageLoader = ImageLoader.getInstance(this);
        setUpViews();
        fetchData();
    }

    private void fetchData() {
        EmployeeViewModel model = ViewModelProviders.of(this).get(EmployeeViewModel.class);
        model.getEmployees().observe(this, new Observer<List<Employee>>()
        {
            @Override
            public void onChanged(@Nullable List<Employee> employees)
            {
                mEmployeeAdapter.setEmployeeList(employees);
            }
        });
    }

    private void setUpViews() {
        RecyclerView recyclerView = findViewById(R.id.employeeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        mEmployeeAdapter = new EmployeeAdapter(mImageLoader);
        recyclerView.setAdapter(mEmployeeAdapter);
    }
}
