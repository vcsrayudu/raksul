FROM java:8
VOLUME /tmp
ADD users-0.0.1-SNAPSHOT.jar users.jar
RUN bash -c 'touch /users.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/users.jar"]
