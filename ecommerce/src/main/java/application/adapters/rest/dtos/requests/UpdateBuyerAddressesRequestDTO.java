package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.*;
import jakarta.validation.Valid;

import java.util.List;

/** Both fields are optional; at least one must be present. */
public record UpdateBuyerAddressesRequestDTO(@Valid AddressDTO primaryAddress,
        List<@Valid AddressDTO> additionalAddresses) {}
