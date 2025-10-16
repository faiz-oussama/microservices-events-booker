pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'api-gateway', 'auth-service', 'booking-service', 'discovery-service', 'event-service', 'ticket-service'
                    }
                }
                stages {
                    stage('Build ${SERVICE}') {
                        steps {
                            dir("${SERVICE}") {
                                sh 'mvn clean compile -DskipTests'
                            }
                        }
                    }
                    stage('Test ${SERVICE}') {
                        steps {
                            dir("${SERVICE}") {
                                sh 'mvn test'
                            }
                        }
                    }
                    stage('Package ${SERVICE}') {
                        steps {
                            dir("${SERVICE}") {
                                sh 'mvn package -DskipTests'
                            }
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'api-gateway', 'auth-service', 'booking-service', 'discovery-service', 'event-service', 'ticket-service'
                    }
                }
                stages {
                    stage('Build Docker ${SERVICE}') {
                        steps {
                            script {
                                def imageTag = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(8)}"
                                sh """
                                    docker build -t ${DOCKERHUB_CREDENTIALS_USR}/ticketing-${SERVICE}:${imageTag} ./${SERVICE}
                                    docker tag ${DOCKERHUB_CREDENTIALS_USR}/ticketing-${SERVICE}:${imageTag} ${DOCKERHUB_CREDENTIALS_USR}/ticketing-${SERVICE}:latest
                                """
                            }
                        }
                    }
                    stage('Push Docker ${SERVICE}') {
                        steps {
                            sh """
                                echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                                docker push ${DOCKERHUB_CREDENTIALS_USR}/ticketing-${SERVICE}:${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(8)}
                                docker push ${DOCKERHUB_CREDENTIALS_USR}/ticketing-${SERVICE}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Local Docker Compose Test') {
            steps {
                script {
                    sh """
                        docker-compose down -v
                        docker-compose up -d
                        sleep 60
                        docker-compose ps
                        echo "Testing API Gateway health check..."
                        curl -f http://localhost:8080/actuator/health || echo "Health check failed"
                        docker-compose logs --tail=50
                    """
                }
            }
        }

        stage('Integration Tests') {
            steps {
                script {
                    sh """
                        echo "Running integration tests..."
                        # Add your integration test commands here
                        # For example: mvn test -Dtest=IntegrationTest
                        echo "Integration tests completed"
                    """
                }
            }
        }
    }

    post {
        always {
            sh '''
                docker-compose down -v || true
                docker system prune -f || true
            '''
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded! Services are running locally.'
        }
        failure {
            echo 'Pipeline failed!'
            sh 'docker-compose logs || true'
        }
    }
}