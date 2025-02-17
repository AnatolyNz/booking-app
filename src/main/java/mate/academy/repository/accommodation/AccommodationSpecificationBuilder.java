package mate.academy.repository.accommodation;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AccommodationSearchParameters;
import mate.academy.model.Accommodation;
import mate.academy.repository.SpecificationBuilder;
import mate.academy.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationBuilder implements SpecificationBuilder<Accommodation> {

    private static final String TYPE_OUT_PARAM = "type";
    private static final String LOCATION_OUT_PARAM = "location";
    private static final String SIZE_OUT_PARAM = "size";
    private static final String AVAILABILITY_OUT_PARAM = "availability";

    private final SpecificationProviderManager<Accommodation>
            accommodationSpecificationProviderManager;

    @Override
    public Specification<Accommodation> build(AccommodationSearchParameters searchParameters) {
        Specification<Accommodation> spec = Specification.where(null);

        if (Objects.nonNull(searchParameters.types()) && searchParameters.types().length > 0) {
            spec = spec.and(accommodationSpecificationProviderManager
                    .getSpecificationProvider(TYPE_OUT_PARAM)
                    .getSpecification(searchParameters.types()));
        }

        if (searchParameters.locations() != null && searchParameters.locations().length > 0) {
            spec = spec.and(accommodationSpecificationProviderManager
                    .getSpecificationProvider(LOCATION_OUT_PARAM)
                    .getSpecification(searchParameters.locations()));
        }

        if (searchParameters.sizes() != null && searchParameters.sizes().length > 0) {
            spec = spec.and(accommodationSpecificationProviderManager
                    .getSpecificationProvider(SIZE_OUT_PARAM)
                    .getSpecification(searchParameters.sizes()));
        }

        if (searchParameters.availabilities() != null
                && searchParameters.availabilities().length > 0) {
            spec = spec.and(accommodationSpecificationProviderManager
                    .getSpecificationProvider(AVAILABILITY_OUT_PARAM)
                    .getSpecification(searchParameters.availabilities()));
        }

        return spec;
    }
}
