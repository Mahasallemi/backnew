package tn.esprit.PI.Services;


import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.PI.entity.Component;
import tn.esprit.PI.entity.ComponentDTO;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.ProjectRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ComponentRp componentRepository;

    @Autowired
    private ComponentService componentService;  // Inject the ComponentService

    // Nouvelle méthode pour créer un projet à partir d'un DTO
    public Project createProjetFromDTO(ProjetDTO projetDTO) {
        Project projet = new Project();
        projet.setProjectName(projetDTO.getNomProjet());
        projet.setProjectManagerName(projetDTO.getNomChefProjet());
        projet.setDescription(projetDTO.getDescription());
        projet.setDate(projetDTO.getDate());
        projet.setBudget(projetDTO.getBudget());
        
        // Gérer les composants avec ComponentDTO (approche améliorée et sécurisée)
        List<Component> components;
        
        ComponentDTO componentDTO = projetDTO.getComponentsDTO();
        
        if (componentDTO.isMap()) {
            // Format 1: Map avec quantités {"COMP001": 5, "COMP002": 3}
            System.out.println("Format détecté: Map avec quantités");
            Map<String, Integer> componentsMap = componentDTO.getMap();
            
            components = componentsMap.keySet().stream()
                    .map(trartArticle -> {
                        try {
                            Component comp = componentService.findByTrartArticle(trartArticle);
                            if (comp != null) {
                                System.out.println("Composant trouvé: " + trartArticle + ", Quantité: " + componentsMap.get(trartArticle));
                            } else {
                                System.out.println("Composant non trouvé: " + trartArticle);
                            }
                            return comp;
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la recherche du composant " + trartArticle + ": " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(comp -> comp != null)
                    .collect(Collectors.toList());
                    
        } else if (componentDTO.isList()) {
            // Format 2: Liste simple ["COMP001", "COMP002"]
            System.out.println("Format détecté: Liste simple");
            List<String> componentsList = componentDTO.getList();
            
            components = componentsList.stream()
                    .map(trartArticle -> {
                        try {
                            Component comp = componentService.findByTrartArticle(trartArticle);
                            if (comp != null) {
                                System.out.println("Composant trouvé: " + trartArticle + ", Quantité: 1 (par défaut)");
                            } else {
                                System.out.println("Composant non trouvé: " + trartArticle);
                            }
                            return comp;
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la recherche du composant " + trartArticle + ": " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(comp -> comp != null)
                    .collect(Collectors.toList());
                    
        } else {
            System.out.println("Aucun composant fourni ou format non reconnu");
            components = List.of();
        }

        projet.setComponents(components);
        return projectRepository.save(projet);
    }

    public List<Project> getAllProjets() {
        return projectRepository.findAll();  // Find all projects from the repository
    }

    public Project addComponentToProject(Long projectId, String componentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Component component = componentRepository.findById(componentId)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        if (project.getComponents().contains(component)) {
            // Le composant est déjà associé au projet
            return project;
        }

        project.getComponents().add(component);  // Utilise l'entité Component, non l'annotation Spring
        return projectRepository.save(project);
    }

}
