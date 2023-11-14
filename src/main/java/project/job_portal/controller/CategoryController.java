package project.job_portal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.job_portal.model.Category;
import project.job_portal.model.Recruitment;
import project.job_portal.service.CategoryService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @RequestMapping("/category/list")
    public String categoryJobList(@RequestParam(name = "id") Long id,
                                  Model model) {
        List<Recruitment> recruitments = categoryService.getJobs(id);
        Category category = categoryService.getCategory(id);

        model.addAttribute("recruitments", recruitments);
        model.addAttribute("category", category);

        return "categoryJobList";
    }
}
