package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.Project;

@Repository
public interface ProjectRepository  extends JpaRepository<Project,Long> {

}
