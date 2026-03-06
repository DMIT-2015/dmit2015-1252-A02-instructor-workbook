package dmit2015.repository;

import dmit2015.entity.Department;
import dmit2015.entity.Employee;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.hibernate.annotations.processing.Pattern;

import java.util.List;

@Repository
public interface HumanResourcesRepository {

    // Automatic query does not support case-insensitive string compare
//    @Find
//    @OrderBy("departmentName")
    // Annotated Query method with Java Text Block
    @Query("""
select d
 from Department d
 where lower(d.departmentName) like lower(?1)
 order by d.departmentName
""")
    List<Department> departmentsBy(String departmentName);

    @Query("""
select e
from Employee e join fetch e.department join fetch e.job join fetch e.manager
where e.department.id = ?1
""")
    List<Employee> employeesByDepartmentId(Short deptId);

    @Find
    Department departmentByDepartmentId(Short id);
}
