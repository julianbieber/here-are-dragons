## Dungeons & Training

### Abhängigkeiten

* sbt ([Installation](https://www.scala-sbt.org/1.x/docs/Setup.html)) zum bauen der API
* docker ([Installation](https://docs.docker.com/get-docker/)) als Container für die API
* docker-compose ([Installation](https://docs.docker.com/compose/install/)) für das ausführen der API und der db (und sptäter um die db für die tests zu starten)

### Run

* build the server (in server directory): `sbt pack`
* run tests:
```
docker-compose -f src/test/docker-compose.yml up --force-recreate --build -d
sbt test
```
* run the server (in server directory): `docker-compose up --force-recreate --build`

