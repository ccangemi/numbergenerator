# RocketScience Inc. - Numbers generator demo

## Prerequisites
- kubectl, helm clients installed (also gcloud client to use GCP).
- K8s cluster installed and available.
- Login to the cluster.  
  E.g.:  
  ```gcloud container clusters get-credentials cluster-1 --zone europe-central2-c --project rocketscience-312714```

## Explore the application source code
- `numbergenerator-backend` is a Maven multi-module project.
- `numbergenerator-backend/numbergenerator-lib` is a "legacy" lib integrated in the refactoring.
- `numbergenerator-backend/numbergenerator-platform` is a distributable backend for the core business logic;
  it contains a simple REST web service through which you can invoke the core business lib on request, and a "worker" the will elaborate numbers and put them in a Kafka Topic
- `numbergenerator-viewer` is a frontend application to invoke the backend service and visualize the results;
  it contains:
  - an MVC controller for preparing the data model to be visualized by the frontend
  - a service with a REST client template to invoke the backend service, and a fallback mechanism; a local cache of generated numbers; it will consume data from the Kafka topic.
  - through a persistent websocket connection, will receive push events when a new number arrives and refresh.
  - html template and fragments
  - static resources (i.e. javascript, images)

## Build the application locally
To test and run the application locally:
```
cd $PROJ_DIR/numbergenerator-backend
#build all the dependencies and install them locally
./mvnw clean install 
#run the backend
./mvnw -f numbergenerator-platform/pom.xml spring-boot:run & 
# http://localhost:8080/random to test

cd $PROJ_DIR/numbergenerator-viewer
./mvnw clean package spring-boot:run &
# http://localhost:9080/ to test
```

To test resilience, backend can be killed too.

## Images building and local test (optional for the demo - already pushed)
To build and push the images with Docker:
```
cd $PROJ_DIR/numbergenerator-backend
docker build . -t ccangemi/numbergenerator-platform:v2
docker push ccangemi/numbergenerator-platform:v2

cd $PROJ_DIR/numbergenerator-viewer
docker build . -t ccangemi/numbergenerator-viewer:v2
docker push ccangemi/numbergenerator-viewer:v2
```

To test:
```
docker run -d -p 8080:8080 --rm --name numbergenerator-platform ccangemi/numbergenerator-platform:v2
docker run -d -p 9080:9080 --rm -e NG_REST_ENDPOINT="http://numbergenerator-platform:8080/random" --name numbergenerator-viewer --link numbergenerator-platform ccangemi/numbergenerator-viewer:v2
```

## Time to go on the cloud
Make sure you're connected to the cluster.

## Create the namespace
Create the namespace that will host application structures:
```
kubectl create namespace numbergenerator
```

## Set default namespace
In order to facilitate the operations (i.e. not having to specify `-n` flag at every step), set the default context used by `kubectl`:
```
kubectl config set-context --current --namespace=numbergenerator
```

## Install Kafka Operator (Strimzi), Kafka cluster instance and topic
Install the Kafka operator through Helm chart.
It will allow the creation of the new CustomResources: `Kafka` and `KafkaTopic`.

```
#Add Helm chart museum (already applied)
helm repo add strimzi https://strimzi.io/charts/

#Create kafka operator namespace (already applied)
kubectl create namespace kafka-operator

#Install kafka operator charts (already applied)
helm install kafka-operator strimzi/strimzi-kafka-operator --set watchNamespaces="{numbergenerator}" -n kafka-operator

cd $PROJ_DIR/structures

#Create kafka cluster instance
kubectl apply -f kafka-cluster.yaml

#Create topic
kubectl apply -f topics.yaml
```

## Deployment
```
cd $PROJ_DIR/structures
kubectl apply -f numbergenerator-platform-cm.yaml
kubectl apply -f numbergenerator-platform-dep.yaml
kubectl apply -f numbergenerator-platform-service.yaml
kubectl apply -f numbergenerator-viewer-cm.yaml
kubectl apply -f numbergenerator-viewer-dep.yaml
kubectl apply -f numbergenerator-viewer-service.yaml
```

# Test the app with rest
Get the service endpoint:
```
kubectl get service numbergenerator-viewer-service
```

# Enable Kafka
```
kubectl apply -f numbergenerator-platform-cm-kafka.yaml
kubectl apply -f numbergenerator-viewer-cm-kafka.yaml
kubectl rollout restart deployment numbergenerator-platform
kubectl rollout restart deployment numbergenerator-viewer
```

## Scale-up and down the app
```
kubectl scale deployment numbergenerator-platform --replicas 10
```

## Troubleshooting
### Debug with shell
```
kubectl exec --stdin --tty numbergenerator-viewer -- apk add --no-cache bash
kubectl exec --stdin --tty numbergenerator-viewer -- /bin/bash
```
