package project.job_portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import project.job_portal.dto.LoginDto;
import project.job_portal.service.CategoryService;
import project.job_portal.service.CompanyService;
import project.job_portal.service.RecruitmentService;
import project.job_portal.service.UserService;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    private final UserService userService;
    private final CompanyService companyService;
    private final RecruitmentService recruitmentService;
    private final CategoryService categoryService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/home")
    public String mainPage(Model model) {
        model.addAttribute("userCount", userService.userCount());
        model.addAttribute("companyCount", companyService.companyCount());
        model.addAttribute("jobCount", recruitmentService.jobCount());

        model.addAttribute("companies", companyService.getList());
        model.addAttribute("recruitments", recruitmentService.getList());
        model.addAttribute("categories", categoryService.getList());

        return "index";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginDto());

        return "login";
    }

    @PostMapping("/login/process")
    public String login(@ModelAttribute(name = "form") @Valid LoginDto dto,
                        BindingResult result,
                        Model model) {
        if (result.hasFieldErrors("email") || result.hasFieldErrors("password")) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");

            return "login";
        }

        try {
            userService.login(dto, request);

            return "redirect:/home";
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");

            return "login";
        }
    }

}
