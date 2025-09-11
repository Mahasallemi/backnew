# Guide d'utilisation ComponentDTO

## Vue d'ensemble

La classe `ComponentDTO` a été créée pour améliorer la gestion des composants dans les projets. Elle remplace l'ancienne approche basée sur `Object` par une solution plus sûre et typée.

## Formats supportés

### 1. Format Liste Simple
```json
{
  "nomProjet": "Projet Maintenance",
  "nomChefProjet": "Anis Mdaini",
  "description": "Projet test",
  "components": ["COMP001", "COMP002", "COMP003"],
  "date": "2025-07-29T00:00:00.000+01:00",
  "budget": 15000
}
```

### 2. Format Map avec Quantités
```json
{
  "nomProjet": "Projet Production",
  "nomChefProjet": "Sarah Ben Ali",
  "description": "Projet avec quantités spécifiques",
  "components": {
    "COMP001": 5,
    "COMP002": 3,
    "COMP003": 10
  },
  "date": "2025-07-29T00:00:00.000+01:00",
  "budget": 25000
}
```

## Améliorations apportées

### 1. Sécurité des types
- Vérification automatique des types avant le cast
- Filtrage des éléments non-String dans les listes
- Gestion des erreurs de cast avec messages explicites

### 2. Logging amélioré
- Messages détaillés pour le debugging
- Distinction entre composants trouvés et non trouvés
- Gestion des erreurs avec stack trace

### 3. Compatibilité
- Les anciennes méthodes `getComponentsAsMap()` et `getComponentsAsList()` sont maintenues mais dépréciées
- Migration transparente vers la nouvelle approche

## Utilisation dans le code

### Dans ProjetDTO
```java
// Nouvelle méthode recommandée
ComponentDTO componentDTO = projetDTO.getComponentsDTO();

// Vérification du type
if (componentDTO.isMap()) {
    Map<String, Integer> map = componentDTO.getMap();
    // Traitement avec quantités
} else if (componentDTO.isList()) {
    List<String> list = componentDTO.getList();
    // Traitement simple
}
```

### Dans ProjectService
```java
ComponentDTO componentDTO = projetDTO.getComponentsDTO();

if (componentDTO.isMap()) {
    // Traitement Map avec gestion d'erreur
    components = componentsMap.keySet().stream()
        .map(trartArticle -> {
            try {
                Component comp = componentService.findByTrartArticle(trartArticle);
                // Logging et traitement
                return comp;
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
                return null;
            }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}
```

## Tests recommandés

### Test avec liste simple
```bash
curl -X POST http://localhost:8089/PI/projects/add \
  -H "Content-Type: application/json" \
  -d '{
    "nomProjet": "Test Liste",
    "nomChefProjet": "Test User",
    "description": "Test",
    "components": ["COMP001", "COMP002"],
    "date": "2025-07-29T00:00:00.000+01:00",
    "budget": 1000
  }'
```

### Test avec map quantités
```bash
curl -X POST http://localhost:8089/PI/projects/add \
  -H "Content-Type: application/json" \
  -d '{
    "nomProjet": "Test Map",
    "nomChefProjet": "Test User",
    "description": "Test",
    "components": {"COMP001": 5, "COMP002": 3},
    "date": "2025-07-29T00:00:00.000+01:00",
    "budget": 2000
  }'
```

## Migration

Les anciennes méthodes sont maintenues pour la compatibilité mais sont marquées comme `@Deprecated`. Il est recommandé de migrer vers `getComponentsDTO()` pour bénéficier des améliorations de sécurité et de performance.
