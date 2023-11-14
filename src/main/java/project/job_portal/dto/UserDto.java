package project.job_portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @Email
    @NotNull
    @NotEmpty
    private String email;

    @Size(min = 6, message = "Mật khẩu dài ít nhất 6 kí tự")
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;

    private String matchingPassword;
}
