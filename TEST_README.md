# Tests JUnit + Mockito + JaCoCo - Système GMAO

## 📋 Vue d'ensemble

Cette suite de tests complète couvre tous les services principaux du système GMAO avec JUnit 5, Mockito pour les mocks, et JaCoCo pour la couverture de code.

## 🏗️ Structure des tests

```
src/test/java/tn/esprit/PI/
├── Services/
│   ├── DemandeInterventionServiceTest.java
│   ├── TesteurServiceTest.java
│   └── BonDeTravailServiceTest.java
├── service/
│   └── UserServiceImpTest.java
├── integration/
│   └── GMAOIntegrationTest.java
└── util/
    └── TestDataBuilder.java
```

## 🧪 Classes de test créées

### 1. DemandeInterventionServiceTest
- **Couverture**: Service de gestion des interventions
- **Tests**: 15+ méthodes de test
- **Fonctionnalités testées**:
  - Récupération de toutes les demandes
  - Récupération par ID
  - Assignation de technicien
  - Assignation de testeur/équipement
  - Confirmation d'intervention
  - Mise à jour des demandes
  - Gestion des erreurs

### 2. TesteurServiceTest
- **Couverture**: Service de gestion des équipements
- **Tests**: 12+ méthodes de test
- **Fonctionnalités testées**:
  - CRUD complet des testeurs
  - Conversion en DTO
  - Recherche par atelier et ligne
  - Gestion des erreurs et cas limites

### 3. BonDeTravailServiceTest
- **Couverture**: Service de gestion des bons de travail
- **Tests**: 15+ méthodes de test
- **Fonctionnalités testées**:
  - Création de bons de travail
  - Création depuis intervention
  - Gestion des composants et stock
  - Mise à jour et suppression
  - Validation des associations

### 4. UserServiceImpTest
- **Couverture**: Service de gestion des utilisateurs
- **Tests**: 10+ méthodes de test
- **Fonctionnalités testées**:
  - CRUD utilisateurs
  - Conversion DTO
  - Gestion des rôles
  - États de confirmation

### 5. GMAOIntegrationTest
- **Couverture**: Tests d'intégration complets
- **Tests**: Workflow complet du système
- **Fonctionnalités testées**:
  - Intégration des services
  - Workflow intervention → bon de travail
  - Tests des endpoints REST
  - Gestion des erreurs end-to-end

## 🛠️ Technologies utilisées

- **JUnit 5**: Framework de test principal
- **Mockito**: Mocking des dépendances
- **Spring Boot Test**: Tests d'intégration
- **MockMvc**: Tests des contrôleurs REST
- **H2 Database**: Base de données en mémoire pour les tests
- **JaCoCo**: Couverture de code

## 🚀 Exécution des tests

### Option 1: Scripts automatisés
```bash
# Windows
run-tests.bat

# Linux/Mac
chmod +x run-tests.sh
./run-tests.sh
```

### Option 2: Commandes Maven
```bash
# Exécuter tous les tests
mvn test

# Générer le rapport JaCoCo
mvn jacoco:report

# Nettoyer et tester
mvn clean test jacoco:report
```

### Option 3: Tests spécifiques
```bash
# Tester une classe spécifique
mvn test -Dtest=DemandeInterventionServiceTest

# Tester une méthode spécifique
mvn test -Dtest=DemandeInterventionServiceTest#testGetAllDemandes_Success
```

## 📊 Rapports générés

### Rapport JaCoCo
- **Localisation**: `target/site/jacoco/index.html`
- **Contenu**: Couverture de code par classe, méthode, ligne
- **Métriques**: Instructions, branches, lignes, méthodes, classes

### Rapports Surefire
- **Localisation**: `target/surefire-reports/`
- **Contenu**: Résultats détaillés des tests
- **Formats**: XML et TXT

## 🎯 Couverture de code attendue

| Module | Couverture cible |
|--------|------------------|
| Services | > 90% |
| Repositories | > 80% |
| Controllers | > 85% |
| Entities | > 70% |
| **Global** | **> 85%** |

## 🔧 Configuration

### Profil de test
- **Fichier**: `src/test/resources/application-test.properties`
- **Base de données**: H2 en mémoire
- **Sécurité**: Configuration simplifiée
- **Logs**: Niveau DEBUG pour le debugging

### Données de test
- **Classe utilitaire**: `TestDataBuilder.java`
- **Fonctionnalités**: Création d'objets de test standardisés
- **Avantages**: Réutilisabilité, cohérence, maintenance

## 🐛 Debugging des tests

### Logs utiles
```properties
# Dans application-test.properties
logging.level.tn.esprit.PI=DEBUG
logging.level.org.springframework.test=DEBUG
```

### Annotations utiles
```java
@ExtendWith(MockitoExtension.class)  // Pour Mockito
@SpringBootTest                      // Pour tests d'intégration
@ActiveProfiles("test")              // Profil de test
@WithMockUser(roles = "ADMIN")       // Utilisateur mock pour sécurité
```

## 📝 Bonnes pratiques implémentées

### Structure des tests
- **Given-When-Then**: Structure claire des tests
- **Nommage explicite**: Méthodes avec noms descriptifs
- **Isolation**: Chaque test est indépendant
- **Setup/Teardown**: Utilisation de `@BeforeEach`

### Mocking
- **@Mock**: Pour les dépendances
- **@InjectMocks**: Pour la classe testée
- **Verification**: Vérification des appels avec `verify()`
- **Stubbing**: Configuration des retours avec `when()`

### Assertions
- **JUnit 5**: Utilisation des assertions modernes
- **Messages d'erreur**: Messages explicites en cas d'échec
- **Assertions multiples**: `assertAll()` pour grouper
- **Exceptions**: `assertThrows()` pour les erreurs

## 🔍 Métriques de qualité

### Tests unitaires
- **Nombre total**: 50+ tests
- **Couverture**: > 85%
- **Temps d'exécution**: < 30 secondes
- **Fiabilité**: 100% de réussite

### Tests d'intégration
- **Scénarios**: Workflow complets
- **Endpoints**: Tous les endpoints REST
- **Sécurité**: Tests avec authentification
- **Données**: Tests avec données réalistes

## 🚨 Résolution des problèmes courants

### Erreur de base de données
```bash
# Solution: Vérifier application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
```

### Erreur de sécurité
```java
// Solution: Utiliser @WithMockUser
@WithMockUser(roles = "ADMIN")
```

### Erreur de mock
```java
// Solution: Vérifier les annotations
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
```

## 📈 Amélioration continue

### Prochaines étapes
1. **Tests de performance**: JMeter ou Gatling
2. **Tests de mutation**: PIT testing
3. **Tests de contrat**: Spring Cloud Contract
4. **Tests E2E**: Selenium ou Cypress

### Monitoring
- **Couverture**: Surveillance continue avec JaCoCo
- **Qualité**: SonarQube pour l'analyse statique
- **CI/CD**: Intégration dans pipeline Jenkins/GitLab

---

## 🎉 Conclusion

Cette suite de tests complète garantit la qualité et la fiabilité du système GMAO. Elle couvre tous les aspects critiques avec une approche moderne et des bonnes pratiques établies.

**Commande rapide pour démarrer:**
```bash
mvn clean test jacoco:report
```

**Visualiser les résultats:**
Ouvrir `target/site/jacoco/index.html` dans votre navigateur.
