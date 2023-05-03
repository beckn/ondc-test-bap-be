# base.dockerfile
# This is the base image for all layers of the Beckn reference BAP. It builds
# and publishes (locally) the DTOs that are used by the different layers of
# the BAP.

# Use JDK 11
FROM gradle:jdk11

# Clone the git repository into the `sources/protocol-dtos` direcotry.
COPY . /sources/protocol-dtos01
# Move into the directory.
WORKDIR /sources/protocol-dtos01
# Build and publish the package.
RUN cd jvm && gradle && gradle clean autoVersion build publishToMavenLocal

# Do nothing
ENTRYPOINT ["bash", "-c", "tail --follow /dev/null"]
