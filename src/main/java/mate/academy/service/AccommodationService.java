package mate.academy.service;

import java.util.List;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.AccommodationSearchParameters;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.accommodation.UpdateAccommodationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {

    AccommodationDto save(CreateAccommodationRequestDto createAccommodationRequestDto);

    Page<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto getAccommodationById(Long id);

    AccommodationDto updateById(Long id, UpdateAccommodationRequestDto
            updateAccommodationRequestDto);

    void deleteById(Long id);

    List<AccommodationDto> search(AccommodationSearchParameters params);
}
