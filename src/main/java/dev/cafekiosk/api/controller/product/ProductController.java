package dev.cafekiosk.api.controller.product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import dev.cafekiosk.api.ApiResponse;
import dev.cafekiosk.api.controller.product.request.ProductCreateRequest;
import dev.cafekiosk.api.service.product.ProductService;
import dev.cafekiosk.api.service.product.response.ProductResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest) {
        ProductResponse productResponse = productService.createProduct(productCreateRequest.toServiceRequest());
        return ApiResponse.ok(productResponse);
    }

    @GetMapping("/products/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {
        return ApiResponse.ok(productService.getSellingProducts());
    }

}
