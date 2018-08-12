# webflux-foundationdb
webflux(reactive) + foundationdb(suit for reactive)

## subscribe data stream
```
curl -v localhost:8080/stream -H 'Accept: application/stream+json'
```

## change address name
```
curl -v -X POST  http://localhost:8080/set -d ${addressName}
```
ex
> curl -v -X POST  http://localhost:8080/set -d SAITAMA
