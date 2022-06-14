buildNumber = currentBuild.id
version = null
branchName = null
commitId = null
repoName = null

pipeline {
    agent none

    options {
        parallelsAlwaysFailFast()
        disableConcurrentBuilds()
    }

    // NOTE: checks every 5 minutes if a new commit occurred after last successful run
    triggers {
        pollSCM 'H/5 * * * *'
    }

    stages {
        stage('Checkout') {
            agent {
                label 'linuxbuild'
            }
            steps {
           }
        }
        stage( 'Prepare + Test + Build' ) {
            parallel {
                stage('Linux') {
                    agent {
                        dockerfile {
                            args '-u root:root'
                        }
                    }
                    steps {
						script {
							def vcs = checkout([
									changelog: true,
									poll: true,
									scm: [
									$class: 'GitSCM',
									userRemoteConfigs: [[
											url: "https://github.com/wireapp/wire-avs"
									]],
									branches: [[name: '*/' + params.BRANCH]],/*
									extensions: [
											[
											$class: 'SubmoduleOption',
											disableSubmodules: false,
											recursiveSubmodules: true,
											parentCredentials: true
											]//,
											//[
											//$class: 'WipeWorkspace'
											//]
									]*/
									]
							])
							
							branchName = vcs.GIT_BRANCH.tokenize( '/' ).drop( 1 ).join( '/' )
							commitId = "${vcs.GIT_COMMIT}"[0..6] 
							repoName = vcs.GIT_URL.tokenize( '/' ).last().tokenize( '.' ).first()
							repoUser = vcs.GIT_URL.tokenize( '/' )[-2]
							
							if ( params.RELEASE_VERSION == null ) {
								version = "0.0.${ buildNumber }"
							} else {
								version = "${RELEASE_VERSION}.${ buildNumber }"
							}
						}
					
                        // The following commands are needed b/c of JENKINS-38438
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/cryptobox-c'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/generic-message-proto'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/googletest'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/openssl'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/re'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/rem'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/rew'
                        //sh 'git config --global --add safe.directory /home/jenkins/workspace/avs_docker/contrib/sodium'
                        //sh 'rm -rf ./build'
                        //sh 'git submodule update --init --recursive'
                        //sh 'chown -R 1001:1001 ./contrib'

                        // clean
                        sh 'make distclean'
                        sh 'touch src/version/version.c'

                        // build tests
                        sh 'make test AVS_VERSION=' + version
                        // run tests
                        sh './ztest'
                        
                        sh 'make dist_clean'
                        sh 'make zcall AVS_VERSION=' + version
                        sh 'make dist_linux AVS_VERSION=' + version + ' BUILDVERSION=' + version
                        sh 'rm -rf ./build/artifacts'
                        sh 'mkdir -p ./build/artifacts'
                        sh 'cp ./build/dist/linux/avscore.tar.bz2 ./build/artifacts/avs.linux.' + version + '.tar.bz2'
                        sh 'zip -9j ./build/artifacts/zcall_linux_${ version }.zip ./zcall'
                    }
                }
                stage('macOS') {
                    agent {
                        label 'built-in'
                    }
                    steps {
                        sh 'echo noop'
                    }
                }
            }
        }
    }
    
    post {
        always {
            node('linuxbuild') {
                script {
                    sh 'docker container prune -f && docker volume prune -f && docker image prune -f'
                }
            }
        }
        
        success { 
            node( 'built-in' ) {
                withCredentials([ string( credentialsId: 'wire-jenkinsbot', variable: 'jenkinsbot_secret' ) ]) {
                    sh 'echo noop'
                    //wireSend secret: "$jenkinsbot_secret", message: "✅ avs ${ params.RELEASE_VERSION != null ? params.RELEASE_VERSION : 'main' } (${ BUILD_ID }) succeeded\n${ BUILD_URL }console\nhttps://${ REPO_BASE_PATH }/commit/${ commitId }"
                }
            }
        }
        
        failure { 
            node( 'built-in' ) {
                withCredentials([ string( credentialsId: 'wire-jenkinsbot', variable: 'jenkinsbot_secret' ) ]) {
                    sh 'echo noop'
                    //wireSend secret: "$jenkinsbot_secret", message: "❌ avs ${ params.RELEASE_VERSION != null ? params.RELEASE_VERSION : 'main' } (${ BUILD_ID }) failed\n${ BUILD_URL }console\nhttps://${ REPO_BASE_PATH }/commit/${ commitId }"
                }
            }
        }
    }
}

