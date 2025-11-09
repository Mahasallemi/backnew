# 🔧 Résolution du Problème MySQL - Application GMAO

## 🚨 Problème Identifié

L'application ne peut pas démarrer à cause d'un problème de connexion à MySQL. L'erreur indique que la propriété `application.security.jwt.refresh-token.expiration` était manquante (maintenant corrigée), mais il y a probablement aussi un problème de connexion à la base de données.

## ✅ Corrections Déjà Appliquées

### 1. Propriété JWT Manquante
**Ajoutée** : `application.security.jwt.refresh-token.expiration=604800000` (7 jours)

### 2. Configuration Complète JWT
```properties
# Configuration JWT
application.security.jwt.secret-key=mySecretKey123456789012345678901234567890
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000
```

## 🔍 Solutions Possibles

### Option 1: Installer et Configurer MySQL

#### Installation MySQL
1. **Télécharger MySQL** : https://dev.mysql.com/downloads/installer/
2. **Installer MySQL Server** avec les paramètres par défaut
3. **Configurer le mot de passe root** (ou laisser vide comme dans votre config)

#### Vérification MySQL
```bash
# Vérifier que MySQL fonctionne
net start mysql80

# Tester la connexion
mysql -u root -p

# Créer la base de données
CREATE DATABASE pfe;
```

### Option 2: Utiliser H2 pour les Tests (Recommandé)

#### Configuration H2 Temporaire
J'ai créé `application-h2.properties` pour tester avec H2 :

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

#### Démarrage avec H2
```bash
# Copier la configuration H2 vers application.properties temporairement
copy src\main\resources\application-h2.properties src\main\resources\application.properties

# Démarrer l'application
mvn spring-boot:run
```

### Option 3: Configuration Docker MySQL

#### docker-compose.yml
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ""
      MYSQL_ALLOW_EMPTY_PASSWORD: "yes"
      MYSQL_DATABASE: pfe
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

#### Démarrage Docker
```bash
# Démarrer MySQL avec Docker
docker-compose up -d

# Vérifier que MySQL fonctionne
docker ps
```

## 🚀 Test de l'Application

### Étapes de Test

1. **Compilation** (✅ Fonctionne)
   ```bash
   mvn clean compile
   ```

2. **Tests Unitaires** (✅ Fonctionnent)
   ```bash
   mvn test -Dtest="*ServiceTest"
   ```

3. **Démarrage Application**
   ```bash
   # Option A: Avec MySQL (si installé)
   mvn spring-boot:run
   
   # Option B: Avec H2 (temporaire)
   # Copier application-h2.properties vers application.properties
   mvn spring-boot:run
   ```

### Vérification du Démarrage
```bash
# Test de santé
curl http://localhost:8089/actuator/health

# Swagger UI
# http://localhost:8089/swagger-ui/index.html
```

## 📋 Configuration Recommandée pour le Développement

### application-dev.properties
```properties
# Configuration MySQL pour développement
spring.datasource.url=jdbc:mysql://localhost:3306/pfe?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
application.security.jwt.secret-key=mySecretKey123456789012345678901234567890
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000

# Email (à configurer avec vos vrais paramètres)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-app

# Serveur
server.port=8089

# Logs
logging.level.tn.esprit.PI=INFO
logging.level.org.springframework.security=WARN

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

### application-h2.properties (Pour tests rapides)
```properties
# Base de données H2 en mémoire
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Console H2 (pour debug)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Même configuration JWT et serveur que dev
```

## 🔧 Commandes de Dépannage

### Vérification MySQL
```bash
# Windows - Vérifier si MySQL est installé
sc query mysql80

# Démarrer MySQL
net start mysql80

# Arrêter MySQL
net stop mysql80

# Tester la connexion
telnet localhost 3306
```

### Vérification Port 8089
```bash
# Vérifier si le port est utilisé
netstat -an | findstr 8089

# Tuer le processus si nécessaire
taskkill /f /pid [PID]
```

### Logs d'Application
```bash
# Démarrer avec logs détaillés
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.org.springframework=DEBUG"
```

## 📝 Prochaines Étapes

### Étape 1: Choisir la Solution
- **Pour développement rapide** : Utiliser H2 (Option 2)
- **Pour production** : Installer MySQL (Option 1) ou Docker (Option 3)

### Étape 2: Tester l'Application
```bash
# 1. Compilation
mvn clean compile

# 2. Tests
mvn test

# 3. Démarrage
mvn spring-boot:run

# 4. Vérification
curl http://localhost:8089/actuator/health
```

### Étape 3: Configuration Email
Une fois l'application démarrée, configurer les vrais paramètres email dans `application.properties`.

## 🎯 Résumé des Fichiers Modifiés

1. **`application.properties`** - Ajout propriété JWT manquante
2. **`application-h2.properties`** - Configuration H2 pour tests
3. **`RESOLUTION_PROBLEME_MYSQL.md`** - Ce guide

## 🚀 Test Rapide avec H2

Pour tester immédiatement l'application :

```bash
# 1. Sauvegarder la config MySQL
copy src\main\resources\application.properties src\main\resources\application-mysql.properties.bak

# 2. Utiliser H2
copy src\main\resources\application-h2.properties src\main\resources\application.properties

# 3. Démarrer
mvn spring-boot:run

# 4. Tester
# http://localhost:8089/swagger-ui/index.html
# http://localhost:8089/h2-console (JDBC URL: jdbc:h2:mem:testdb)
```

---

## 🎉 Conclusion

Le problème JWT est résolu. Il reste à choisir entre MySQL (production) ou H2 (développement rapide) pour la base de données. Les deux configurations sont prêtes à utiliser !
