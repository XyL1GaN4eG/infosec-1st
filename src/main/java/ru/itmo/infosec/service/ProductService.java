package ru.itmo.infosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import ru.itmo.infosec.dto.ProductDto;
import ru.itmo.infosec.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDto(product.getId(),
                        product.getData() == null ? null : HtmlUtils.htmlEscape(product.getData())))
                .toList();
    }
}
