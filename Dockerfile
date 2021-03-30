FROM openjdk:8u282-jre
COPY ./google-photo-manager/ /opt/google-photo-manager/
WORKDIR /opt/google-photo-manager/
EXPOSE 8080
EXPOSE 5000
ENTRYPOINT ["/bin/sh", "run.sh"]