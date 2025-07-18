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
        DOCKER_IMAGE_ORDER_SIMULATOR = "${DOCKER_USERNAME}/order-simulator:${env.BUILD_ID}"

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

                 echo "Building Order Simulator..."
                 sh "cd order-simulator && mvn clean package"
             }
        }

        stage('Run Unit Tests') {
              steps {
                  echo "Running unit tests for Product Catalog..."
                  sh 'cd product-catalog && mvn test'

                  echo "Running unit tests for Eureka Server..."
                  sh 'cd service-discovery && mvn test'

                  echo "Running unit tests for Order Simulator..."
                  sh 'cd order-simulator && mvn test'
              }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase()

                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                    }

                    // --- Eureka Server ---
                    echo "Building and pushing Eureka Server image..."
                    def eurekaImageWithBuildId = "${DOCKER_USERNAME}/eureka-server:${lowerCaseBuildId}"
                    def eurekaImageLatest = "${DOCKER_USERNAME}/eureka-server:latest"
                    sh "docker build -t ${eurekaImageWithBuildId} ./service-discovery"
                    sh "docker push ${eurekaImageWithBuildId}"
                    sh "docker tag ${eurekaImageWithBuildId} ${eurekaImageLatest}" // Tagueia com 'latest'
                    sh "docker push ${eurekaImageLatest}" // Faz push da 'latest'

                    // --- Catálogo de Produtos ---
                    echo "Building and pushing Product Catalog image..."
                    def catalogImageWithBuildId = "${DOCKER_USERNAME}/product-catalog:${lowerCaseBuildId}"
                    def catalogImageLatest = "${DOCKER_USERNAME}/product-catalog:latest"
                    sh "docker build -t ${catalogImageWithBuildId} ./product-catalog"
                    sh "docker push ${catalogImageWithBuildId}"
                    sh "docker tag ${catalogImageWithBuildId} ${catalogImageLatest}" // Tagueia com 'latest'
                    sh "docker push ${catalogImageLatest}" // Faz push da 'latest'

                    // --- Order Simulator --- // <-- NOVO
                    echo "Building and pushing Order Simulator image..."
                    def orderSimulatorImageWithBuildId = "${DOCKER_USERNAME}/order-simulator:${lowerCaseBuildId}"
                    def orderSimulatorImageLatest = "${DOCKER_USERNAME}/order-simulator:latest"
                    sh "docker build -t ${orderSimulatorImageWithBuildId} ./order-simulator"
                    sh "docker push ${orderSimulatorImageWithBuildId}"
                    sh "docker tag ${orderSimulatorImageWithBuildId} ${orderSimulatorImageLatest}"
                    sh "docker push ${orderSimulatorImageLatest}"
                }
            }
        }

        stage('Deploy Services with Docker Compose') {
             when {
                 branch 'develop'
             }
             steps {
                 script {
                     def lowerCaseBuildId = env.BUILD_ID.toLowerCase()
                     def composeDir = '.'
                     dir(composeDir) {
                          echo "Stopping any existing Docker Compose services..."
                          sh "docker-compose -p ${env.COMPOSE_PROJECT_NAME} down --volumes --remove-orphans || true"

                          echo "Deploying Eureka Server, Product Catalog, PostgreSQL, RabbitMQ (if configured) for project ${env.COMPOSE_PROJECT_NAME}..."
                          sh "BUILD_ID=${lowerCaseBuildId} docker-compose -p ${env.COMPOSE_PROJECT_NAME} up -d --build eureka-server product-catalog order-simulator postgres"
                     }
                 }
             }
        }
    }

    post {
        always {
            echo "Cleaning up Docker containers..."
            sh "docker-compose -p ${env.COMPOSE_PROJECT_NAME} down --volumes --remove-orphans || true"
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
    }

}