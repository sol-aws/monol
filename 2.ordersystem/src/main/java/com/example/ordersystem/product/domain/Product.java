package com.example.ordersystem.product.domain;

import com.example.ordersystem.common.domain.BaseTimeEntity;
import com.example.ordersystem.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;

    // S3에 저장된 실제 이미지 파일의 Object Key만 DB에 저장한다.
    // 상품 조회 시 Public S3 URL로 변환해서 Frontend에 반환한다.
    private String imageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateStockQuantity(int stockQuantity){
        this.stockQuantity = this.stockQuantity - stockQuantity;
    }
}
