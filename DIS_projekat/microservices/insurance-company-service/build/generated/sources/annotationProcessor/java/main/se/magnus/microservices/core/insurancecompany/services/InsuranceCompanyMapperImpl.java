package se.magnus.microservices.core.insurancecompany.services;

import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.microservices.core.insurancecompany.persistence.InsuranceCompanyEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-08T13:55:01+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class InsuranceCompanyMapperImpl implements InsuranceCompanyMapper {

    @Override
    public InsuranceCompany entityToApi(InsuranceCompanyEntity entityInsuranceCompany) {
        if ( entityInsuranceCompany == null ) {
            return null;
        }

        InsuranceCompany insuranceCompany = new InsuranceCompany();

        insuranceCompany.setInsuranceCompanyId( entityInsuranceCompany.getInsuranceCompanyId() );
        insuranceCompany.setName( entityInsuranceCompany.getName() );
        insuranceCompany.setCity( entityInsuranceCompany.getCity() );
        insuranceCompany.setAddress( entityInsuranceCompany.getAddress() );
        insuranceCompany.setPhoneNumber( entityInsuranceCompany.getPhoneNumber() );

        return insuranceCompany;
    }

    @Override
    public InsuranceCompanyEntity apiToEntity(InsuranceCompany apiInsuranceCompany) {
        if ( apiInsuranceCompany == null ) {
            return null;
        }

        InsuranceCompanyEntity insuranceCompanyEntity = new InsuranceCompanyEntity();

        insuranceCompanyEntity.setInsuranceCompanyId( apiInsuranceCompany.getInsuranceCompanyId() );
        insuranceCompanyEntity.setName( apiInsuranceCompany.getName() );
        insuranceCompanyEntity.setCity( apiInsuranceCompany.getCity() );
        insuranceCompanyEntity.setAddress( apiInsuranceCompany.getAddress() );
        insuranceCompanyEntity.setPhoneNumber( apiInsuranceCompany.getPhoneNumber() );

        return insuranceCompanyEntity;
    }
}
