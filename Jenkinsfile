pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {
        stage('Instalar browsers') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -q exec:java -Dexec.args="install chromium"'
                    } else {
                        bat 'mvn -q exec:java -Dexec.args="install chromium"'
                    }
                }
            }
        }

        stage('Tests') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean test -Dheadless=true'
                    } else {
                        bat 'mvn clean test -Dheadless=true'
                    }
                }
            }
        }
    }

    post {
        always {
            allure includeProperties: false,
                   jdk: '',
                   results: [[path: 'target/allure-results']]

            archiveArtifacts artifacts: 'target/screenshots/**',
                             allowEmptyArchive: true
        }
    }
}
