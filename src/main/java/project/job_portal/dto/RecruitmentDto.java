package project.job_portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentDto {

    @NotNull
    @NotEmpty
    private String title;

    @NotEmpty
    @NotNull
    private String description;

    private String experience;

    @NotNull
    @NotEmpty
    private String location;

    private int quantity;

    private String salary;


}
