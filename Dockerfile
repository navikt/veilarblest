FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21 as jdk

COPY --from=jdk /bin/mkdir /bin/mkdir
COPY --from=jdk /bin/chown /bin/chown

ENV TZ="Europe/Oslo"
WORKDIR /app
COPY target/veilarblest.jar ./
RUN /bin/mkdir /secure-logs
RUN chown nonroot /secure-logs
EXPOSE 8080
USER nonroot
CMD ["veilarblest.jar"]