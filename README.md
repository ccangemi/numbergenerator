# Create namespaces
```
kubectl create namespace numbergenerator
kubectl create namespace kafka-operator

```

# Set default namespace
```
kubectl config set-context --current --namespace=numbergenerator
```

# Strimzi Operator install
```
helm repo add strimzi https://strimzi.io/charts/

helm install kafka-operator strimzi/strimzi-kafka-operator --set watchNamespaces="{numbergenerator}" -n kafka-operator
```

# Image build
```
# (in numbergenerator/numbergenerator-backend)
docker build . -t ccangemi/numbergenerator-platform:latest

docker push ccangemi/numbergenerator-platform:latest
```

```
# (in numbergenerator/numbergenerator-viewer)
docker build . -t ccangemi/numbergenerator-viewer:latest

docker push ccangemi/numbergenerator-viewer:latest
```

# Deployment
```
kubectl rollout restart deployment numbergenerator
```

# Debug with shell
```
kubectl exec --stdin --tty shell-demo -- apk add --no-cache bash
kubectl exec --stdin --tty shell-demo -- /bin/bash
```