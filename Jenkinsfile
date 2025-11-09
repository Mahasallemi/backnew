pipeline {
    agent any

    environment {
        // ====== DOCKER HUB ======
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub'
        DOCKERHUB_USER           = 'mahasallemi'

        // ====== NEXUS DOCKER ======
        // Credentials ID = "nexus-docker" (admin / mot de passe Nexus)
        NEXUS_DOCKER_CREDENTIALS_ID = 'nexus-docker'
        NEXUS_DOCKER_URL            = 'localhost:5000'

        // ====== GIT REPOS ======
        BACKEND_REPO_URL  = 'https://github.com/Mahasallemi/backnew.git'
        FRONTEND_REPO_URL = 'https://github.com/Mahasallemi/frontnew.git'

        // ====== IMAGES (Docker Hub) ======
        BACKEND_IMAGE_DH  = 'mahasallemi/backend'
        FRONTEND_IMAGE_DH = 'mahasallemi/frontend'

        // ====== IMAGES (Nexus - docker-hosted) ======
        BACKEND_IMAGE_NX  = 'localhost:5000/backend'
        FRONTEND_IMAGE_NX = 'localhost:5000/frontend'

        // ====== VERSIONS ======
        BACKEND_VERSION  = '5.1.0'
        FRONTEND_VERSION = '1.0.0'

        // ====== SONARQUBE ======
        SONARQUBE_ENV_NAME = 'backend'           // Nom du serveur dans Jenkins
        SONAR_PROJECT_KEY  = 'tn.esprit:backend'  // Clé projet

        // ====== DEPLOIEMENT LOCAL ======
        DEPLOY_PATH = '/var/lib/jenkins/pfe-app'
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {

        // ================= BACKEND =================

        stage('Backend - Checkout') {
            steps {
                echo '📥 Clonage du backend...'
                dir('backend') {
                    git url: "${BACKEND_REPO_URL}", branch: 'main'
                }
            }
        }

        stage('Backend - Build & Tests (JaCoCo)') {
            steps {
                dir('backend') {
                    echo '🧪 Build + tests + JaCoCo backend...'
                    sh '''
                        chmod +x mvnw || true
                        ./mvnw -B clean verify jacoco:report
                    '''
                }
            }
        }

        stage('Backend - Publish Test Reports') {
            steps {
                dir('backend') {
                    echo '📊 Publication rapports JUnit & JaCoCo...'

                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false

                    script {
                        jacoco(
                            execPattern: 'target/jacoco.exec',
                            classPattern: 'target/classes',
                            sourcePattern: 'src/main/java',
                            inclusionPattern: '**/*.class',
                            exclusionPattern: '**/*Test*.class,**/generated/**'
                        )
                    }

                    archiveArtifacts artifacts: 'target/site/jacoco/jacoco.xml, target/surefire-reports/*.xml',
                                      fingerprint: true
                }
            }
        }

        stage('Backend - SonarQube Analysis') {
            steps {
                dir('backend') {
                    echo '🔎 Analyse SonarQube backend...'
                    withSonarQubeEnv("${SONARQUBE_ENV_NAME}") {
                        sh '''
                            ./mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                              -Dsonar.projectKey=tn.esprit:backend \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -DskipTests
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '✅ Vérification du Quality Gate SonarQube...'
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate abortPipeline: false
                        if (qg.status != 'OK') {
                            echo "⚠️ Quality Gate failed: ${qg.status}"
                            echo "📊 Pipeline continue malgré l'échec du Quality Gate"
                            echo "🔗 SonarQube Dashboard: http://172.22.156.136:9000/dashboard?id=tn.esprit%3Abackend"
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            echo "✅ Quality Gate passed!"
                        }
                    }
                }
            }
        }

        // ================= FRONTEND =================

        stage('Frontend - Checkout') {
            steps {
                echo '📥 Clonage du frontend...'
                dir('frontend') {
                    git url: "${FRONTEND_REPO_URL}", branch: 'main'
                }
            }
        }

        stage('Frontend - Build') {
            steps {
                dir('frontend') {
                    echo '🎨 Build du frontend React...'
                    sh '''
                        if [ -f package-lock.json ]; then
                          npm ci --legacy-peer-deps
                        else
                          npm install --legacy-peer-deps
                        fi

                        CI=false npm run build
                    '''
                }
            }
        }

        // ================= DOCKER BUILD =================

        stage('Docker Build - Backend & Frontend') {
            steps {
                echo '🐳 Build des images Docker (DockerHub + Nexus)...'

                dir('backend') {
                    sh '''
                        docker build \
                          -t ${BACKEND_IMAGE_DH}:${BACKEND_VERSION} \
                          -t ${BACKEND_IMAGE_DH}:latest \
                          -t ${BACKEND_IMAGE_NX}:${BACKEND_VERSION} \
                          -t ${BACKEND_IMAGE_NX}:latest .
                    '''
                }

                dir('frontend') {
                    sh '''
                        docker build \
                          -t ${FRONTEND_IMAGE_DH}:${FRONTEND_VERSION} \
                          -t ${FRONTEND_IMAGE_DH}:latest \
                          -t ${FRONTEND_IMAGE_NX}:${FRONTEND_VERSION} \
                          -t ${FRONTEND_IMAGE_NX}:latest .
                    '''
                }
            }
        }

        // ================= DOCKER PUSH - DOCKER HUB =================

        stage('Docker Push - Docker Hub') {
            steps {
                echo '📤 Push des images sur Docker Hub...'
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKERHUB_CREDENTIALS_ID}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        docker push ${BACKEND_IMAGE_DH}:${BACKEND_VERSION}
                        docker push ${BACKEND_IMAGE_DH}:latest

                        docker push ${FRONTEND_IMAGE_DH}:${FRONTEND_VERSION}
                        docker push ${FRONTEND_IMAGE_DH}:latest

                        docker logout
                    '''
                }
            }
        }

        // ================= DOCKER PUSH - NEXUS =================

        stage('Docker Push - Nexus') {
            steps {
                echo '📤 Push des images sur Nexus (docker-hosted)...'
                withCredentials([
                    usernamePassword(
                        credentialsId: "${NEXUS_DOCKER_CREDENTIALS_ID}",
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )
                ]) {
                    sh '''
                        docker login ${NEXUS_DOCKER_URL} -u "$NEXUS_USER" -p "$NEXUS_PASS"

                        docker push ${BACKEND_IMAGE_NX}:${BACKEND_VERSION}
                        docker push ${BACKEND_IMAGE_NX}:latest

                        docker push ${FRONTEND_IMAGE_NX}:${FRONTEND_VERSION}
                        docker push ${FRONTEND_IMAGE_NX}:latest

                        docker logout ${NEXUS_DOCKER_URL} || true
                    '''
                }
            }
        }

        // ================= DEPLOY + PROMETHEUS + GRAFANA =================

        stage('Deploy with docker-compose') {
            steps {
                echo '🚀 Déploiement avec docker-compose (images Nexus) + Prometheus + Grafana...'
                sh '''
                    # Nettoyage ancien
                    cd ${DEPLOY_PATH} 2>/dev/null && docker compose down || true

                    # Arborescence
                    mkdir -p ${DEPLOY_PATH}/config
                    mkdir -p ${DEPLOY_PATH}/grafana/provisioning/datasources
                    mkdir -p ${DEPLOY_PATH}/grafana/provisioning/dashboards
                    mkdir -p ${DEPLOY_PATH}/grafana/dashboards
                    mkdir -p ${DEPLOY_PATH}/grafana/data

                    # ====== Prometheus config ======
cat > ${DEPLOY_PATH}/config/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-backend'
    metrics_path: '/PI/actuator/prometheus'
    static_configs:
      - targets: ['spring-backend:8089']
EOF

                    # ====== Grafana datasource Prometheus ======
cat > ${DEPLOY_PATH}/grafana/provisioning/datasources/prometheus.yml << 'EOF'
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
EOF

                    # ====== Grafana dashboards provider ======
cat > ${DEPLOY_PATH}/grafana/provisioning/dashboards/dashboard.yml << 'EOF'
apiVersion: 1

providers:
  - name: 'Spring Boot Dashboards'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    editable: true
    options:
      path: /var/lib/grafana/dashboards
EOF

                    # ====== Dashboard JSON Spring Boot ======
cat > ${DEPLOY_PATH}/grafana/dashboards/spring-boot-overview.json << 'EOF'
{
  "id": null,
  "uid": "spring-boot-overview",
  "title": "Spring Boot - JVM & HTTP",
  "tags": ["spring", "prometheus", "jvm"],
  "timezone": "browser",
  "schemaVersion": 39,
  "version": 1,
  "refresh": "10s",
  "time": { "from": "now-15m", "to": "now" },
  "panels": [
    {
      "type": "stat",
      "title": "Uptime (seconds)",
      "id": 1,
      "gridPos": { "x": 0, "y": 0, "w": 4, "h": 4 },
      "targets": [
        {
          "refId": "A",
          "expr": "process_uptime_seconds{job=\\"spring-backend\\"}"
        }
      ],
      "options": {
        "reduceOptions": { "calcs": ["lastNotNull"], "fields": "", "values": false }
      }
    },
    {
      "type": "timeseries",
      "title": "JVM Memory Used",
      "id": 2,
      "gridPos": { "x": 4, "y": 0, "w": 12, "h": 8 },
      "targets": [
        {
          "refId": "A",
          "expr": "jvm_memory_used_bytes{job=\\"spring-backend\\"}"
        }
      ]
    },
    {
      "type": "timeseries",
      "title": "HTTP Requests by Status (1m rate)",
      "id": 3,
      "gridPos": { "x": 0, "y": 8, "w": 16, "h": 8 },
      "targets": [
        {
          "refId": "A",
          "expr": "sum by (status) (rate(http_server_requests_seconds_count{job=\\"spring-backend\\"}[1m]))"
        }
      ]
    }
  ]
}
EOF

                    # ====== docker-compose.yml ======
cat > ${DEPLOY_PATH}/docker-compose.yml << EOF
name: pfe-app

services:
  backend:
    image: ${BACKEND_IMAGE_NX}:${BACKEND_VERSION}
    container_name: spring-backend
    restart: always
    environment:
      SERVER_PORT: "8089"
      SPRING_DATASOURCE_URL: jdbc:mysql://192.168.0.8:3306/salut?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ""
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT: org.hibernate.dialect.MySQL8Dialect
      SPRING_JPA_SHOW_SQL: "true"
      MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: prometheus,health,info
      MANAGEMENT_ENDPOINT_PROMETHEUS_ENABLED: "true"
      MANAGEMENT_METRICS_EXPORT_PROMETHEUS_ENABLED: "true"
      JWT_SECRET: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
    ports:
      - "8089:8089"
    networks:
      - app-network

  frontend:
    image: ${FRONTEND_IMAGE_NX}:${FRONTEND_VERSION}
    container_name: react-frontend
    depends_on:
      - backend
    restart: always
    environment:
      NODE_ENV: production
    ports:
      - "3000:80"
    networks:
      - app-network

  phpmyadmin:
    image: phpmyadmin:latest
    container_name: phpmyadmin
    restart: always
    environment:
      PMA_HOST: 192.168.0.8
      PMA_PORT: 3306
      PMA_USER: root
      PMA_PASSWORD: ""
    ports:
      - "8081:80"
    networks:
      - app-network

  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    restart: always
    volumes:
      - ${DEPLOY_PATH}/config/prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    networks:
      - app-network

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    restart: always
    depends_on:
      - prometheus
    environment:
      GF_SECURITY_ADMIN_USER: admin
      GF_SECURITY_ADMIN_PASSWORD: admin
      GF_PATHS_PROVISIONING: /etc/grafana/provisioning
    ports:
      - "3002:3000"
    volumes:
      - ${DEPLOY_PATH}/grafana/provisioning/datasources:/etc/grafana/provisioning/datasources
      - ${DEPLOY_PATH}/grafana/provisioning/dashboards:/etc/grafana/provisioning/dashboards
      - ${DEPLOY_PATH}/grafana/dashboards:/var/lib/grafana/dashboards
      - ${DEPLOY_PATH}/grafana/data:/var/lib/grafana
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
EOF

                    cd ${DEPLOY_PATH}
                    docker compose pull || true
                    docker compose up -d
                '''
            }
        }
    }

    post {
        success {
            echo '✅ SUCCESS — Build, tests, SonarQube OK, push DockerHub + Nexus, déploiement et monitoring opérationnels.'
            echo '🌐 Frontend :     http://localhost:3000'
            echo '⚙️ Backend :      http://localhost:8089'
            echo '🛢️ phpMyAdmin :   http://localhost:8081'
            echo '📈 Prometheus :   http://localhost:9090'
            echo '📊 Grafana :      http://localhost:3002 (admin / admin)'
        }
        unstable {
            echo '⚠️ UNSTABLE — Pipeline terminé avec des avertissements (Quality Gate failed mais déploiement réussi)'
            echo '🔗 Vérifiez SonarQube : http://172.22.156.136:9000/dashboard?id=tn.esprit%3Abackend'
            echo '🌐 Frontend :     http://localhost:3000'
            echo '⚙️ Backend :      http://localhost:8089'
        }
        failure {
            echo '❌ ECHEC — Voir les logs ci-dessus pour la cause.'
        }
    }
}
