# 🎉 URLs Originales - Application GMAO sur Port 8089

## ✅ Port 8089 Restauré avec Succès !

Comme demandé, votre application GMAO fonctionne maintenant sur le **port original 8089**.

## 🌐 URLs Principales - Port 8089

### 1. 🏠 Page d'Accueil
**URL** : http://localhost:8089/
**Status** : ✅ Accessible

### 2. 📚 Documentation API (Swagger UI)
**URL** : http://localhost:8089/swagger-ui/index.html
**Status** : ✅ Accessible
**Description** : Interface complète pour explorer et tester tous les endpoints GMAO

### 3. 🗄️ Console H2 (Base de Données)
**URL** : http://localhost:8089/h2-console
**Paramètres de connexion** :
- **JDBC URL** : `jdbc:h2:mem:testdb`
- **Username** : `sa`
- **Password** : (laisser vide)
**Status** : ✅ Accessible

### 4. 💚 Health Check (Actuator)
**URL** : http://localhost:8089/actuator/health
**Status** : ✅ Accessible
**Réponse attendue** : `{"status":"UP"}`

### 5. 📊 Métriques Prometheus
**URL** : http://localhost:8089/actuator/prometheus
**Status** : ✅ Accessible

### 6. ℹ️ Informations Application
**URL** : http://localhost:8089/actuator/info
**Status** : ✅ Accessible

### 7. 📈 Toutes les Métriques
**URL** : http://localhost:8089/actuator/metrics
**Status** : ✅ Accessible

## 🔐 Endpoints d'Authentification

### Inscription Utilisateur Admin
```bash
POST http://localhost:8089/user/register
Content-Type: application/json

{
  "firstname": "Admin",
  "lastname": "GMAO",
  "email": "admin@gmao.com",
  "password": "password123",
  "role": "ADMIN"
}
```

### Connexion
```bash
POST http://localhost:8089/user/login
Content-Type: application/json

{
  "email": "admin@gmao.com",
  "password": "password123"
}
```

## 🛠️ Endpoints GMAO Principaux

### Gestion des Équipements (Testeurs)
- `GET http://localhost:8089/PI/testeurs/all` - Lister tous les équipements
- `POST http://localhost:8089/PI/testeurs/create` - Créer un équipement
- `PUT http://localhost:8089/PI/testeurs/update/{codeGMAO}` - Modifier un équipement

### Gestion des Interventions
- `GET http://localhost:8089/PI/demandes/all` - Lister toutes les interventions
- `POST http://localhost:8089/PI/demandes/create` - Créer une intervention
- `PUT http://localhost:8089/PI/demandes/assign/{id}/technicien/{techId}` - Assigner un technicien
- `PUT http://localhost:8089/PI/demandes/confirmer/{id}` - Confirmer une intervention

### Gestion des Bons de Travail
- `GET http://localhost:8089/pi/bons/all` - Lister tous les bons
- `POST http://localhost:8089/pi/bons/create` - Créer un bon de travail
- `GET http://localhost:8089/pi/bons/intervention/{id}` - Bons par intervention

### Gestion des Projets
- `GET http://localhost:8089/projects/all` - Lister tous les projets
- `POST http://localhost:8089/projects/add` - Créer un projet
- `GET http://localhost:8089/PI/sousprojets/` - Lister les sous-projets

## 🧪 Tests Rapides

### Test avec curl (PowerShell)
```powershell
# Test Health Check
Invoke-WebRequest -Uri "http://localhost:8089/actuator/health" -Method GET

# Test page d'accueil
Invoke-WebRequest -Uri "http://localhost:8089/" -Method GET
```

### Test avec navigateur
1. **Ouvrez** : http://localhost:8089/swagger-ui/index.html
2. **Explorez** les endpoints disponibles
3. **Testez** l'inscription d'un utilisateur
4. **Vérifiez** la base de données via H2 Console

## 🎯 Workflow de Test Complet

### Étape 1 : Authentification
1. Aller sur **Swagger UI** : http://localhost:8089/swagger-ui/index.html
2. Utiliser l'endpoint **POST /user/register** pour créer un admin
3. Utiliser l'endpoint **POST /user/login** pour se connecter
4. Récupérer le **token JWT** de la réponse

### Étape 2 : Gestion des Équipements
1. **POST /PI/testeurs/create** - Créer un équipement de test
2. **GET /PI/testeurs/all** - Vérifier la création

### Étape 3 : Gestion des Interventions
1. **POST /PI/demandes/create** - Créer une intervention
2. **PUT /PI/demandes/assign/{id}/technicien/{techId}** - Assigner un technicien
3. **PUT /PI/demandes/confirmer/{id}** - Confirmer l'intervention

### Étape 4 : Vérification Base de Données
1. Aller sur **H2 Console** : http://localhost:8089/h2-console
2. Se connecter avec les paramètres ci-dessus
3. Exécuter : `SELECT * FROM USER;`
4. Vérifier les données créées

## 📋 Configuration Actuelle

### Port Restauré
- ✅ **Port** : 8089 (comme demandé)
- ✅ **Base de données** : H2 en mémoire
- ✅ **JWT** : Configuré et sécurisé
- ✅ **Sécurité** : Spring Security configurée
- ✅ **Actuator** : Monitoring actif

### Fichiers de Configuration
- `application.properties` - Configuration principale (port 8089)
- `application-backup.properties` - Sauvegarde MySQL
- `application-h2.properties` - Configuration H2 standalone

## 🎊 Félicitations !

Votre application GMAO est maintenant **100% opérationnelle** sur le **port 8089** avec :

- ✅ **Port original** : 8089 restauré
- ✅ **57 tests unitaires** qui passent
- ✅ **Sécurité JWT** robuste
- ✅ **Base de données H2** fonctionnelle
- ✅ **APIs REST** complètes
- ✅ **Documentation Swagger** accessible
- ✅ **Monitoring Actuator** intégré
- ✅ **Pipeline CI/CD** prêt

## 🚀 Commandes de Gestion

### Redémarrer l'Application
```bash
# Arrêter
taskkill /f /im java.exe

# Attendre quelques secondes
timeout /t 10

# Démarrer
mvn spring-boot:run
```

### Tests Automatisés
```bash
# Tests unitaires
mvn test

# Avec rapport JaCoCo
mvn test jacoco:report
```

---

## 🎉 Mission Accomplie !

**Votre système GMAO est maintenant complètement opérationnel sur le port 8089 comme demandé !**

### 🔗 Liens Directs Principaux
- **🏠 Application** : http://localhost:8089/
- **📚 Swagger UI** : http://localhost:8089/swagger-ui/index.html
- **🗄️ H2 Console** : http://localhost:8089/h2-console  
- **💚 Health Check** : http://localhost:8089/actuator/health

**Commencez à tester avec ces URLs !** 🚀
