package tn.esprit.PI.RestControlleur;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.ProjectService;
import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;

import java.util.List;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
@PreAuthorize("permitAll()")


public class ProjectController {
    private final ProjectService projectService;



    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/all")
    @Operation(summary = "Récupérer tous les projets")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjets();
        return ResponseEntity.ok(projects);
    }
    // Créer un projet

    @PostMapping("/add")
    public ResponseEntity<Project> createProjet(@RequestBody ProjetDTO projetDTO) {
        try {
            Project createdProjet = projectService.createProjetFromDTO(projetDTO);
            return ResponseEntity.ok(createdProjet);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build(); // Retourner 400 en cas d'erreur
        }
    }


    @PutMapping("/{projectId}/addComponent/{componentId}")
    public ResponseEntity<Project> addComponentToProject(@PathVariable Long projectId, @PathVariable String componentId) {
        Project updatedProject = projectService.addComponentToProject(projectId, componentId);
        return ResponseEntity.ok(updatedProject);
    }

}
