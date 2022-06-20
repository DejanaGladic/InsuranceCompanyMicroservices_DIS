package se.magnus.microservices.core.employee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.employee.*;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final EmployeeService employeeService;

    @Autowired
    public MessageProcessor (EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Employee> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
        	Employee employee = event.getData();
            LOG.info("Create employee with ID: {}", employee.getEmployeeId());
            employeeService.createEmployee(employee);
            break;

        case DELETE:
            int insuranceCompanyId = event.getKey();
            LOG.info("Delete employees with insuranceCompanyId: {}", insuranceCompanyId);
            employeeService.deleteEmployees(insuranceCompanyId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}