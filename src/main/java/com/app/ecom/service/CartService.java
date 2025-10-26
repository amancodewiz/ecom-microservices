package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ====================================================================
 * 🛒 CartService — Handles all cart-related business logic
 * ====================================================================
 *
 * This class acts as the **Service Layer** for shopping cart operations.
 * It coordinates between the controller and repository layers to manage:
 *  • Adding items to a user's cart
 *  • Deleting items from the cart
 *  • Retrieving a user's cart items
 *  • Clearing the entire cart
 *
 * All methods are marked transactional to ensure data consistency:
 * If any database operation fails, the entire transaction rolls back.
 *
 * Key Annotations:
 *  - @Service → Marks it as a Spring-managed service component.
 *  - @Transactional → Ensures atomic database operations.
 *  - @RequiredArgsConstructor → Automatically injects final dependencies.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    /** Repository dependencies — injected via constructor using Lombok */
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------------
    // 🔹 addToCart() — Add or update cart item
    // ----------------------------------------------------------------------

    /**
     * Adds a product to the user's cart.
     * If the product already exists, increments its quantity and updates price.
     *
     * @param userId  The ID of the user (as String for header compatibility)
     * @param request Contains productId and quantity to be added
     * @return true if successfully added/updated, false if validation fails
     *
     * <p><b>Business Flow:</b></p>
     *  1️⃣ Validate product existence
     *  2️⃣ Check stock availability
     *  3️⃣ Validate user existence
     *  4️⃣ Add new item or update existing one
     */
    public boolean addToCart(String userId, CartItemRequest request) {

        // Step 1: Validate product existence
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty())
            return false;

        Product product = productOpt.get();

        // Step 2: Ensure requested quantity is available
        if (product.getStockQuantity() < request.getQuantity())
            return false;

        // Step 3: Validate user existence
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty())
            return false;

        User user = userOpt.get();

        // Step 4: Check if item already exists in cart
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {
            // ✅ Update existing cart item quantity and total price
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(
                    product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity()))
            );
            cartItemRepository.save(existingCartItem);
        } else {
            // ✅ Create a new cart item if none exists
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(
                    product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
            );
            cartItemRepository.save(cartItem);
        }

        return true;
    }

    // ----------------------------------------------------------------------
    // 🔹 deleteItemFromCart() — Remove specific product from cart
    // ----------------------------------------------------------------------

    /**
     * Deletes a specific product from the user's cart.
     *
     * @param userId    The ID of the user
     * @param productId The ID of the product to remove
     * @return true if deleted successfully, false if product/user not found
     *
     * <p><b>Logic:</b></p>
     *  • Confirms both user and product exist
     *  • Uses repository method deleteByUserAndProduct() to remove item
     */
    public boolean deleteItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

        if (productOpt.isPresent() && userOpt.isPresent()) {
            cartItemRepository.deleteByUserAndProduct(userOpt.get(), productOpt.get());
            return true;
        }

        return false;
    }

    // ----------------------------------------------------------------------
    // 🔹 getCart() — Retrieve all items for a given user
    // ----------------------------------------------------------------------

    /**
     * Retrieves all cart items for the specified user.
     *
     * @param userId The ID of the user
     * @return List of CartItem objects; empty list if user not found or cart empty
     *
     * <p><b>Notes:</b></p>
     *  • Uses Optional.map() for concise null-safe mapping
     *  • Returns immutable empty list (List.of()) when user not found
     */
    public List<CartItem> getCart(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)   // Fetch cart items for valid user
                .orElse(List.of());                    // Return empty list otherwise
    }

    // ----------------------------------------------------------------------
    // 🔹 clearCart() — Remove all cart items for a user
    // ----------------------------------------------------------------------

    /**
     * Clears all items in a user's cart.
     *
     * @param userId The ID of the user whose cart should be cleared
     *
     * <p><b>Implementation Detail:</b></p>
     *  • Uses Optional.ifPresent() to conditionally execute deletion
     *  • Ensures deletion only occurs for valid user records
     *  • Internally calls repository method deleteByUser()
     */
    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId))
                .ifPresent(cartItemRepository::deleteByUser);
    }
}
