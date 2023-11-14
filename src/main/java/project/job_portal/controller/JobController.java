package project.job_portal.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.job_portal.model.Company;
import project.job_portal.model.FileEntity;
import project.job_portal.model.Recruitment;
import project.job_portal.model.User;
import project.job_portal.service.FileService;
import project.job_portal.service.RecruitmentService;
import project.job_portal.service.UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final RecruitmentService recruitmentService;

    private final UserService userService;

    private final FileService fileService;

    @Autowired
    private HttpServletResponse response;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findUserByEmail(email);
    }

    @RequestMapping("/search/company")
    public String searchCompany(@RequestParam String query,
                                Model model) {
        try {
            List<Recruitment> result = recruitmentService.searchByCompany(query);

            model.addAttribute("results", result);

            return "jobSearch";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Không tìm thấy công việc phù hợp");
            return "redirect:/home";
        }
    }

    @RequestMapping("/search/keyword")
    public String searchKeyword(@RequestParam String query,
                                Model model) {
        List<Recruitment> result = recruitmentService.searchByKeyword(query);

        model.addAttribute("results", result);

        return "jobSearch";
    }

    @RequestMapping("/search/location")
    public String searchLocation(@RequestParam String query,
                                 Model model) {
        List<Recruitment> result = recruitmentService.searchByLocation(query);

        model.addAttribute("results", result);

        return "jobSearch";
    }

    @GetMapping("/job/list")
    public String jobListing(Model model) {
        List<Recruitment> list = recruitmentService.getList();
        model.addAttribute("results", list);

        return "jobSearch";
    }


    @GetMapping("/job/info")
    public String jobInfo(@RequestParam(name = "id") Long id,
                          Model model) {
        Recruitment recruitment = recruitmentService.findRecruitmentById(id);
        List<User> applicants = recruitment.getApplicants();
        Company company = recruitment.getCompany();

        model.addAttribute("job", recruitment);
        model.addAttribute("company", company);
        model.addAttribute("applicants", applicants);

        return "jobInfo";
    }

    @RequestMapping("/job/upload")
    public String uploadCV(@ModelAttribute("id") Long id,
                                   Model model) {
        User user = getAuthenticatedUser();
        Recruitment recruitment = recruitmentService.findRecruitmentById(id);

        if (fileService.getFileByUser(user) == null) {
            model.addAttribute("noCV", "Bạn chưa cập nhật CV");
            model.addAttribute("job", recruitment);

            return "jobInfo";
        }
        if (recruitment.getApplicants().contains(user)) {
            model.addAttribute("yesCV", "Bạn đã nộp CV");
            model.addAttribute("job", recruitment);

            return "jobInfo";
        }

        recruitmentService.sendApply(user, recruitment);

        model.addAttribute("success", "Nộp CV thành công");

        return "redirect:/job/list";
    }

    @GetMapping("/job/applicant/info")
    public String getApplicantInfo(@RequestParam(name = "id") int id,
                                   Model model) {

        User user = userService.findUserById(id);
        FileEntity file = fileService.getFileByUser(user);

        Path filePath = Paths.get(fileService.getDirectory() + "/" + file.getName());
        String encodedFilePath = URLEncoder.encode(filePath.toString(), StandardCharsets.UTF_8);
        model.addAttribute("path", encodedFilePath);

        model.addAttribute("user", user);
        model.addAttribute("file", file);

        return "userApplicantInfo";
    }

    @RequestMapping("/download")
    public void downloadFile(@RequestParam(name = "name") String fileName) {
        try {
            Path filePath = Paths.get(fileService.getDirectory() + "/" + fileName);

            byte[] fileBytes = Files.readAllBytes(filePath);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ServletOutputStream writer = response.getOutputStream();
            writer.write(fileBytes);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
