package crud.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<crud.dao.Customer, Long> {
    List<crud.dao.Customer> findByNameStartsWithIgnoreCase(String name);
}
