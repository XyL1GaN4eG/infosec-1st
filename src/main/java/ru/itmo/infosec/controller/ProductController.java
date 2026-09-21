package ru.itmo.infosec.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.infosec.entity.ProductModel;
import ru.itmo.infosec.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/data")
    public List<ProductModel> getData() {
        return productService.getAllProducts();
    }
}