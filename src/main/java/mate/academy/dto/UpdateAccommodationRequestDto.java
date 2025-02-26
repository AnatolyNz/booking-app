package mate.academy.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import mate.academy.exception.model.Accommodation;

@Data
public class UpdateAccommodationRequestDto {

    private String location;
    private String size;
    private List<String> amenities;
    private BigDecimal price;
    private Integer availability;
    private BigDecimal dailyRate;
    private Accommodation.Type type;
}

