package com.example.crudwithvaadin;


import java.util.List;

public interface CustomerRepository {

	List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);
}
