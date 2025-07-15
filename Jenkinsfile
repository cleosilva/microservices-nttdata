// Jenkinsfile
pipeline {
    agent any
    tools {
       maven 'Maven 3.9.6'
    }

    environment {
        // Defina o DOCKER_USERNAME com seu nome de usuário do Docker Hub
        DOCKER_USERNAME = 'cleosilva' // SUBSTITUA PELO SEU USUÁRIO DO DOCKER HUB
        DOCKER_REGISTRY = 'https://docker.io' // Geralmente docker.io para Docker Hub

        // Imagem do Eureka Server
        DOCKER_IMAGE_EUREKA = "${DOCKER_USERNAME}/eureka-server:${env.BUILD_ID}"
        COMPOSE_PROJECT_NAME = "${env.JOB_NAME}-${env.BRANCH_NAME.replace('/', '-')}"
    }

    stages {
        stage('Declarative: Checkout SCM') {
                steps {
                    checkout scm
                }
        }

        stage('Checkout Source Code') {
            steps {
                git branch: 'main', url: 'https://github.com/cleosilva/microservices-nttdata.git'
            }
        }

        stage('Build Eureka Server') {
            steps {
                sh "cd service-discovery && mvn clean package"
            }
        }
        // Test unitário antes do deploy seguindo o princípio do "Fail Fast"
        stage('Run Unit Tests (Eureka Server)') {
             steps {
                  sh 'cd service-discovery && mvn test'
             }
        }

        stage('Docker Build and Push Eureka Server') {
            steps {
                script {
                    // Garante que a variável BUILD_ID esteja em minúsculas para a tag
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase()

                    // Autenticação no Docker Hub (isso já está funcionando)
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                    }

                    // Construir a imagem Docker
                    // O Dockerfile para o eureka-server está dentro de service-discovery
                    // Usamos a tag com o BUILD_ID em minúsculas
                    sh "docker build -t cleosilva/eureka-server:${lowerCaseBuildId} ./service-discovery"

                    // Fazer o push da imagem para o Docker Hub
                    sh "docker push cleosilva/eureka-server:${lowerCaseBuildId}"
                }
            }
        }

        stage('Deploy Eureka Server') {
             steps {
                  script {
                      def lowerCaseBuildId = env.BUILD_ID.toLowerCase()
                      def composeDir = '.'
                      dir(composeDir) {
                            echo "Cleaning up existing Docker Compose services for project ${env.COMPOSE_PROJECT_NAME}..."
                            sh "docker-compose -p ${env.COMPOSE_PROJECT_NAME} down --rmi all --volumes --remove-orphans || true"

                            echo "Deploying Eureka Server with build ID ${lowerCaseBuildId} for project ${env.COMPOSE_PROJECT_NAME}..."
                            // Subimos os serviços com o mesmo nome de projeto
                            sh "BUILD_ID=${lowerCaseBuildId} docker-compose -p ${env.COMPOSE_PROJECT_NAME} up -d --build eureka-server"
                            }
                      }
                  }
             }


        stage('Run Integration/Acceptance Tests') {
            steps {
               sh 'cd service-discovery && mvn verify'
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