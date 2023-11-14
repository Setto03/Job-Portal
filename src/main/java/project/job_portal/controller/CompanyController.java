package project.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.job_portal.dto.CompanyUpdateDto;
import project.job_portal.dto.RecruitmentDto;
import project.job_portal.model.Category;
import project.job_portal.model.Company;
import project.job_portal.model.Recruitment;
import project.job_portal.service.CategoryService;
import project.job_portal.service.CompanyService;
import project.job_portal.service.RecruitmentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CategoryService categoryService;

    private final RecruitmentService recruitmentService;

    @GetMapping("/c/info")
    public String companyInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Company company = companyService.findCompanyByEmail(email);
        model.addAttribute("company", company);

        return "companyInfo";
    }

    @GetMapping("/c/list")
    public String companyJobList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Company company = companyService.findCompanyByEmail(authentication.getName());

        model.addAttribute("results", company.getRecruitments());


        return "jobSearch";
    }

    @GetMapping("/c/update")
    public String updateCompanyForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Company company = companyService.findCompanyByEmail(auth.getName());

        // Tạo đối tượng DTO tương ứng để nhận thông tin
        var dto = CompanyUpdateDto.builder()
                .name(company.getName())
                .address(company.getAddress())
                .phone(company.getPhone())
                .description(company.getDescription())
                .build();

        model.addAttribute("company", dto);
        return "companyUpdate";
    }

    @PostMapping("/c/update/process")
    public String processCompanyUpdate(@Valid @ModelAttribute("company") CompanyUpdateDto dto,
                                       BindingResult bindingResult,
                                       Model model) {

        // Kiểm tra lỗi input của các field
        if (bindingResult.hasFieldErrors("name")) {
            model.addAttribute("nameErr", "Tên công ty không dược để trống");
            return "companyUpdate";
        }
        if (bindingResult.hasFieldErrors("address")) {
            model.addAttribute("addressErr", "Địa chỉ không được để trống");
            return "companyUpdate";
        }
        if (bindingResult.hasFieldErrors("phone")) {
            model.addAttribute("phoneErr", "Số điện thoại không hợp lệ");
            return "companyUpdate";
        }

        companyService.updateCompany(dto);
        return "redirect:/c/info";
    }

    @RequestMapping("/info")
    public String companyInfo(@RequestParam(name = "id") Long id, Model model) {
        Company company = companyService.findCompanyById(id);

        model.addAttribute("company", company);

        return "companyInfo";
    }

    @GetMapping("/job/create")
    public String createJob(Model model) {
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("job", new RecruitmentDto());
        model.addAttribute("categories", categories);

        return "jobCreate";
    }

    @PostMapping("/job/create/process")
    public String processJob(@ModelAttribute("job") @Valid RecruitmentDto dto,
                             @RequestParam(name = "categoryId") Long id,
                             Model model,
                             BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("title")) {
            model.addAttribute("titleErr", "Must have a name");

            return "jobCreate";
        }
        if (bindingResult.hasFieldErrors("description")) {
            model.addAttribute("descriptionErr", "Must have description and job requirement");

            return "jobCreate";
        }
        if (bindingResult.hasFieldErrors("location")) {
            model.addAttribute("locationErr", "Must have a job location");

            return "jobCreate";
        }
        if (id == null) {
            model.addAttribute("idErr", "Must select a category");

            return "jobCreate";
        }

        companyService.createJob(dto, id);

        return "redirect:/c/list";
    }

    @RequestMapping("/job/delete")
    public String deleteJob(@RequestParam(name = "id") Long id) {
        companyService.removeJob(id);

        return "redirect:/c/list";
    }

    @GetMapping("/job/update")
    public String updateJob(@RequestParam(name = "id") Long id,
                            Model model) {
        Recruitment recruitment = recruitmentService.findRecruitmentById(id);
        List<Category> categories = categoryService.getAllCategories();

        var dto = RecruitmentDto.builder()
                .title(recruitment.getTitle())
                .salary(recruitment.getSalary())
                .experience(recruitment.getExperience())
                .quantity(recruitment.getQuantity())
                .description(recruitment.getDescription())
                .location(recruitment.getLocation())
                .build();

        model.addAttribute("job", dto);
        model.addAttribute("categories", categories);
        model.addAttribute("jobId", id);

        return "jobUpdate";
    }

    @PostMapping("/job/update/process")
    public String updateJob(@ModelAttribute("job") @Valid RecruitmentDto dto,
                             @RequestParam(name = "categoryId") Long categoryId,
                             @RequestParam(name = "jobId") Long jobId,
                             Model model,
                             BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors("title")) {
            model.addAttribute("titleErr", "Phải có tiêu đề");

            return "jobUpdate";
        }
        if (bindingResult.hasFieldErrors("description")) {
            model.addAttribute("descriptionErr", "Mô tả công việc và yêu cầu cho ứng viên");

            return "jobUpdate";
        }
        if (bindingResult.hasFieldErrors("location")) {
            model.addAttribute("locationErr", "Địa điểm làm việc không được để trống");

            return "jobUpdate";
        }
        if (categoryId == null) {
            model.addAttribute("categoryErr", "Chọn danh mục công việc");

            return "jobUpdate";
        }

        companyService.updateJob(dto, categoryId, jobId);

        return "redirect:/c/list";
    }

}
