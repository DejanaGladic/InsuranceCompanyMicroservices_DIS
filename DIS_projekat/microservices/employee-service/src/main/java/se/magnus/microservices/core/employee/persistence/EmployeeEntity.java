package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import static java.lang.String.format;

@Document(collection="employees")
@CompoundIndex(name = "incom-empl-id", unique = true, def = "{'insuranceCompanyId': 1, 'employeeId' : 1}")
public class EmployeeEntity {

    @Id
    private String id;

    @Version
    private Integer version;

	private int insuranceCompanyId;
	private int employeeId;
	private String name;
	private String surname;
	private String education;
	private String specialization;

	public EmployeeEntity() {
	}

	public EmployeeEntity(int insuranceCompanyId, int employeeId, String name, String surname, String education,
			String specialization) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.employeeId = employeeId;
		this.name = name;
		this.surname = surname;
		this.education = education;
		this.specialization = specialization;
	}

    @Override
    public String toString() {
        return format("EmployeeEntity: %s/%d", insuranceCompanyId, employeeId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }
    
    public int getInsuranceCompanyId() {
		return insuranceCompanyId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEducation() {
		return education;
	}

	public String getSpecialization() {
		return specialization;
	}

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public void setInsuranceCompanyId(Integer insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }
    
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    
}