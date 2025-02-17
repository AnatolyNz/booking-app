package mate.academy.dto;

public record AccommodationSearchParameters(
        String[] types,
        String[] locations,
        String[] sizes,
        String[] availabilities) {
}
