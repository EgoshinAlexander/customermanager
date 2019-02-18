package crud.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import crud.dao.Customer;
import crud.dao.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout implements KeyNotifier {

    private final List<String> genders = new ArrayList<>();
    private final CustomerRepository repository;
    private Customer customer;

    private final TextField name = new TextField("Name");
    private final ComboBox<String> gender;
    private final DatePicker bornDate = new DatePicker("Date of Birth");


    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private final HorizontalLayout action = new HorizontalLayout(save, cancel, delete);

    private  final Binder<Customer> binder = new Binder<>(Customer.class);

    private ChangeHandler changeHandler;

    @Autowired
    public CustomerEditor(CustomerRepository repository) {
        this.repository = repository;

        genders.add("Male");
        genders.add("Female");

        this.gender = new ComboBox<>("Gender", genders);
        this.gender.setValue(genders.get(0));

        bornDate.setMax(LocalDate.now());

        HorizontalLayout fields = new HorizontalLayout(name, gender, bornDate);
        add(fields, action);

        binder.bindInstanceFields(this);
        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");


        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editCustomer(customer));
        setVisible(false);
    }

    public void editCustomer(Customer customer) {
        if (customer == null) {
            setVisible(false);
            return;
        }
        final boolean percisted = customer.getId() != null;
        if (percisted) {
            this.customer = repository.findById(customer.getId()).get();
        } else {
            this.customer = customer;
        }
        cancel.setVisible(percisted);
        binder.setBean(this.customer);
        setVisible(true);
        name.focus();
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

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }
}
