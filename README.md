# Rest Pepper Plantation

# Objects

```kotlin
data class Pepper(var name: String, var potId: Int, var lastWatering: Long)
```

```kotlin
data class Pot(var name: String, var count: Int)
```

# Routes

# /peppers

### GET

Parameters:
- start: Int
- limit: Int
- name: String

```bash
curl -X GET "http://localhost:8080/peppers?start=0&limit=2" -i
```

```
HTTP/1.1 200 OK
Total-Count: 3
Next-Page: /peppers?start=2&limit=2
Content-Length: 116
Content-Type: application/json

[{"name":"Reaper","potId":1,"lastWatering":1652289366611},{"name":"PepperX","potId":1,"lastWatering":1652289414355}]
```

### POST

```bash
curl -X POST "http://localhost:8080/peppers" -i
```

```
HTTP/1.1 201 Created
ETag: -1273670072
Content-Length: 25
Content-Type: text/plain; charset=UTF-8

Pepper created with id: 1
```

# /peppers{id}

### GET

```bash
curl -X GET "http://localhost:8080/peppers/1" -i
```

```
HTTP/1.1 200 OK
ETag: -2064343519
Content-Length: 56
Content-Type: application/json

{"name":"Reaper","potId":1,"lastWatering":1652289366611}
```

### PUT
```bash
curl -H Content-Type:application/json -H Etag:??? -X PUT --data {"name":"Reaper","potId":1,"lastWatering":"0"} http://localhost:8080/peppers/1 -i
```

```
HTTP/1.1 200 OK
ETag: -2064343519
Content-Length: 14
Content-Type: text/plain; charset=UTF-8

Pepper updated
```

### DELETE

```bash
curl -X DELETE "http://localhost:8080/peppers/1" -i
```

```
HTTP/1.1 200 OK
Content-Length: 14
Content-Type: text/plain; charset=UTF-8

Pepper deleted
```

# /peppers/{id}/waterings

### POST

```bash
curl -X POST "http://localhost:8080/peppers/1/waterings" -i -H Etag:???
```

```
HTTP/1.1 200 OK
ETag: -285343108
Content-Length: 14
Content-Type: text/plain; charset=UTF-8

Pepper watered
```

# /pepper/{id}/repottings

"2" is the id of the pot to which the pepper will be repotted

```bash
curl -X POST "http://localhost:8080/peppers/1/repottings" -i -H Etag:??? --data "2"
```

```
HTTP/1.1 200 OK
ETag: -2063367539
Content-Length: 15
Content-Type: text/plain; charset=UTF-8

Pepper repotted
```

# /warehouse

### GET

```bash
curl -X GET "http://localhost:8080/warehouse" -i
```

```
HTTP/1.1 200 OK
Content-Length: 77
Content-Type: text/plain; charset=UTF-8

Water: 9
Soil: 9
Pots: [Pot(name=small, count=11), Pot(name=medium, count=9)]
```

# /warehouse/water

### GET

```bash
curl -X GET "http://localhost:8080/warehouse/water" -i
```

```
HTTP/1.1 200 OK
Content-Length: 1
Content-Type: text/plain; charset=UTF-8

9
```

### PUT

```bash
curl -X PUT "http://localhost:8080/warehouse/water" -i --data "100"
```

```
HTTP/1.1 200 OK
Content-Length: 10
Content-Type: text/plain; charset=UTF-8

Water: 100
```

# /warehouse/soil

### GET

```bash
curl -X GET "http://localhost:8080/warehouse/soil" -i
```

```
HTTP/1.1 200 OK
Content-Length: 1
Content-Type: text/plain; charset=UTF-8

9
```

### PUT

```bash
curl -X PUT "http://localhost:8080/warehouse/soil" -i --data "100"
```

```
HTTP/1.1 200 OK
Content-Length: 9
Content-Type: text/plain; charset=UTF-8

Soil: 100
```

# /warehouse/pots

### GET

```bash
curl -X GET "http://localhost:8080/warehouse/pots" -i
```

```
HTTP/1.1 200 OK
Content-Length: 57
Content-Type: application/json

[{"name":"small","count":11},{"name":"medium","count":9}]
```

### POST

```bash
curl -X POST "http://localhost:8080/warehouse/pots" -i
```

```
HTTP/1.1 201 Created
ETag: 0
Content-Length: 22
Content-Type: text/plain; charset=UTF-8

Pot created with id: 1
```

# /warehouse/pots/{id}

### GET

```bash
curl -X GET "http://localhost:8080/warehouse/pots/1" -i
```

```
HTTP/1.1 200 OK
ETag: -898954268
Content-Length: 27
Content-Type: application/json

{"name":"small","count":11}
```


### PUT

```bash
curl -H Content-Type:application/json -H Etag:??? -X PUT http://localhost:8080/warehouse/pots/1 --data {"name":"small","count":10} -i
```

```
HTTP/1.1 200 OK
ETag: -898954269
Content-Length: 11
Content-Type: text/plain; charset=UTF-8

Pot updated
```

### DELETE

```bash
curl -X DELETE "http://localhost:8080/warehouse/pots/1" -i
```

```
HTTP/1.1 200 OK
Content-Length: 11
Content-Type: text/plain; charset=UTF-8

Pot deleted
```

# /waterings

### POST

```bash
curl -X POST "http://localhost:8080/waterings" -i
```

```
HTTP/1.1 200 OK
Content-Length: 19
Content-Type: text/plain; charset=UTF-8

All peppers watered
```