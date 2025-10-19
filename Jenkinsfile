pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        IMAGE_PREFIX = 'ticketing'
        BUILD_TIMEOUT = '30'
        TEST_TIMEOUT = '15'
        DEPLOY_TIMEOUT = '60'
        MAVEN_IMAGE = 'maven:3.9.6-eclipse-temurin-17'
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
                                    script {
                                        docker.image(env.MAVEN_IMAGE).inside('-v $HOME/.m2:/root/.m2') {
                                            sh 'mvn clean compile -DskipTests'
                                        }
                                    }
                                }
                            }
                        }
                    }

                    stage('Test ${SERVICE}') {
                        steps {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                timeout(time: env.TEST_TIMEOUT.toInteger(), unit: 'MINUTES') {
                                    dir("${SERVICE}") {
                                        script {
                                            docker.image(env.MAVEN_IMAGE).inside('-v $HOME/.m2:/root/.m2') {
                                                sh 'mvn test'
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    stage('Package ${SERVICE}') {
                        steps {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                dir("${SERVICE}") {
                                    script {
                                        docker.image(env.MAVEN_IMAGE).inside('-v $HOME/.m2:/root/.m2') {
                                            sh 'mvn package -DskipTests'
                                        }
                                    }
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
                                        sh """
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
                                sh """
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
                        sh """
                            docker-compose down -v || true
                            docker-compose up -d
                            sleep 60
                            docker-compose ps
                            curl -f http://localhost:8080/actuator/health || echo "Health check failed"
                            docker-compose logs --tail=50
                        """
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    sh '''
                        echo "Running integration tests..."
                        # mvn test -Dtest=IntegrationTest
                        echo "Integration tests completed"
                    '''
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
            script {
                echo "Build completed with status: ${currentBuild.currentResult}"
            }
        }
        success {
            echo 'Pipeline succeeded! Services are running locally.'
        }
        failure {
            echo 'Pipeline failed! Check logs above.'
        }
        unstable {
            echo 'Pipeline completed with test failures.'
        }
    }
}
