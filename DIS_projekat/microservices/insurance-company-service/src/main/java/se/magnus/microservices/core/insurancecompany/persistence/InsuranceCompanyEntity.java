package se.magnus.microservices.core.insurancecompany.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.lang.String.format;

//mapiramo Entity klasu u kolekciju u MongoDB-u
@Document(collection="insuranceCompanys")
public class InsuranceCompanyEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int insuranceCompanyId;
	
    private String name;
	private String city;
	private String address;
	private String phoneNumber;

	public InsuranceCompanyEntity() {
	}

	public InsuranceCompanyEntity(int insuranceCompanyId, String name, String city, String address, String phoneNumber) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.name = name;
		this.city = city;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

    @Override
    public String toString() {
        return format("InsuranceCompanyEntity: %s", insuranceCompanyId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
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
	
    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
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