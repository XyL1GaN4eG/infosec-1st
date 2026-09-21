package ru.itmo.infosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import ru.itmo.infosec.entity.ProductModel;
import ru.itmo.infosec.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductModel> getAllProducts() {
        return productRepository.findAll().stream().peek(
                it -> it.setData(HtmlUtils.htmlEscape(it.getData()))
        ).toList();
    }
}