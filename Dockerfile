FROM folioci/alpine-jre-openjdk21:latest

# Copy your fat jar to the container provide the actual name for your fat jar file for example mod-notes-fat.jar
ENV APP_FILE edge-fqm.jar
# - should be a single jar file
ARG JAR_FILE=./target/*.jar
# - copy
COPY ${JAR_FILE} ${JAVA_APP_DIR}/${APP_FILE}

# Expose this port locally in the container.
EXPOSE 8081
