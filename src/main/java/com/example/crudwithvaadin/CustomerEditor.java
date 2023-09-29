package com.example.crudwithvaadin;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout implements KeyNotifier {

	private final CustomerRepository repository;

	private Customer customer;

	TextField firstName = new TextField("First name");

	TextField lastName = new TextField("Last name");

	Binder<Customer> binder = new Binder<>(Customer.class);

	private ChangeHandler changeHandler;

	@Autowired
	public CustomerEditor(CustomerRepository repository) {
		this.repository = repository;

		add(firstName, lastName);

		binder.bindInstanceFields(this);

		setSpacing(true);
		setVisible(false);
	}

	void delete() {
		repository.delete(customer);
		changeHandler.onChange();
	}

	void save() {
		repository.save(customer);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}

	public final void editCustomer(Customer c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			customer = repository.findById(c.getId()).get();
		}
		else {
			customer = c;
		}
		binder.setBean(customer);

		setVisible(true);

		firstName.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		changeHandler = h;
	}
}