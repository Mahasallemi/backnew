package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjetDTO {
    private String nomProjet;
    private String nomChefProjet;
    private String description;
    
    // Nouvelle approche avec ComponentDTO pour une meilleure gestion des types
    @JsonProperty("components")
    private Object components; // Temporaire pour la compatibilité - sera converti en ComponentDTO
    
    private Date date;
    private float budget;
    
    // Méthodes utilitaires améliorées pour gérer les deux formats de manière plus sûre
    public ComponentDTO getComponentsDTO() {
        ComponentDTO componentDTO = new ComponentDTO();
        
        if (components instanceof Map) {
            try {
                componentDTO.setMap((Map<String, Integer>) components);
            } catch (ClassCastException e) {
                System.err.println("Erreur de cast pour Map: " + e.getMessage());
            }
        } else if (components instanceof List) {
            try {
                List<?> rawList = (List<?>) components;
                // Vérification de sécurité pour s'assurer que tous les éléments sont des String
                List<String> stringList = rawList.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
                componentDTO.setList(stringList);
                
                if (stringList.size() != rawList.size()) {
                    System.err.println("Attention: Certains éléments de la liste ne sont pas des String et ont été ignorés");
                }
            } catch (ClassCastException e) {
                System.err.println("Erreur de cast pour List: " + e.getMessage());
            }
        }
        
        return componentDTO;
    }
    
    // Méthodes de compatibilité (dépréciées mais maintenues pour la transition)
    @Deprecated
    public Map<String, Integer> getComponentsAsMap() {
        ComponentDTO dto = getComponentsDTO();
        return dto.isMap() ? dto.getMap() : null;
    }
    
    @Deprecated
    public List<String> getComponentsAsList() {
        ComponentDTO dto = getComponentsDTO();
        return dto.isList() ? dto.getList() : null;
    }
}
