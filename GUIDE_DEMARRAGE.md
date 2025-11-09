# 🚀 Guide de Démarrage - Système GMAO

## 📋 Configuration Actuelle

### Base de Données
- **Type** : MySQL
- **Base** : `salut` (création automatique)
- **Port** : 3306
- **Utilisateur** : `root`
- **Mot de passe** : (vide)

### Serveur
- **Port** : 8089
- **Context Path** : `/PI`
- **URL Base** : `http://localhost:8089/PI`

### Documentation API
- **Swagger UI** : `http://localhost:8089/PI/swagger-ui/index.html`

### Monitoring
- **Health Check** : `http://localhost:8089/PI/actuator/health`
- **Métriques** : `http://localhost:8089/PI/actuator/prometheus`

## 🔧 Prérequis

### 1. MySQL Server
```bash
# Vérifier que MySQL est démarré
mysql -u root -p

# Créer la base si nécessaire (optionnel, création automatique activée)
CREATE DATABASE salut;
```

### 2. Java 17+
```bash
java -version
# Doit afficher Java 17 ou supérieur
```

### 3. Maven 3.6+
```bash
mvn -version
```

## 🚀 Démarrage de l'Application

### 1. Compilation et Tests
```bash
# Compilation complète avec tests
mvn clean compile test jacoco:report

# Ou utiliser le script
run-tests.bat
```

### 2. Démarrage du Serveur
```bash
# Démarrage Spring Boot
mvn spring-boot:run

# Ou après compilation
java -jar target/backend-5.1.0.jar
```

### 3. Vérification du Démarrage
```bash
# Test de santé
curl http://localhost:8089/PI/actuator/health

# Réponse attendue:
# {"status":"UP"}
```

## 📧 Configuration Email

### Gmail SMTP Configuré
- **Host** : smtp.gmail.com
- **Port** : 587
- **Email** : sallemimaha9@gmail.com
- **Mot de passe d'application** : wflmwkxdjnplvbki

### Test d'Email
```java
// Endpoint pour tester l'envoi d'email
POST http://localhost:8089/PI/test/email
{
    "to": "destinataire@example.com",
    "subject": "Test GMAO",
    "body": "Test d'envoi depuis l'application GMAO"
}
```

## 🔐 Sécurité JWT

### Configuration
- **Secret Key** : 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
- **Expiration Token** : 1 heure (3600000 ms)
- **Refresh Token** : 2 heures (7200000 ms)

### Authentification
```bash
# Login
POST http://localhost:8089/PI/user/login
{
    "email": "admin@gmao.com",
    "password": "password"
}

# Réponse avec token JWT
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "user": {...}
}
```

## 📊 Endpoints Principaux

### Authentification
- `POST /PI/user/login` - Connexion
- `POST /PI/user/register` - Inscription
- `PUT /PI/user/confirm/{id}` - Confirmation utilisateur

### Interventions
- `GET /PI/demandes/all` - Liste des interventions
- `POST /PI/demandes/create` - Créer intervention
- `PUT /PI/demandes/assign/{id}/technicien/{techId}` - Assigner technicien
- `PUT /PI/demandes/confirmer/{id}` - Confirmer intervention

### Équipements (Testeurs)
- `GET /PI/testeurs/all` - Liste des équipements
- `POST /PI/testeurs/create` - Créer équipement
- `PUT /PI/testeurs/update/{codeGMAO}` - Modifier équipement

### Bons de Travail
- `GET /PI/bons/all` - Liste des bons
- `POST /PI/bons/create` - Créer bon de travail
- `POST /PI/bons/intervention/{id}/technicien/{techId}` - Bon depuis intervention

## 🔍 Debugging et Logs

### Niveaux de Log Activés
- **Spring Framework** : DEBUG
- **Application GMAO** : DEBUG
- **Web Layer** : DEBUG
- **Boot AutoConfig** : DEBUG

### Consultation des Logs
```bash
# Logs en temps réel
tail -f logs/application.log

# Ou dans la console lors du démarrage
mvn spring-boot:run
```

## 📈 Monitoring avec Actuator

### Endpoints Disponibles
```bash
# Santé de l'application
GET http://localhost:8089/PI/actuator/health

# Informations générales
GET http://localhost:8089/PI/actuator/info

# Métriques Prometheus
GET http://localhost:8089/PI/actuator/prometheus
```

### Métriques Importantes
- **Connexions DB** : `hikari_connections_active`
- **Requêtes HTTP** : `http_server_requests_seconds`
- **JVM Memory** : `jvm_memory_used_bytes`
- **Uptime** : `process_uptime_seconds`

## 🗄️ Base de Données

### Configuration Hikari
- **Connection Timeout** : 30 secondes
- **Idle Timeout** : 10 minutes
- **Max Lifetime** : 30 minutes
- **Housekeeping Period** : 30 secondes

### Tables Principales
- `user` - Utilisateurs et rôles
- `testeur` - Équipements GMAO
- `demande_intervention` - Interventions
- `curative` / `preventive` - Types d'interventions
- `bon_de_travail` - Ordres de travail
- `component` - Composants/pièces détachées

## 🧪 Tests et Qualité

### Exécution des Tests
```bash
# Tests unitaires seulement
mvn test -Dtest="*ServiceTest"

# Avec rapport de couverture
mvn clean test jacoco:report

# Ouvrir le rapport
start target/site/jacoco/index.html
```

### Couverture Actuelle
- **Services** : > 90%
- **Total** : > 85%
- **Tests** : 57 tests unitaires

## 🚨 Troubleshooting

### Problème de Connexion MySQL
```bash
# Vérifier MySQL
mysql -u root -p

# Vérifier le port
netstat -an | findstr 3306

# Redémarrer MySQL si nécessaire
net stop mysql80
net start mysql80
```

### Problème de Port 8089
```bash
# Vérifier si le port est utilisé
netstat -an | findstr 8089

# Tuer le processus si nécessaire
taskkill /f /pid [PID]
```

### Problème d'Email
```bash
# Tester la configuration SMTP
telnet smtp.gmail.com 587

# Vérifier les logs d'application
# Rechercher "mail" dans les logs
```

## 📝 Commandes Utiles

### Développement
```bash
# Démarrage rapide
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Avec debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Build pour production
mvn clean package -Pprod
```

### Base de Données
```bash
# Backup
mysqldump -u root -p salut > backup.sql

# Restore
mysql -u root -p salut < backup.sql

# Reset (attention: supprime toutes les données)
mysql -u root -p -e "DROP DATABASE salut; CREATE DATABASE salut;"
```

## 🎯 Prochaines Étapes

1. **Démarrer l'application** : `mvn spring-boot:run`
2. **Tester l'API** : Ouvrir Swagger UI
3. **Créer un utilisateur admin** : Via endpoint `/register/Admin`
4. **Configurer les équipements** : Ajouter des testeurs
5. **Créer des interventions** : Tester le workflow complet

---

## 🎉 Félicitations !

Votre système GMAO est maintenant configuré et prêt à l'emploi avec :
- ✅ Base de données MySQL configurée
- ✅ Serveur sur port 8089 avec context `/PI`
- ✅ Email SMTP fonctionnel
- ✅ JWT sécurisé
- ✅ Monitoring Actuator/Prometheus
- ✅ Tests complets (57 tests unitaires)
- ✅ Documentation Swagger

**URL de démarrage** : `http://localhost:8089/PI/swagger-ui/index.html`
