package project.job_portal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recruitment")
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "experience")
    private String experience;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "salary")
    private String salary;

    @Column(name = "status")
    private String status;

    @Column(name = "location")
    private String location;

    @Column(name = "created")
    private String created;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "Application",
            joinColumns = @JoinColumn(name = "recruitment"),
            inverseJoinColumns = @JoinColumn(name = "user")
    )
    private List<User> applicants;
}
