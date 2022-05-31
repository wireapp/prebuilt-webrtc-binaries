pipeline {
    agent {
        label: 'built-in'
    }
    stages {
        stage('Get artifacts') {
            steps {
                copyArtifacts filter: 'webrtc_*.zip', fingerprintArtifacts: true, projectName: 'webrtc', selector: buildParameter('BUILD_SELECTOR')
            }
        }
        stage('Publish') {
            steps {
                withCredentials([string(credentialsId: 'github-repo-access', variable: 'token')]) {
                    sh '''
                    export GITHUB_TOKEN=$token
                    . scripts/version.sh
                    # python3 scripts/push2github.py
                    '''
                }
            }
        }
    }
}
