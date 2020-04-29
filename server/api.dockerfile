FROM adoptopenjdk:11-openj9

ADD target/pack/ /opt

ENTRYPOINT exec /opt/bin/api