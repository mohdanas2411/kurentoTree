#!/bin/bash

# ${project.description} installer for Ubuntu >= 14.04
if [ `id -u` -ne 0 ]; then
    echo ""
    echo "Only root can start Kurento"
    echo ""
    exit 1
fi

APP_HOME=$(dirname $(dirname $(readlink -f $0)))
APP_NAME=${project.artifactId}

useradd -d /var/kurento/ kurento

SYSTEMD=$(pidof systemd && echo "systemd" || echo "other")

# Install binaries
mkdir -p /var/lib/kurento
chown kurento /var/lib/kurento
install -o kurento -g root $APP_HOME/lib/$APP_NAME.jar /var/lib/kurento/
install -o kurento -g root $APP_HOME/config/$APP_NAME.conf /var/lib/kurento/
install -o kurento -g root $APP_HOME/support-files/keystore.jks /var/lib/kurento/
sudo ln -s /var/lib/kurento/$APP_NAME.jar /etc/init.d/$APP_NAME
mkdir -p /etc/kurento/
install -o kurento -g root $APP_HOME/config/app.conf.json /etc/kurento/$APP_NAME.conf.json

mkdir -p /var/log/kurento-media-server
chown kurento /var/log/kurento-media-server


if [[ "$SYSTEMD" != "other" ]]; then
	install -o root -g root $APP_HOME/support-files/systemd.service /etc/systemd/system/$APP_NAME.service

	sudo systemctl daemon-reload

	# enable at startup
	systemctl enable $APP_NAME

	# start service
	systemctl restart $APP_NAME
else
	# Create defaults
	mkdir -p /etc/default
	cat > /etc/default/kurento-tree-server <<-EOF
		# Defaults for kurento-tree-server initscript
		# sourced by /etc/init.d/kurento-tree-server
		# installed at /etc/default/kurento-tree-server by the maintainer scripts

		#
		# This is a POSIX shell fragment
		#

		# Commment next line to disable kurento-tree-server daemon
		START_DAEMON=true

		# Whom the daemons should run as
		DAEMON_USER=nobody
	EOF

	update-rc.d $APP_NAME defaults
	service $APP_NAME restart
fi