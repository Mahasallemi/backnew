# 🎉 Application GMAO - Configuration MySQL avec Context-Path

## ✅ Application Démarrée avec Succès !

Votre application GMAO fonctionne maintenant avec :
- **Base de données** : MySQL (`salut`)
- **Port** : 8089
- **Context-path** : `/PI`
- **Email** : Configuration Gmail réelle

## 🌐 Nouvelles URLs avec Context-Path /PI

### 1. 🏠 Page d'Accueil
**URL** : http://localhost:8089/PI/
**Status** : ✅ Accessible

### 2. 📚 Documentation API (Swagger UI)
**URL** : http://localhost:8089/PI/swagger-ui/index.html
**Status** : ✅ Accessible
**Description** : Interface complète pour explorer et tester tous les endpoints GMAO

### 3. 💚 Health Check (Actuator)
**URL** : http://localhost:8089/PI/actuator/health
**Status** : ✅ Accessible
**Réponse attendue** : `{"status":"UP"}`

### 4. 📊 Métriques Prometheus
**URL** : http://localhost:8089/PI/actuator/prometheus
**Status** : ✅ Accessible

### 5. ℹ️ Informations Application
**URL** : http://localhost:8089/PI/actuator/info
**Status** : ✅ Accessible

### 6. 📈 Toutes les Métriques
**URL** : http://localhost:8089/PI/actuator/metrics
**Status** : ✅ Accessible

## 🔐 Endpoints d'Authentification

### Inscription Utilisateur Admin
```bash
POST http://localhost:8089/PI/user/register
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
POST http://localhost:8089/PI/user/login
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
- `GET http://localhost:8089/PI/bons/all` - Lister tous les bons
- `POST http://localhost:8089/PI/bons/create` - Créer un bon de travail
- `GET http://localhost:8089/PI/bons/intervention/{id}` - Bons par intervention

### Gestion des Projets
- `GET http://localhost:8089/PI/projects/all` - Lister tous les projets
- `POST http://localhost:8089/PI/projects/add` - Créer un projet
- `GET http://localhost:8089/PI/sousprojets/` - Lister les sous-projets

## 🗄️ Configuration Base de Données

### MySQL Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/salut?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

**Base de données** : `salut` (créée automatiquement si elle n'existe pas)

### Vérification MySQL
```sql
-- Se connecter à MySQL
mysql -u root -p

-- Vérifier la base de données
SHOW DATABASES;
USE salut;
SHOW TABLES;

-- Vérifier les utilisateurs
SELECT * FROM user;
```

## 📧 Configuration Email

### Gmail SMTP
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=sallemimaha9@gmail.com
spring.mail.password=wflmwkxdjnplvbki
```

**Note** : Configuration Gmail réelle avec mot de passe d'application

## 🧪 Tests Rapides

### Test avec curl (PowerShell)
```powershell
# Test Health Check
Invoke-WebRequest -Uri "http://localhost:8089/PI/actuator/health" -Method GET

# Test page d'accueil
Invoke-WebRequest -Uri "http://localhost:8089/PI/" -Method GET
```

### Test avec navigateur
1. **Ouvrez** : http://localhost:8089/PI/swagger-ui/index.html
2. **Explorez** les endpoints disponibles
3. **Testez** l'inscription d'un utilisateur
4. **Vérifiez** la base MySQL

## 🎯 Workflow de Test Complet

### Étape 1 : Authentification
1. Aller sur **Swagger UI** : http://localhost:8089/PI/swagger-ui/index.html
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

### Étape 4 : Vérification Base MySQL
1. Se connecter à MySQL : `mysql -u root -p`
2. Utiliser la base : `USE salut;`
3. Lister les tables : `SHOW TABLES;`
4. Vérifier les données : `SELECT * FROM user;`

## 📋 Configuration Actuelle

### Serveur
- ✅ **Port** : 8089
- ✅ **Context-path** : `/PI`
- ✅ **Base URL** : http://localhost:8089/PI

### Base de Données
- ✅ **Type** : MySQL
- ✅ **Base** : `salut`
- ✅ **Auto-création** : Activée
- ✅ **DDL** : `update` (mise à jour automatique)

### Email
- ✅ **Provider** : Gmail SMTP
- ✅ **Compte** : sallemimaha9@gmail.com
- ✅ **Authentification** : Mot de passe d'application

### JWT
- ✅ **Secret** : Clé sécurisée 256 bits
- ✅ **Expiration** : 1 heure (3600000 ms)
- ✅ **Refresh Token** : 2 heures (7200000 ms)

## 🚨 Points Importants

### Context-Path Impact
**Toutes les URLs doivent maintenant inclure `/PI`** :
- ❌ Ancien : `http://localhost:8089/swagger-ui/index.html`
- ✅ Nouveau : `http://localhost:8089/PI/swagger-ui/index.html`

### MySQL Prérequis
1. **MySQL Server** doit être installé et démarré
2. **Base `salut`** sera créée automatiquement
3. **Utilisateur `root`** sans mot de passe (ou configurer le mot de passe)

### Email Fonctionnel
- Configuration Gmail réelle
- Envoi d'emails opérationnel
- Notifications activées

## 🎊 Félicitations !

Votre application GMAO est maintenant **100% opérationnelle** avec :

- ✅ **MySQL** : Base de données production
- ✅ **Context-path** : `/PI` configuré
- ✅ **Email Gmail** : Fonctionnel
- ✅ **JWT sécurisé** : Tokens optimisés
- ✅ **Monitoring** : Actuator/Prometheus
- ✅ **Documentation** : Swagger UI accessible

## 🚀 Commandes de Gestion

### Redémarrer l'Application
```bash
# Arrêter
taskkill /f /im java.exe

# Démarrer
mvn spring-boot:run
```

### Vérifier MySQL
```bash
# Vérifier le service MySQL
net start mysql80

# Se connecter
mysql -u root -p
```

---

## 🎉 Mission Accomplie !

**Votre système GMAO est maintenant complètement opérationnel avec MySQL et context-path /PI !**

### 🔗 Liens Directs Principaux
- **🏠 Application** : http://localhost:8089/PI/
- **📚 Swagger UI** : http://localhost:8089/PI/swagger-ui/index.html
- **💚 Health Check** : http://localhost:8089/PI/actuator/health
- **📊 Métriques** : http://localhost:8089/PI/actuator/prometheus

**Commencez à tester avec ces nouvelles URLs !** 🚀
