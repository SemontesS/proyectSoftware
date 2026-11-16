package application.adapters.rest.dtos.requests;

/** Signed correction applied to the available stock; zero is rejected by the domain. */
public record AdjustmentRequestDTO(int delta) {}
