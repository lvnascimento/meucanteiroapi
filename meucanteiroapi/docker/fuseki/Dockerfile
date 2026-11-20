FROM eclipse-temurin:21-jre

# Confira a versão mais recente em https://jena.apache.org/download/
ARG JENA_VERSION=5.2.0

RUN curl -fSL --retry 5 --retry-all-errors --retry-delay 3 \
      -o /tmp/fuseki.tar.gz \
      https://repo1.maven.org/maven2/org/apache/jena/apache-jena-fuseki/${JENA_VERSION}/apache-jena-fuseki-${JENA_VERSION}.tar.gz \
 && tar xzf /tmp/fuseki.tar.gz -C /opt \
 && mv /opt/apache-jena-fuseki-${JENA_VERSION} /opt/fuseki \
 && rm /tmp/fuseki.tar.gz

WORKDIR /opt/fuseki
COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

ENV JVM_ARGS="-Xmx1500m"

EXPOSE 3030
ENTRYPOINT ["/entrypoint.sh"]