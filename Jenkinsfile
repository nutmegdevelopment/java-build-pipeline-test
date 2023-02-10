pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    agent {
        label 'k8s'
    }

    stages {

        stage('Environment') {
            steps {
                withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                                  credentialsId   : "nutcore-credentials-github",
                                  usernameVariable: 'GIT_USERNAME',
                                  passwordVariable: 'GIT_PASSWORD']]) {
                    sh "git config --global user.email jenkins-bulls@nutmeg.com"
                    sh "git config --global user.name jenkins-bulls"
                    sh "git config remote.origin.url https://\${GIT_PASSWORD}@github.com/nutmegdevelopment/nm-document-service.git"
                    sh "git fetch --tags"

                    shell('''
                        #!/usr/bin/env bash
                        set -e
                        pwd
                        ls -la
                        uname -a
                        ls /proc/sys/fs/binfmt_misc/
                        yum list installed | grep 'qemu'
                        yum list installed | grep 'binfmt'
                        docker info
                        java -version
                        #tools/bin/build.sh -d ${BRANCH} -v ${DEPLOY_VERSION}
                        #tools/bin/create-application-container.sh -d -v ${DEPLOY_VERSION}
                    ''')

                    script {
                        env.RELEASE = isReleaseBranch() ? nextRelease() : currentCommitSha()
                    }
                }
            }
        }
    }
}

def currentCommitSha() {
    sh "git rev-parse --short HEAD > .git/current-commit"
    return ("${env.BRANCH_NAME.take(9)}-" + readFile(".git/current-commit").trim()).replaceAll(/[^A-Za-z0-9]/, "").toLowerCase()
}

def nextRelease() {
    sh "git tag -l --sort version:refname | awk '/^([0-9]+).([0-9]+).([0-9]+)\$/{split(\$0,v,\".\")}END{printf(\"%d.%d.%d\",v[1],v[2],v[3]+1)}' > .git/current-tag"
    readFile(".git/current-tag").trim()
}

def isReleaseBranch() {
    env.BRANCH_NAME == "main"
}

static stringParameterVal(String name, String value) {
    [$class: 'StringParameterValue', name: name, value: value]
}
