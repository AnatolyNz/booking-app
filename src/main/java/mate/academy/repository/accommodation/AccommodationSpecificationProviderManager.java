package mate.academy.repository.accommodation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.exception.SpecificationProviderNotFoundException;
import mate.academy.model.Accommodation;
import mate.academy.repository.SpecificationProvider;
import mate.academy.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationProviderManager implements
        SpecificationProviderManager<Accommodation> {
    private final List<SpecificationProvider<Accommodation>> accommodationSpecificationProviders;

    @Override
    public SpecificationProvider<Accommodation> getSpecificationProvider(String key) {
        return accommodationSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(()
                        -> new SpecificationProviderNotFoundException(
                                "Can't find correct specification "
                        + "provider for key " + key));
    }
}
