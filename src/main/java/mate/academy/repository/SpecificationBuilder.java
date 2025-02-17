package mate.academy.repository;

import mate.academy.dto.AccommodationSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(AccommodationSearchParameters searchParameters);
}
