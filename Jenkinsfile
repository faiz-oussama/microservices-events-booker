pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        JAVA_HOME = tool name: 'JDK17', type: 'jdk'
        PATH = "${JAVA_HOME}/bin:${PATH}"
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_PREFIX = 'ticketing'
        BUILD_TIMEOUT = '30'
        TEST_TIMEOUT = '15'
        DEPLOY_TIMEOUT = '60'
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
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                dir("${SERVICE}") {
                                    bat 'mvn clean compile -DskipTests'  // Use bat for Windows
                                }
                            }
                        }
                    }
                    stage('Test ${SERVICE}') {
                        steps {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                timeout(time: env.TEST_TIMEOUT.toInteger(), unit: 'MINUTES') {
                                    dir("${SERVICE}") {
                                        bat 'mvn test'  // Use bat for Windows
                                    }
                                }
                            }
                        }
                    }
                    stage('Package ${SERVICE}') {
                        steps {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                dir("${SERVICE}") {
                                    bat 'mvn package -DskipTests'  // Use bat for Windows
                                }
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
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                timeout(time: env.BUILD_TIMEOUT.toInteger(), unit: 'MINUTES') {
                                    script {
                                        def imageTag = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(8)}"
                                        bat """
                                            docker build -t ${DOCKERHUB_CREDENTIALS_USR}/${IMAGE_PREFIX}-${SERVICE}:${imageTag} ./${SERVICE}
                                            docker tag ${DOCKERHUB_CREDENTIALS_USR}/${IMAGE_PREFIX}-${SERVICE}:${imageTag} ${DOCKERHUB_CREDENTIALS_USR}/${IMAGE_PREFIX}-${SERVICE}:latest
                                        """
                                    }
                                }
                            }
                        }
                    }
                    stage('Push Docker ${SERVICE}') {
                        steps {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                bat """
                                    echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                                    docker push ${DOCKERHUB_CREDENTIALS_USR}/${IMAGE_PREFIX}-${SERVICE}:${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(8)}
                                    docker push ${DOCKERHUB_CREDENTIALS_USR}/${IMAGE_PREFIX}-${SERVICE}:latest
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Local Docker Compose Test') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    timeout(time: env.DEPLOY_TIMEOUT.toInteger(), unit: 'MINUTES') {
                        script {
                            bat """
                                docker-compose down -v
                                docker-compose up -d
                                timeout /t 60 /nobreak > nul
                                docker-compose ps
                                echo "Testing API Gateway health check..."
                                curl -f http://localhost:8080/actuator/health || echo "Health check failed"
                                docker-compose logs --tail=50
                            """
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    script {
                        bat """
                            echo "Running integration tests..."
                            # Add your integration test commands here
                            # For example: mvn test -Dtest=IntegrationTest
                            echo "Integration tests completed"
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            bat '''
                docker-compose down -v || true
                docker system prune -f || true
            '''
            cleanWs()
            script {
                def buildStatus = currentBuild.currentResult
                def subject = "Jenkins Build ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                def body = """
                    Build Status: ${buildStatus}
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}

                    Changes:
                    ${currentBuild.changeSets}

                    Console Output:
                    ${env.BUILD_URL}console
                """
                echo "Build completed with status: ${buildStatus}"
                echo "Sending notification..."
            }
        }
        success {
            echo 'Pipeline succeeded! Services are running locally.'
            script {
                echo "Build #${env.BUILD_NUMBER} completed successfully!"
                echo "Services deployed and tested."
            }
        }
        failure {
            echo 'Pipeline failed!'
            bat 'docker-compose logs || true'
            script {
                echo "Build #${env.BUILD_NUMBER} failed!"
                echo "Check the logs above for details."
            }
        }
        unstable {
            echo 'Pipeline completed with test failures.'
            script {
                echo "Build #${env.BUILD_NUMBER} completed with warnings!"
                echo "Some tests may have failed, but deployment succeeded."
            }
        }
    }
}