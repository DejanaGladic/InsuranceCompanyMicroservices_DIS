package se.magnus.microservices.core.employee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public EmployeeServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Employee> getEmployees(int insuranceCompanyId) {

        if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

        if (insuranceCompanyId == 113) {
            LOG.debug("No employees found for insuranceCompanyId: {}", insuranceCompanyId);
            return  new ArrayList<>();
        }

        List<Employee> list = new ArrayList<>();
        list.add(new Employee(insuranceCompanyId, 1, "Dejana","Gladic", "Education 1", "Specialization 1", serviceUtil.getServiceAddress()));
        list.add(new Employee(insuranceCompanyId, 2, "Marinko", "Petrovic", "Education 2", "Specialization 2", serviceUtil.getServiceAddress()));
        list.add(new Employee(insuranceCompanyId, 3, "Magdalena", "Ninkov", "Education 3", "Specialization 3", serviceUtil.getServiceAddress()));

        LOG.debug("/employee response size: {}", list.size());

        return list;
    }
}