package mate.academy.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AccommodationDto;
import mate.academy.dto.AccommodationSearchParameters;
import mate.academy.dto.CreateAccommodationRequestDto;
import mate.academy.dto.UpdateAccommodationRequestDto;
import mate.academy.exception.model.Accommodation;
import mate.academy.mapper.AccommodationMapper;
import mate.academy.repository.accommodation.AccommodationRepository;
import mate.academy.repository.accommodation.AccommodationSpecificationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto createAccommodationRequestDto) {
        Accommodation accommodation = accommodationMapper.toModel(createAccommodationRequestDto);
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationMapper.toDto(accommodationRepository.getAccommodationById(id)
                .orElseThrow(() -> new
                        EntityNotFoundException("Can't find accommodation with id " + id)));
    }

    @Override
    public void updateById(Long id, UpdateAccommodationRequestDto updateAccommodationRequestDto) {
        Accommodation accommodation = accommodationMapper
                .toModel(new CreateAccommodationRequestDto());
        accommodation.setId(id);
        accommodationRepository.save(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }

    @Override
    public List<AccommodationDto> search(AccommodationSearchParameters params) {
        Specification<Accommodation> bookSpecification =
                accommodationSpecificationBuilder.build(params);
        return accommodationRepository.findAll(bookSpecification)
                .stream()
                .map(accommodationMapper::toDto)
                .toList();
    }
}
