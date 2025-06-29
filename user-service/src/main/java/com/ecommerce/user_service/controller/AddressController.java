package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.AddressDTO;
import com.ecommerce.user_service.dto.CreateAddressRequest;
import com.ecommerce.user_service.dto.UserDTO;
import com.ecommerce.user_service.service.AddressService;
import com.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Slf4j
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody CreateAddressRequest request) {
        log.info("REST request to create address");
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        AddressDTO address = addressService.createAddress(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("REST request to get user addresses");
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        List<AddressDTO> addresses = addressService.getUserAddresses(user.getId());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long id) {
        log.debug("REST request to get address: {}", id);
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        AddressDTO address = addressService.getAddress(user.getId(), id);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody CreateAddressRequest request) {
        log.info("REST request to update address: {}", id);
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        AddressDTO address = addressService.updateAddress(user.getId(), id, request);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        log.info("REST request to delete address: {}", id);
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        addressService.deleteAddress(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long id) {
        log.info("REST request to set default address: {}", id);
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        addressService.setDefaultAddress(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}