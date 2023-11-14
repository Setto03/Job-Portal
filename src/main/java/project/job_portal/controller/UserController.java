package project.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.job_portal.dto.UserUpdateDto;
import project.job_portal.model.FileEntity;
import project.job_portal.model.User;
import project.job_portal.service.FileService;
import project.job_portal.service.UserService;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FileService fileService;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email);
    }

    @GetMapping("/u/info")
    public String userInfo(Model model) {
        User user = getAuthenticatedUser();
        FileEntity file = fileService.getFileByUser(user);

        model.addAttribute("user", user);
        if (file != null) {
            model.addAttribute("fileName", file.getName());
        }

        return "userInfo";
    }

    @GetMapping("/u/update")
    public String updateForm(Model model) {
        User user = getAuthenticatedUser();

        var dto = UserUpdateDto.builder()
                .username(user.getName())
                .fullname(user.getFullname())
                .address(user.getAddress())
                .phone(user.getPhone())
                .description(user.getDescription())
                .build();

        model.addAttribute("user", dto);
        model.addAttribute("warning", "Cập nhật thông tin cá nhân trước khi tiếp tục");

        return "userUpdate";
    }

    @PostMapping("/u/update/process")
    public String processUpdate(@ModelAttribute("user") @Valid UserUpdateDto user,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasFieldErrors("fullname")) {
            model.addAttribute("fullnameErr", "Họ và tên không được để trống");
            return "userUpdate";
        }

        if (bindingResult.hasFieldErrors("phone")) {
            model.addAttribute("phoneErr", "Số điện thoại không hợp lệ");
            return "userUpdate";
        }

        userService.updateUser(user);
        return "redirect:/u/info";
    }

    @GetMapping("/u/upload")
    public String uploadFile(Model model) {
        User user = getAuthenticatedUser();

        model.addAttribute("user", user);

        return "fileUpload";
    }

    @PostMapping("/u/upload/process")
    public String processUploadFile(@RequestParam(name = "file") MultipartFile file,
                                    @RequestParam(name = "id") int id,
                                    Model model) throws IOException {

        fileService.saveFile(file, id);

        model.addAttribute("fileName", file.getOriginalFilename());

        return "redirect:/u/info";
    }
}
