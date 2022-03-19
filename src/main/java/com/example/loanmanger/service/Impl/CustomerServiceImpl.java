package com.example.loanmanger.service.Impl;

import com.example.loanmanger.domain.entity.Customer;
import com.example.loanmanger.domain.entity.embadable.FullName;
import com.example.loanmanger.domain.entity.embadable.Passport;
import com.example.loanmanger.exception.CustomerNotFoundException;
import com.example.loanmanger.repository.CustomerRepository;
import com.example.loanmanger.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer create(Customer customer) {
        return Optional.of(customer)
                .map(customerRepository::save)
                .get();
    }

    @Override
    public Customer getByPassportCode(String code) {
        return customerRepository.findByPassportCode(code)
                .orElseThrow(() -> new CustomerNotFoundException("Passport code: " + code));
    }

    @Override
    public Customer getByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Phone number: " + phoneNumber));
    }

    @Override
    public Page<Customer> getByFullName(FullName fullName, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByFullName(fullName, pageable);
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException(fullName.toString());
        }
        return customers;
    }

    @Override
    public Page<Customer> getByFirstNameAndSurname(String firstName, String surname, Pageable pageable) {
        Page<Customer> customers = customerRepository
                .findByFullNameFirstNameAndFullNameSurname(firstName, surname, pageable);
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException(firstName + " " + surname);
        }
        return customers;
    }

    @Override
    public boolean isExist(Customer customer) {
        return Optional.of(customer)
                .map(Customer::getPassport)
                .map(Passport::getCode)
                .map(customerRepository::existsByPassportCode)
                .isPresent();
    }

    @Override
    public Customer update(Customer customer) {
        return customerRepository.save(customer);
    }
}
