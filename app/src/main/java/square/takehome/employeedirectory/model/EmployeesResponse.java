package square.takehome.employeedirectory.model;

import java.util.ArrayList;
import java.util.List;

public class EmployeesResponse
{
    private List<Employee> employees = new ArrayList<>();

    public List<Employee> getEmployees() {
        return employees;
    }
}