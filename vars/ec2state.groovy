#!/usr/bin/env groovy

def call(String instance_name = 'no_instance', String region = 'region', String state = 'start', String channel = 'channel') {

  script {
    env.INSTANCES = sh(returnStdout: true, \
  script: "export AWS_DEFAULT_REGION=${region}; \
  aws ec2 describe-instances \
  --filters \"Name=tag:Name,Values=${instance_name}\" \
  --query \"Reservations[].Instances[].InstanceId\" \
  --output text")

    sh(returnStdout: true, \
  script: "export AWS_DEFAULT_REGION=${region}; \
  aws ec2 ${state}-instances --instance-ids $INSTANCES")

    env.IPADDRESS = sh(returnStdout: true, \
  script: "export AWS_DEFAULT_REGION=${region}; \
  aws ec2 describe-instances \
  --filters \"Name=tag:Name,Values=${instance_name}\" \
  --query \"Reservations[*].Instances[*].PublicIpAddress\" \
  --output text").trim()

  }

  slackSend channel: "${channel}", color: "good", message: "${instance_name} ${state} - ${env.IPADDRESS}(<${env.BUILD_URL}|Open>)"

  echo "${env.INSTANCES}"
  echo "${env.IPADDRESS}"

  return "${env.IPADDRESS}"
}
