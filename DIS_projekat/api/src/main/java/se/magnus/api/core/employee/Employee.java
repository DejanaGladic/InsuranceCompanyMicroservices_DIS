package se.magnus.api.core.employee;

public class Employee {
	private final int insuranceCompanyId;
	private final int employeeId;
	private final String name;
	private final String surname;
	private final String education;
	private final String specialization;
	private final String serviceAddress;

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
}
