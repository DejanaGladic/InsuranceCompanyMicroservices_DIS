package se.magnus.api.core.insuranceCompany;

public class InsuranceCompany{

	private int insuranceCompanyId;
	private String name;
	private String city;
	private String address;
	private String phoneNumber;
	private String serviceAddress;

	public InsuranceCompany() {
		insuranceCompanyId = 0;
		name = null;
		city = null;
		address = null;
		phoneNumber = null;
		serviceAddress = null;
	}

	public InsuranceCompany(int insuranceCompanyId, String name, String city, String address, String phoneNumber,
			String serviceAddress) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.name = name;
		this.city = city;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.serviceAddress = serviceAddress;
	}

	public int getInsuranceCompanyId() {
		return this.insuranceCompanyId;
	}

	public String getName() {
		return this.name;
	}

	public String getCity() {
		return this.city;
	}

	public String getAddress() {
		return this.address;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public String getServiceAddress() {
		return this.serviceAddress;
	}
	
	public void setServiceAddress(String serviceAddress) {
		 this.serviceAddress = serviceAddress;
	}

	public void setInsuranceCompanyId(int insuranceCompanyId) {
		this.insuranceCompanyId = insuranceCompanyId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}