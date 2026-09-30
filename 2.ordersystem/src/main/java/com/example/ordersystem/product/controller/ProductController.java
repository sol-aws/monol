package com.example.ordersystem.product.controller;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.service.ProductService;
import jakarta.validation.Valid;
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

    // 상품 정보 + 이미지 파일을 multipart/form-data 한 번의 요청으로 받는다.
    // 상품등록 API는 SecurityConfig에서 인증된 사용자만 접근 가능하다.
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> productCreate(
            @Valid @ModelAttribute ProductRegisterDto dto,
            @RequestParam("image") MultipartFile image) throws IOException {
        Product product = productService.productCreate(dto, image);
        return new ResponseEntity<>(product.getId(), HttpStatus.CREATED);
    }

    // 메인 페이지 상품 카드에서 사용한다. 로그인하지 않아도 조회할 수 있다.
    @GetMapping("/list")
    public ResponseEntity<?> productList(){
        return ResponseEntity.ok(productService.productList());
    }
}
