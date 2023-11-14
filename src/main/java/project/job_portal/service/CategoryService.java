package project.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import project.job_portal.model.Category;
import project.job_portal.model.Recruitment;
import project.job_portal.repository.CategoryRepository;
import project.job_portal.repository.RecruitmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    private final RecruitmentRepository recruitmentRepository;

    public List<Category> getList() {
        return repository.findCategoriesByRecruitmentsCount(PageRequest.of(0, 3));
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public List<Recruitment> getJobs(Long id) {
        return recruitmentRepository.findRecruitmentsByCategoryId(id);
    }

    public Category getCategory(Long id) {
        return repository.findCategoryById(id);
    }

}
