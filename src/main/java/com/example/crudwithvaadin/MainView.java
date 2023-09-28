package com.example.crudwithvaadin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

@Route
public class MainView extends VerticalLayout {

	private final CustomerRepository repo;

	final Grid<Customer> grid;

	final TextField filter;

	public MainView(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();

		// build layout
		Button addNewBtn = new Button("New customer", VaadinIcon.PLUS.create());
		createHeader(addNewBtn);
		HorizontalLayout actions = new HorizontalLayout();
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle("New Customer");
		addNewBtn.addClickListener(click -> {
			dialog.add(editor);
			dialog.open();
			editor.editCustomer(new Customer());
		});
		Button closeButton = new Button("Delete", VaadinIcon.TRASH.create(),
			(e) -> {
			editor.delete();
			dialog.close();
		});
		closeButton.getElement().setAttribute("theme", "error");
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		Button saveButton = new Button("Save", e -> {
			editor.save();
			dialog.close();
		});
		saveButton.getElement().setAttribute("theme", "primary");
		saveButton.addClickShortcut(Key.ENTER);
		HorizontalLayout editorActions = new HorizontalLayout();
		editorActions.add(saveButton, closeButton);
		editorActions.setSpacing(true);
		editorActions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		editor.add(editorActions);

		add(actions, grid, editor);

		grid.setHeight("300px");
		grid.setColumns("id", "firstName", "lastName");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listCustomers(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			dialog.add(editor);
			dialog.open();
			editor.editCustomer(e.getValue());
		});

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});

		// Initialize listing
		listCustomers(null);
	}

	private void createHeader(Button addNewBtn) {
		H1 h1 = new H1("Customers CRUD");
		HorizontalLayout header = new HorizontalLayout();
		header.addClassName("viewheader");
		header.setWidth("100%");
		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.setPadding(true);
		header.setSpacing(true);
		header.setMargin(false);
		header.add(h1);
		header.add(filter);
		header.add(addNewBtn);
		add(header);
	}

	void listCustomers(String filterText) {
		if (StringUtils.hasText(filterText)) {
			grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
		} else {
			grid.setItems(repo.findAll());
		}
	}
}
