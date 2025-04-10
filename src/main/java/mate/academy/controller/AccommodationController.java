package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.service.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping
    @Operation(summary = "Get all accommodation",
            description = "Get a list of all available accoommodations")
    public List findAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public AccommodationDto getBookById(@PathVariable Long id) {
        return accommodationService.getAccommodationById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new accommodation")
    public AccommodationDto createBook(@RequestBody @Valid
                                           CreateAccommodationRequestDto accommodationDto) {
        return accommodationService.save(accommodationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public void updateAccommodation(@PathVariable Long id,
                           @RequestBody UpdateAccommodationRequestDto
                                   updateAccommodationRequestDto) {
        accommodationService.updateById(id, updateAccommodationRequestDto);
    }
}
