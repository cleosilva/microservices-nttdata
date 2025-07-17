// Jenkinsfile
pipeline {
    agent any
    tools {
       maven 'Maven 3.9.6'
    }

    environment {
        DOCKER_USERNAME = 'cleosilva'
        DOCKER_REGISTRY = 'https://docker.io'

        // Imagens dos serviços
        DOCKER_IMAGE_EUREKA = "${DOCKER_USERNAME}/eureka-server:${env.BUILD_ID}"
        DOCKER_IMAGE_CATALOG = "${DOCKER_USERNAME}/product-catalog:${env.BUILD_ID}"

        COMPOSE_PROJECT_NAME = "${env.JOB_NAME.replace('/', '-')}-${env.BRANCH_NAME.replace('/', '-')}".toLowerCase()
    }

    stages {
        stage('Declarative: Checkout SCM') {
                steps {
                    checkout scm
                }
        }

        stage('Build Microservices') {
             steps {
                 echo "Building Product Catalog..."
                 sh "cd product-catalog && mvn clean package"

                 echo "Building Eureka Server..."
                 sh "cd service-discovery && mvn clean package"
             }
        }

        stage('Run Unit Tests') {
              steps {
                  echo "Running unit tests for Product Catalog..."
                  sh 'cd product-catalog && mvn test'

                  echo "Running unit tests for Eureka Server..."
                  sh 'cd service-discovery && mvn test'
              }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase()

                    // Autenticação no Docker Hub (isso já está funcionando)
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                    }

                    // Construir e Fazer Push do Eureka Server
                    echo "Building and pushing Eureka Server image..."
                    sh "docker build -t ${DOCKER_IMAGE_EUREKA} ./service-discovery"
                    sh "docker push ${DOCKER_IMAGE_EUREKA}"

                    // Construir e Fazer Push do Catálogo de Produtos
                    echo "Building and pushing Product Catalog image..."
                    sh "docker build -t ${DOCKER_IMAGE_CATALOG} ./product-catalog"
                    sh "docker push ${DOCKER_IMAGE_CATALOG}"
                }
            }
        }

        stage('Deploy Services with Docker Compose') {
             steps {
                 script {
                     def lowerCaseBuildId = env.BUILD_ID.toLowerCase()
                     def composeDir = '.'
                     dir(composeDir) {
                          echo "Stopping any existing Docker Compose services..."
                          sh 'docker-compose -f docker-compose.yml down -v --remove-orphans'

                          echo "Cleaning up existing Docker Compose services for project ${env.COMPOSE_PROJECT_NAME}..."
                          // Removido '--rmi all' daqui. As imagens serão puxadas do Docker Hub ou construídas.
                          sh "docker-compose -p ${env.COMPOSE_PROJECT_NAME} down --volumes --remove-orphans || true"

                          echo "Deploying Eureka Server, Product Catalog, PostgreSQL, RabbitMQ (if configured) for project ${env.COMPOSE_PROJECT_NAME}..."
                          // Usamos --build aqui porque estamos construindo localmente AGORA.
                          // Quando você quiser puxar do Docker Hub (próxima etapa), mude para --pull always.
                          sh "BUILD_ID=${lowerCaseBuildId} docker-compose -p ${env.COMPOSE_PROJECT_NAME} up -d --build eureka-server product-catalog postgres"
                     }
                 }
             }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
    }

}