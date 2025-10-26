package com.app.ecom.dto;

import lombok.Data;

@Data
public class CartItemRequest {

    private Long productId;
    private Long quantity;
}
