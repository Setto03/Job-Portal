package project.job_portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto {

    @Email
    @NotEmpty
    @NotEmpty
    private String email;

    @NotEmpty
    @NotNull
    private String password;
}
