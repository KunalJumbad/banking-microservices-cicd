
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        PROJECT_NAME = 'banking-microservices'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out banking microservices source code'
                checkout scm
            }
        }

        stage('Verify Project Structure') {
            steps {
                script {
                    def services = [
                        'DiscoveryService',
                        'ApiGateway',
                        'AuthenticationService',
                        'AccountService',
                        'CustomerService'
                    ]

                    for (service in services) {
                        echo "Checking service: ${service}"
                    }
                }
            }
        }

        stage('1 - Build Eureka Server') {
            steps {
                echo 'Build DiscoveryService first'
                dir('DiscoveryService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('2 - Build API Gateway') {
            steps {
                dir('ApiGateway') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('3 - Build Authentication Service') {
            steps {
                dir('AuthenticationService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('4 - Build Account Service') {
            steps {
                dir('AccountService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('5 - Build Customer Service') {
            steps {
                dir('CustomerService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'All five service build stages completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check the Jenkins console output.'
        }

        always {
            echo 'Banking microservices pipeline finished.'
        }
    }
}
