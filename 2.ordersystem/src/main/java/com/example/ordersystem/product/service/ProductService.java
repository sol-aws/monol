package com.example.ordersystem.product.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dto.ProductRegisterDto;
import com.example.ordersystem.product.dto.ProductResponseDto;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    public ProductService(ProductRepository productRepository, MemberRepository memberRepository, S3Service s3Service) {
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.s3Service = s3Service;
    }

    // 로그인한 사용자의 ID는 JWT 인증 후 Authentication 객체에 저장되어 있다.
    public Product productCreate(ProductRegisterDto dto, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new EntityNotFoundException("member is not found"));

        String imageKey = s3Service.upload(image);
        return productRepository.save(dto.toEntity(member, imageKey));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> productList() {
        return productRepository.findAll().stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .category(product.getCategory())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .imageUrl(s3Service.createImageUrl(product.getImageKey()))
                        .build())
                .toList();
    }
}
