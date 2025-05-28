package mate.academy.dto.accommodation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import mate.academy.model.Accommodation;

@Data
public class CreateAccommodationRequestDto {
    @NotBlank(message = "Location must not be blank")
    private String location;

    @NotBlank(message = "Size must not be blank")
    private String size;

    @NotEmpty(message = "Amenities list cannot be empty")
    private List<String> amenities;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Availability is required")
    @Min(value = 1, message = "Availability must be at least 1")
    private Integer availability;

    @NotNull(message = "Daily rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily rate must be greater than 0")
    private BigDecimal dailyRate;

    @NotNull(message = "Type is required")
    private Accommodation.Type type;
}
