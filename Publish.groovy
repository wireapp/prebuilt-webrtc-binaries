pipeline {
    agent {
        label 'built-in'
    }
    parameters { string(name: 'PARENT_BUILD_NUMBER', defaultValue: '', description: 'Build number of the webrtc-prebuild-new to be published') }
    stages {
        stage('Clean') {
            steps {
                sh 'rm -rf webrtc*.zip'
            }
        }
        stage('Get artifacts') {
            steps {
                copyArtifacts filter: 'webrtc*.zip', fingerprintArtifacts: true, projectName: 'webrtc-prebuild-new', selector: specific("${PARENT_BUILD_NUMBER}")
            }
        }
        stage('Publish') {
            steps {
                withCredentials([string(credentialsId: 'github-repo-access', variable: 'token')]) {
                    sh '''
                    export GITHUB_TOKEN=$token
                    . scripts/version.sh
                    BUILD_NUMBER=$PARENT_BUILD_NUMBER python3 scripts/push2github.py
                    '''
                }
            }
        }
    }
}
