// Jenkinsfile
pipeline {
    agent any
    tools {
       maven 'Maven 3.9.6'
    }

    environment {
        DOCKER_REGISTRY = 'https://docker.io'
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
                    def buildId = env.BUILD_ID.toLowerCase()

                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials',
                                                      usernameVariable: 'DOCKER_USERNAME',
                                                      passwordVariable: 'DOCKER_PASSWORD')]) {

                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"

                        def imageEureka     = "${DOCKER_USERNAME}/eureka-server:${buildId}"
                        def imageCatalog    = "${DOCKER_USERNAME}/product-catalog:${buildId}"
                        def imageOrderSim   = "${DOCKER_USERNAME}/order-simulator:${buildId}"

                        // --- Eureka Server ---
                        echo "Building and pushing Eureka Server image..."
                        sh "docker build -t ${imageEureka} ./service-discovery"
                        sh "docker push ${imageEureka}"
                        sh "docker tag ${imageEureka} ${DOCKER_USERNAME}/eureka-server:latest"
                        sh "docker push ${DOCKER_USERNAME}/eureka-server:latest"

                        // --- Product Catalog ---
                        echo "Building and pushing Product Catalog image..."
                        sh "docker build -t ${imageCatalog} ./product-catalog"
                        sh "docker push ${imageCatalog}"
                        sh "docker tag ${imageCatalog} ${DOCKER_USERNAME}/product-catalog:latest"
                        sh "docker push ${DOCKER_USERNAME}/product-catalog:latest"

                        // --- Order Simulator ---
                        echo "Building and pushing Order Simulator image..."
                        sh "docker build -t ${imageOrderSim} ./order-simulator"
                        sh "docker push ${imageOrderSim}"
                        sh "docker tag ${imageOrderSim} ${DOCKER_USERNAME}/order-simulator:latest"
                        sh "docker push ${DOCKER_USERNAME}/order-simulator:latest"
                    }
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