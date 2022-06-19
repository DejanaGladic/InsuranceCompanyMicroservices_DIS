package se.magnus.api.composite.insuranceCompany;

public class EmployeeSummary {
	private final int employeeId;
	private final String name;
	private final String surname;
	private final String specialization;

	public EmployeeSummary() {
		this.employeeId = 0;
		this.name = null;
		this.surname = null;
		this.specialization = null;
	}

	public EmployeeSummary(int employeeId, String name, String surname, String specialization) {
		this.employeeId = employeeId;
		this.name = name;
		this.surname = surname;
		this.specialization = specialization;
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

	public String getSpecialization() {
		return specialization;
	}
}
