package project.job_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.job_portal.model.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

    @Query("select f from FileEntity f where f.user.id = ?1")
    FileEntity getFileEntityByUserId(int id);


}
