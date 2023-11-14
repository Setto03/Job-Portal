package project.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.job_portal.dto.CompanyDto;
import project.job_portal.dto.UserDto;
import project.job_portal.service.CompanyService;
import project.job_portal.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    private final CompanyService companyService;

    @GetMapping("/red")
    public String redirect() {
        return "redirect";
    }

    @RequestMapping("/u")
    public String registerUser(Model model) {
        model.addAttribute("user", new UserDto());

        return "userRegister";
    }

    @PostMapping("/u/process")
    public String processUser(@ModelAttribute(name = "user") @Valid UserDto userDto,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasFieldErrors("emailErr")) {
            model.addAttribute("emailErr", "Email không hợp lệ");

            return "userRegister";
        }

        if (bindingResult.hasFieldErrors("password")) {
            model.addAttribute("passwordErr", "Mật khẩu dài ít nhất 6 kí tự");

            return "userRegister";
        }

        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            model.addAttribute("notMatched", "Mật khẩu nhập lại không trùng khớp");

            return "userRegister";
        }

        try {
            userService.registerUser(userDto);

            return "redirect:/u/update";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Người dùng đã tồn tại");

            return "userRegister";
        }
    }

    @RequestMapping("/c")
    public String registerUserCompany(Model model) {
        model.addAttribute("company", new CompanyDto());

        return "companyRegister";
    }

    @PostMapping("/c/process")
    public String processUserCompany(@ModelAttribute("company") @Valid CompanyDto dto,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasFieldErrors("email")) {
            model.addAttribute("emailErr", "Email không hợp lệ");

            return "companyRegister";
        }

        if (bindingResult.hasFieldErrors("password")) {
            model.addAttribute("passwordErr", "Mật khẩu dài ít nhất 6 kí tự");

            return "companyRegister";
        }
        if (!dto.getPassword().equals(dto.getMatchingPassword())) {
            model.addAttribute("notMatched", "Mật khẩu nhập lại không trùng khớp");

            return "companyRegister";
        }

        try {
            companyService.registerCompany(dto);

            return "redirect:/c/update";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Email đã được đăng ký");

            return "companyRegister";
        }
    }

}
