# Numbers generator demo

## Prerequisites
- kubectl, helm clients installed (also gcloud client to use GCP).
- K8s cluster installed and available.
- Login to the cluster.  
  E.g.:  
  ```gcloud container clusters get-credentials cluster-1 --zone europe-central2-a --project single-being-312008```

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

## Explore the application (v1) source code
- `numbergenerator-backend` is a Maven multi-module project.
- `numbergenerator-backend/numbergenerator-lib` is a "legacy" lib integrated in the refactoring.
- `numbergenerator-backend/numbergenerator-platform` is a distributable backend for the core business logic;
  it contains a simple REST web service through which you can invoke the core business lib.
- `numbergenerator-viewer` is a frontend application to invoke the backend service and visualize the results;
  it contains:
  - an MVC controller for preparing the data model to be visualized by the frontend
  - a service with a REST client template to invoke the backend service, and a fallback mechanism; a local cache of generate numbers
  - html template and fragments
  - static resources (i.e. javascript, images)

## Build the application locally
To test and run the application locally:
```
cd $PROJ_DIR/v1/numbergenerator-backend
#build all the dependencies and install them locally
./mvnw clean install 
#run the backend
./mvnw -f numbergenerator-platform/pom.xml spring-boot:run & 
# http://localhost:8080/random to test

cd $PROJ_DIR/v1/numbergenerator-viewer
./mvnw clean package spring-boot:run &
# http://localhost:9080/ to test
```

To test resilience, backend can be killed too.

## Images building and local test (optional for the demo - already pushed)
To build and push the images with Docker:
```
cd $PROJ_DIR/v1/numbergenerator-backend
docker build . -t ccangemi/numbergenerator-platform:v1
docker push ccangemi/numbergenerator-platform:v1

cd $PROJ_DIR/v1/numbergenerator-viewer
docker build . -t ccangemi/numbergenerator-viewer:v1
docker push ccangemi/numbergenerator-viewer:v1
```

To test:
```
docker run -d -p 8080:8080 --rm --name numbergenerator-platform ccangemi/numbergenerator-platform:v1
docker run -d -p 9080:9080 --rm -e NG_REST_ENDPOINT="http://numbergenerator-platform:8080/random" --name numbergenerator-viewer --link numbergenerator-platform ccangemi/numbergenerator-viewer:v1
```

## Deployment
```
cd $PROJ_DIR/v1/structures
kubectl apply -f numbergenerator-platform-dep.yaml
kubectl apply -f numbergenerator-platform-service.yaml
kubectl apply -f numbergenerator-viewer-cm.yaml
kubectl apply -f numbergenerator-viewer-dep.yaml
kubectl apply -f numbergenerator-viewer-service.yaml

```

## Evolve the application: V2

- Backend will elaborate numbers and put them in a Kafka Topic.
- `numbergenerator-viewer` will consume data from the same topic.
- Frontend, through a persistent websocket connection, will receive push events when a new number arrives and refresh.

## Build new image: V2 (optional for the demo - already pushed)
```
cd $PROJ_DIR/v2/numbergenerator-backend
docker build . -t ccangemi/numbergenerator-platform:v2
docker push ccangemi/numbergenerator-platform:v2

cd $PROJ_DIR/v2/numbergenerator-viewer
docker build . -t ccangemi/numbergenerator-viewer:v2
docker push ccangemi/numbergenerator-viewer:v2
```

## Install Kafka Operator (Strimzi), Kafka cluster instance and topic
Install the Kafka operator through Helm chart.
It will allow the creation of the new CustomResources: `Kafka` and `KafkaTopic`.

```
#Add Helm chart museum
helm repo add strimzi https://strimzi.io/charts/

#Create kafka operator namespace
kubectl create namespace kafka-operator

#Install kafka operator charts
helm install kafka-operator strimzi/strimzi-kafka-operator --set watchNamespaces="{numbergenerator}" -n kafka-operator

cd $PROJ_DIR/v2/structures

#Create kafka cluster instance
kubectl apply -f kafka-cluster.yaml

#Create topic
kubectl apply -f topics.yaml
```

## Update application structures to V2
```
kubectl apply -f numbergenerator-platform-dep.yaml
kubectl apply -f numbergenerator-viewer-cm.yaml
kubectl apply -f numbergenerator-viewer-dep.yaml
```

Preview the app at the numbergenerator-viewer service endpoint

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
