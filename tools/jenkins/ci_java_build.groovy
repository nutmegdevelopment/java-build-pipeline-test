job('java-build-pipeline-test/docker-build') {

    label('testcontainers')

    wrappers {
        timestamps()
        colorizeOutput()
        preBuildCleanup()
        deliveryPipelineVersion('\${DEPLOY_VERSION}', true)
    }

    parameters {
        stringParam('DEPLOY_VERSION', '', 'Deployment version')
        stringParam('BRANCH', 'main', 'Git branch')
    }

    scm {
        git {
            branch('${BRANCH}')
            remote {
                credentials('nutcore-credentials-ssh')
                url('git@github.com:nutmegdevelopment/java-build-pipeline-test.git')
            }
            extensions {
                cleanBeforeCheckout()
            }
        }
    }

    steps {
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
    }
}