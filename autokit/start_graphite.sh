
cd /opt/graphite/bin; ./carbon-cache.py start & > /dev/null
cd /opt/graphite/bin; ./run-graphite-devel-server.py /opt/graphite & > /dev/null
cd /opt/statsd; node ./stats.js ./localConfig.js & > /log.txt
