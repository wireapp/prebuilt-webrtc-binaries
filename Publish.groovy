pipeline {
    agent {
        label 'built-in'
    }
    parameters { string(name: 'PARENT_BUILDNUMBER', defaultValue: '', description: 'Build number of the webrtc-prebuild-new to be published') }
    stages {
        stage('Get artifacts') {
            steps {
                copyArtifacts filter: 'webrtc*.zip', fingerprintArtifacts: true, projectName: 'webrtc-prebuild-new', selector: specific("${PARENT_BUILDNUMBER}")
            }
        }
        stage('Publish') {
            steps {
                withCredentials([string(credentialsId: 'github-repo-access', variable: 'token')]) {
                    sh '''
                    export GITHUB_TOKEN=$token
                    . scripts/version.sh
                    BUILDNUMBER=$PARENT_BUILDNUMBER python3 scripts/push2github.py
                    '''
                }
            }
        }
    }
}
