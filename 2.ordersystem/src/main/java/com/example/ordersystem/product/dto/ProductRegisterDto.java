package com.example.ordersystem.product.dto;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.product.domain.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRegisterDto {
    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotBlank(message = "카테고리를 입력하세요.")
    private String category;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "재고수량은 0개 이상이어야 합니다.")
    private int stockQuantity;

    public Product toEntity(Member member, String imageKey){
        return Product.builder()
                .name(this.name.trim())
                .category(this.category.trim())
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .imageKey(imageKey)
                .member(member)
                .build();
    }
}
