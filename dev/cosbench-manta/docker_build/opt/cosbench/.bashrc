MANTA_ENV_WORKING=1

if [ -n "${MANTA_PUBLIC_KEY}" ] && [ ! -f $HOME/.ssh/id_rsa.pub ]; then
    echo "${MANTA_PUBLIC_KEY}" > $HOME/.ssh/id_rsa.pub
    chmod og-rwx $HOME/.ssh/id_rsa.pub
    unset MANTA_PUBLIC_KEY
fi

if [ -n "${MANTA_PRIVATE_KEY}" ] && [ ! -f $HOME/.ssh/id_rsa ]; then
    echo "${MANTA_PRIVATE_KEY}" > $HOME/.ssh/id_rsa
    chmod og-rwx $HOME/.ssh/id_rsa
    unset MANTA_PRIVATE_KEY
fi

if [ ! -f $HOME/.ssh/id_rsa.pub ]; then
    >&2 echo "No public key for Manta authentication set at $HOME/.ssh/id_rsa.pub"
    MANTA_ENV_WORKING=0
fi

if [ -z "${MANTA_USER}" ]; then
    >&2 echo "MANTA_USER must be set in order for Manta adaptor to work"
    MANTA_ENV_WORKING=0
fi

if [ -z "${MANTA_URL}" ]; then
    >&2 echo "MANTA_URL is not set. Defaulting to: https://us-east.manta.joyent.com:443"
    export MANTA_URL=https://us-east.manta.joyent.com:443
fi

if [ $MANTA_ENV_WORKING -ne 1 ]; then
    >&2 echo "Manta environment is not setup correctly"
fi

export MANTA_KEY_ID=$(ssh-keygen -l -f $HOME/.ssh/id_rsa.pub | awk '{print $2}')

# If we are running on Triton, then we will tune the JVM for the platform
if [ -d /native ]; then
    HW_THREADS=$(/usr/local/bin/proclimit)

    if [ $HW_THREADS -le 8 ]; then
        GC_THREADS=$(echo "8k $HW_THREADS 2 + pq" | dc)
    else
        # ParallelGCThreads = (ncpus <= 8) ? ncpus : 3 + ((ncpus * 5) / 8)
        ADJUSTED=$(echo "8k $HW_THREADS 5 * pq" | dc)
        DIVIDED=$(echo "8k $ADJUSTED 8 / pq" | dc)
        GC_THREADS=$(echo "8k $DIVIDED 3 + pq" | dc | awk 'function ceil(valor) { return (valor == int(valor) && value != 0) ? valor : int(valor)+1 } { printf "%d", ceil($1) }')
    fi

    export JAVA_OPTS="$JAVA_OPTS -XX:-UseGCTaskAffinity -XX:-BindGCTaskThreadsToCPUs -XX:ParallelGCThreads=${GC_THREADS}"
fi
