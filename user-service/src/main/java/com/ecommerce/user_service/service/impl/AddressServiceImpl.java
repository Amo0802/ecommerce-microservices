package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.AddressDTO;
import com.ecommerce.user_service.dto.CreateAddressRequest;
import com.ecommerce.user_service.entity.Address;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.AddressNotFoundException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.mapper.AddressMapper;
import com.ecommerce.user_service.repository.AddressRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressDTO createAddress(Long userId, CreateAddressRequest request) {
        log.info("Creating address for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // If this is the first address or set as default, make it default
        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        boolean shouldBeDefault = existingAddresses.isEmpty() || request.getIsDefault();

        // If setting as default, unset other defaults
        if (shouldBeDefault) {
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        Address address = Address.builder()
                .user(user)
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .type(request.getType())
                .isDefault(shouldBeDefault)
                .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", savedAddress.getId());

        return addressMapper.toDTO(savedAddress);
    }

    @Override
    public AddressDTO updateAddress(Long userId, Long addressId, CreateAddressRequest request) {
        log.info("Updating address {} for user: {}", addressId, userId);

        Address address = getAddressForUser(userId, addressId);

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setType(request.getType());

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        log.info("Deleting address {} for user: {}", addressId, userId);

        Address address = getAddressForUser(userId, addressId);
        boolean wasDefault = address.getIsDefault();

        addressRepository.delete(address);

        // If deleted address was default, set another as default
        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByUserId(userId);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(Long userId) {
        log.debug("Fetching addresses for user: {}", userId);
        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddress(Long userId, Long addressId) {
        log.debug("Fetching address {} for user: {}", addressId, userId);
        Address address = getAddressForUser(userId, addressId);
        return addressMapper.toDTO(address);
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("Setting default address {} for user: {}", addressId, userId);

        Address address = getAddressForUser(userId, addressId);

        // Unset current default
        List<Address> currentDefaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        currentDefaults.forEach(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });

        // Set new default
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private Address getAddressForUser(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException("Address not found for user");
        }

        return address;
    }
}