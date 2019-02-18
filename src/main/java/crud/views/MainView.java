package crud.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import crud.dao.Customer;
import crud.dao.CustomerRepository;
import org.springframework.util.StringUtils;

import java.util.List;

@Route()
public class MainView extends VerticalLayout {
    private final CustomerRepository repository;
    private final CustomerEditor editor;

    private final Grid<Customer> grid;
    private final TextField filter;
    private final Button addNewCustomerButton;

    public MainView(CustomerRepository repository, CustomerEditor editor) {
        this.repository = repository;
        this.editor = editor;
        this.grid = new Grid<>(Customer.class);
        this.filter = new TextField();
        this.addNewCustomerButton = new Button("New customer", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(filter, addNewCustomerButton);

        setSpacing(true);

        grid.setColumns("id", "name", "gender", "bornDate");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        grid.setHeight("300px");

        add(actions, grid, editor);

        filter.setPlaceholder("Filter by name");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> listCustomers(event.getValue()));

        grid.asSingleSelect().addValueChangeListener(e -> editor.editCustomer(e.getValue()));

        addNewCustomerButton.addClickListener(e -> editor.editCustomer(new Customer()));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });
        listCustomers(null);

    }

    private void listCustomers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            List<Customer> listCustomers = repository.findAll();
            grid.setItems(listCustomers);
        } else {
            grid.setItems(repository.findByNameStartsWithIgnoreCase(filterText));
        }
    }
}
