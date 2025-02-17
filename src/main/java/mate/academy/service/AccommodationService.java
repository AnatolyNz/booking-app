package mate.academy.service;

import java.util.List;
import mate.academy.dto.AccommodationDto;
import mate.academy.dto.AccommodationSearchParameters;
import mate.academy.dto.CreateAccommodationRequestDto;
import mate.academy.dto.UpdateAccommodationRequestDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {

    AccommodationDto save(CreateAccommodationRequestDto createAccommodationRequestDto);

    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto getAccommodationById(Long id);

    void updateById(Long id, UpdateAccommodationRequestDto updateAccommodationRequestDto);

    void deleteById(Long id);

    List<AccommodationDto> search(AccommodationSearchParameters params);
}
