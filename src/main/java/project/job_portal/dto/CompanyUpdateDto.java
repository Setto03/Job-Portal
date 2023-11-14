package project.job_portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyUpdateDto {

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String address;

    @NotEmpty
    @NotNull
    @Size(min = 9)
    private String phone;

    private String description;

}
