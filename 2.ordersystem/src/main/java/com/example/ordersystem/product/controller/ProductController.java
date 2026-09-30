package com.example.ordersystem.product.controller;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 상품등록은 JWT 인증된 사용자만 가능하다.
    // 이미지와 상품 정보를 함께 전송하기 위해 multipart/form-data를 사용한다.
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> productCreate(
            @ModelAttribute ProductRegisterDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Product product = productService.productCreate(dto, image);
        return new ResponseEntity<>(product.getId(), HttpStatus.CREATED);
    }

    // 쇼핑몰 메인 화면에서 상품 목록을 출력하기 위한 API
    @GetMapping("/list")
    public ResponseEntity<?> productList(){
        return ResponseEntity.ok(productService.productList());
    }
}
