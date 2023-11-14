package project.job_portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {

    private String username;

    @NotEmpty
    @NotNull
    private String fullname;

    private String address;

    @NotEmpty
    @NotNull
    @Size(min = 10, max = 10)
    private String phone;

    private String description;
}
