package mate.academy.dto.accommodation;

public record AccommodationSearchParameters(
        String[] types,
        String[] locations,
        String[] sizes,
        String[] availabilities) {
}
