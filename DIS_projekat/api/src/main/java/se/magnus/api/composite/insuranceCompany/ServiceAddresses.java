package se.magnus.api.composite.insuranceCompany;

public class ServiceAddresses {
	private String cmp;
	private String incom;
	private String emp;
	private String inoff;
	private String trans;

	public ServiceAddresses() {
		cmp = null;
		incom = null;
		emp = null;
		inoff = null;
		trans = null;
	}

	public ServiceAddresses(String compositeAddress, String insuranceCompanyAddress, String employeeAddress,
			String insuranceOfferAddress, String transactionAddress) {
		cmp = compositeAddress;
		incom = insuranceCompanyAddress;
		emp = employeeAddress;
		inoff = insuranceOfferAddress;
		trans = transactionAddress;
	}

	public String getCmp() {
		return cmp;
	}

	public String getIncom() {
		return incom;
	}

	public String getEmp() {
		return emp;
	}

	public String getInoff() {
		return inoff;
	}
	
	public String getTrans() {
		return trans;
	}
}
