package square.takehome.employeedirectory.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import square.takehome.employeedirectory.model.Employee;
import square.takehome.employeedirectory.model.EmployeesResponse;

import java.util.List;

public class EmployeeViewModel extends ViewModel
{
    private static final String TAG = EmployeeViewModel.class.getSimpleName();
    private MutableLiveData<List<Employee>> mEmployees;
    public LiveData<List<Employee>> getEmployees() {
        if (mEmployees == null) {
            mEmployees = new MutableLiveData<>();
            loadEmployees();
        }
        return mEmployees;
    }

    private void loadEmployees() {
        Call<ResponseBody> call = new NetworkApi().getApi().getEmployees();
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                try
                {
                    if (response.body() == null) {
                        return;
                    }
                    final String jsonString = new String(response.body().bytes());
                    final EmployeesResponse employeesResponse = new ObjectMapper().
                            readValue(jsonString, EmployeesResponse.class);
                    mEmployees.setValue(employeesResponse.getEmployees());
                }
                catch (Exception e)
                {
                    Log.e(TAG, "error processing response", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                Log.e(TAG, "error fetching employees", t);
            }
        });
    }
}
