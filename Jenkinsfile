#!/usr/bin/env groovy

pipeline {
    agent any

    post {
        success {
            ircSendSuccess()
        }

        failure {
            ircSendFailure()
        }
    }

    stages {
        stage('Checkout') {
            steps {
                ircSendStarted()

                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    env.MVN_HOME = tool 'M3'
                }

                sh "${env.MVN_HOME}/bin/mvn -Dmaven.test.failure.ignore clean package"
            }
        }

        stage('Archive') {
            steps {
                archive includes: 'target/*.jar,target/*.hpi'
                junit '**/target/surefire-reports/TEST-*.xml'
            }
        }
    }
}
