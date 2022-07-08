package se.magnus.microservices.composite.insurancecompany.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.insuranceCompany.*;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InsuranceCompanyCompositeServiceImpl implements InsuranceCompanyCompositeService {

    private final ServiceUtil serviceUtil;
    private InsuranceCompanyCompositeIntegration integration;
    private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyCompositeServiceImpl.class);

    private final SecurityContext nullSC = new SecurityContextImpl();

    @Autowired
    public InsuranceCompanyCompositeServiceImpl(ServiceUtil serviceUtil,
                                                InsuranceCompanyCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<Void> createCompositeInsuranceCompany(InsuranceCompanyAggregate body) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalCreateCompositeInsuranceCompany(sc, body)).then();
    }

    private void internalCreateCompositeInsuranceCompany(SecurityContext sc, InsuranceCompanyAggregate body) {

        try {

            logAuthorizationInfo(sc);

            LOG.debug("createCompositeInsuranceCompany: creates a new composite entity for insuranceCompanyId: {}", body.getInsuranceCompanyId());

            InsuranceCompany insuranceCompany = new InsuranceCompany(body.getInsuranceCompanyId(), body.getName(), body.getCity(),
                    body.getAddress(), body.getPhoneNumber(), null);
            integration.createInsuranceCompany(insuranceCompany);

            if (body.getEmployees() != null) {
                body.getEmployees().forEach(r -> {
                    Employee employee = new Employee(body.getInsuranceCompanyId(), r.getEmployeeId(), r.getName(), r.getSurname(), null, r.getSpecialization(), null);
                    integration.createEmployee(employee);
                });
            }

            if (body.getInsuranceOffers() != null) {
                body.getInsuranceOffers().forEach(r -> {
                    InsuranceOffer insuranceOffer = new InsuranceOffer(body.getInsuranceCompanyId(), r.getInsuranceOfferId(), r.getOfferName(), null, null, r.getPrice(), r.getCurrencyOffer(), null);
                    integration.createInsuranceOffer(insuranceOffer);
                });
            }

            if (body.getTransactions() != null) {
                body.getTransactions().forEach(r -> {
                    Transaction transaction = new Transaction(body.getInsuranceCompanyId(), r.getTransactionId(), r.getTypeTransaction(), null, r.getAmount(), r.getCurrencyTransaction(), null, r.getPolicyNumber(), null);
                    integration.createTransaction(transaction);
                });
            }

            LOG.debug("createCompositeInsuranceCompany: composite entites created for insuranceCompanyId: {}", body.getInsuranceCompanyId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    @Override
    public Mono<InsuranceCompanyAggregate> getCompositeInsuranceCompany(int insuranceCompanyId, int delay, int faultPercent) {
        return Mono.zip(
                        values -> createInsuranceCompanyAggregate((SecurityContext) values[0],(InsuranceCompany) values[1], (List<Employee>) values[2],
                                (List<InsuranceOffer>) values[3], (List<Transaction>) values[4], serviceUtil.getServiceAddress()),
                        ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
                        integration.getInsuranceCompany(insuranceCompanyId, delay, faultPercent)
                                .onErrorReturn(CallNotPermittedException.class, getInsuranceCompanyFallbackValue(insuranceCompanyId)),
                        integration.getEmployees(insuranceCompanyId).collectList(),
                        integration.getInsuranceOffers(insuranceCompanyId).collectList(),
                        integration.getTransactions(insuranceCompanyId).collectList())
                      .doOnError(ex -> LOG.warn("getCompositeInsuranceCompany failed: {}", ex.toString()))
                      .log();
    }

    @Override
    public Mono<Void> deleteCompositeInsuranceCompany(int insuranceCompanyId) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(sc -> internalDeleteCompositeInsuranceCompany(sc, insuranceCompanyId)).then();
    }

    private void internalDeleteCompositeInsuranceCompany(SecurityContext sc,int insuranceCompanyId) {

        try {
            logAuthorizationInfo(sc);
            LOG.debug("deleteCompositeInsuranceCompany: Deletes a insurance company aggregate for insuranceCompanyId: {}", insuranceCompanyId);

            integration.deleteInsuranceCompany(insuranceCompanyId);

            integration.deleteEmployees(insuranceCompanyId);

            integration.deleteInsuranceOffers(insuranceCompanyId);

            integration.deleteTransactions(insuranceCompanyId);

            LOG.debug("getCompositeInsuranceCompany: aggregate entities deleted for insuranceCompanyId: {}", insuranceCompanyId);
        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    private InsuranceCompany getInsuranceCompanyFallbackValue(int insuranceCompanyId) {

        LOG.warn("Creating a fallback insurance company for insuranceCompanyId = {}", insuranceCompanyId);

        if (insuranceCompanyId == 13) {
            String errMsg = "Insurance company id: " + insuranceCompanyId + " not found in fallback cache!";
            LOG.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new InsuranceCompany(insuranceCompanyId, "Fallback insurance company" + insuranceCompanyId, "city", "address","phoneNumber", serviceUtil.getServiceAddress());
    }

    private InsuranceCompanyAggregate createInsuranceCompanyAggregate(SecurityContext sc, InsuranceCompany insuranceCompany,
                                                                      List<Employee> employees, List<InsuranceOffer> insuranceOffers, List<Transaction> transactions,
                                                                      String serviceAddress) {
        logAuthorizationInfo(sc);

        // 1. Setup insuranceCompany info
        int insuranceCompanyId = insuranceCompany.getInsuranceCompanyId();
        String name = insuranceCompany.getName();
        String city = insuranceCompany.getCity();
        String address = insuranceCompany.getAddress();
        String phoneNumber = insuranceCompany.getPhoneNumber();

        // 2. Copy summary employee info, if available
        List<EmployeeSummary> employeesSummaries = (employees == null) ? null
                : employees.stream().map(r -> new EmployeeSummary(r.getEmployeeId(), r.getName(), r.getSurname(),
                r.getSpecialization())).collect(Collectors.toList());

        // 3. Copy summary insuranceOffer info, if available
        List<InsuranceOfferSummary> insuranceOffersSummaries = (insuranceOffers == null) ? null
                : insuranceOffers.stream().map(r -> new InsuranceOfferSummary(r.getInsuranceOfferId(), r.getOfferName(),
                r.getPrice(), r.getCurrencyOffer())).collect(Collectors.toList());

        // 4. Copy summary transaction info, if available
        List<TransactionSummary> transactionsSummaries = (transactions == null) ? null
                : transactions.stream()
                .map(r -> new TransactionSummary(r.getTransactionId(), r.getTypeTransaction(), r.getAmount(),
                        r.getCurrencyTransaction(), r.getPolicyNumber()))
                .collect(Collectors.toList());

        // 5. Create info regarding the involved microservices addresses
        String insuranceCompanyAddress = insuranceCompany.getServiceAddress();
        String employeeAddress = (employees != null && employees.size() > 0) ? employees.get(0).getServiceAddress()
                : "";
        String insuranceOfferAddress = (insuranceOffers != null && insuranceOffers.size() > 0)
                ? insuranceOffers.get(0).getServiceAddress()
                : "";
        String transactionAddress = (transactions != null && transactions.size() > 0)
                ? transactions.get(0).getServiceAddress()
                : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, insuranceCompanyAddress,
                employeeAddress, insuranceOfferAddress, transactionAddress);

        return new InsuranceCompanyAggregate(insuranceCompanyId, name, city, address, phoneNumber, employeesSummaries,
                insuranceOffersSummaries, transactionsSummaries, serviceAddresses);
    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }
    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }

}