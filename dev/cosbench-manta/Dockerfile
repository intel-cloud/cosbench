# dekobon/cosbench-manta

# We use an Ubuntu OS because it is the reference OS in the COSBench documentation.
# We use the phusion passenger base image of Ubuntu, so that we can run COSBench
# as a multi-process container.
FROM phusion/baseimage:0.9.18

MAINTAINER Elijah Zupancic <elijah.zupancic@joyent.com>

ENV JAVA_MAJOR_VERSION 8
ENV COSBENCH_VERSION 0.4.1.0
ENV COSBENCH_CHECKSUM a044cd232b3cc376802aa6a4a697988ec690a8b1d70040641710066acd322c5a
ENV COSBENCH_MANTA_VERSION 1.0.1
ENV COSBENCH_MANTA_CHECKSUM 01aee2bbf02fe95c1bb8f1d6dc88aff6ef4b29f16836d4a5dc06d29c2fe41805

# Setup the (Oracle) JVM and install needed utilities
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections

RUN apt-add-repository ppa:webupd8team/java && \
    apt-get -qq update && \
    apt-get -qy upgrade && \
    apt-get install -y oracle-java${JAVA_MAJOR_VERSION}-installer patch unzip dc && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* \
           /tmp/* \
           /var/tmp/* \
           /var/cache/oracle-jdk* \
           /usr/lib/jvm/java-${JAVA_MAJOR_VERSION}-oracle/src.zip \
           /usr/lib/jvm/java-${JAVA_MAJOR_VERSION}-oracle/javafx-src.zip

# Download and install Cosbench
RUN curl --retry 6 -Ls "https://github.com/intel-cloud/cosbench/releases/download/v${COSBENCH_VERSION}/${COSBENCH_VERSION}.zip" > /tmp/cosbench.zip && \
    echo "${COSBENCH_CHECKSUM}  /tmp/cosbench.zip" | sha256sum -c && \
    unzip -q /tmp/cosbench.zip -d /opt/ && \
    mv "/opt/${COSBENCH_VERSION}" /opt/cosbench && \
    rm /tmp/cosbench.zip

# Download and install the Manta adaptor
RUN curl --retry 6 -Ls "https://github.com/joyent/cosbench-manta/releases/download/cosbench-manta-${COSBENCH_MANTA_VERSION}/cosbench-manta-${COSBENCH_MANTA_VERSION}.jar" > /opt/cosbench/osgi/plugins/cosbench-manta.jar && \
    echo "${COSBENCH_MANTA_CHECKSUM}  /opt/cosbench/osgi/plugins/cosbench-manta.jar" | sha256sum -c

# Adding machine sizing utility useful when on Triton
COPY docker_build/usr/local/bin /usr/local/bin
RUN chmod +x /usr/local/bin/proclimit

# Install patches that will update configuration for use with Manta
# benchmarking. Check this directory for what we had to change to enable
# the adapter.
COPY docker_build/patches /patches

# Adding sample Manta configuration and init files
COPY docker_build/opt/cosbench /opt/cosbench

# Patch Cosbench for use with the Manta adaptor
RUN patch -p0 < /patches/manta_enabled.patch

# Setup Tomcat user to run COSBench process
RUN groupadd -g 120 tomcat && \
    useradd -g 120 -u 120 -c 'Tomcat User' -d /opt/cosbench -r -s /bin/false tomcat && \
    mkdir /opt/cosbench/.ssh && \
    chown -R tomcat:tomcat /opt/cosbench

# Run the container using the tomcat user by default
USER tomcat

# COSBench driver port
EXPOSE 18088
# COSBench controller port
EXPOSE 19088
