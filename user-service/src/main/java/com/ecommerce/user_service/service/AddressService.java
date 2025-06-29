package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.AddressDTO;
import com.ecommerce.user_service.dto.CreateAddressRequest;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(Long userId, CreateAddressRequest request);
    AddressDTO updateAddress(Long userId, Long addressId, CreateAddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    List<AddressDTO> getUserAddresses(Long userId);
    AddressDTO getAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
}