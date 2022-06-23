package se.magnus.api.core.employee;

public class Employee {
	private int insuranceCompanyId;
	private int employeeId;
	private String name;
	private String surname;
	private String education;
	private String specialization;
	private String serviceAddress;

	public Employee() {
		insuranceCompanyId = 0;
		employeeId = 0;
		name = null;
		surname = null;
		education = null;
		specialization = null;
		serviceAddress = null;
	}

	public Employee(int insuranceCompanyId, int employeeId, String name, String surname, String education,
			String specialization, String serviceAddress) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.employeeId = employeeId;
		this.name = name;
		this.surname = surname;
		this.education = education;
		this.specialization = specialization;
		this.serviceAddress = serviceAddress;
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

	public String getServiceAddress() {
		return serviceAddress;
	}
	
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public void setInsuranceCompanyId(int insuranceCompanyId) {
		this.insuranceCompanyId = insuranceCompanyId;
	}

	public void setEmployeeId(int employeeId) {
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
