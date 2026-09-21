package ru.itmo.infosec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.infosec.entity.ProductModel;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, String> {
}