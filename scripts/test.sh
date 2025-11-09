#!bin/bash

echo "Updating packages"
sudo apt update

echo "Upgrading packages"
sudo apt upgrade

echo "Installing MySQL server"
sudo apt install -y mysql-server

echo "Ensuring it opens on boot"
sudo systemctl enable mysql


echo "Checking if user exists, if not, creating user"
USER_EXISTS=$(mysql -u"root" -p"Siddarth46" -sse \
"SELECT EXISTS(SELECT 1 FROM mysql.user WHERE user='root' AND host='localhost');")

if [ "$USER_EXISTS" != 1 ]; then
    mysql -u root -p"Siddarth46" -e "CREATE USER 'root'@'localhost' IDENTIFIED BY 'Siddarth46';
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
    FLUSH PRIVILEGES;"
fi

echo "Creating db"
DATABASE_EXISTS=$(mysql -u"root" -p"Siddarth46" -sse \
"SHOW DATABASES LIKE 'trial'")

if [ -z "$DATABASE_EXISTS" ]; then
  sudo mysql -e "CREATE DATABASE trial;"
fi

#sudo mysql -e "CREATE USER 'root'@'localhost' IDENTIFIED BY 'Siddarth46;'
#sudo mysql -e 'GRANT ALL PRIVILEGES ON trial.* TO 'root'@'localhost';'
#sudo mysql -e 'FLUSH PRIVILEGES;'


echo "Checking if user group exists"
if getent group "csye6225" > /dev/null 2>&1; then
  echo "Group 'csye6225' exists."
else
  echo "Creating group csye6225"
  sudo groupadd csye6225
fi


echo "Checking if user clouduser exists"
if id "clouduser" &>/dev/null; then
  echo "User clouduser exists."
else
  echo "Creating clouduser"
  sudo useradd -m -s /bin/bash clouduser
fi


echo "Checking if the user has been added to the user group"
if id -nG "clouduser" | grep -qw "csye6225"; then
  echo "User clouduser is in group csye6225"
else
  echo "Adding user clouduser to csye6225"
  sudo usermod -aG csye6225 clouduser
fi


APP_DIR="/opt/csye6225"
ZIP_FILE="/root/demo"
echo "Deploying application files"
mkdir -p ${APP_DIR}
if [ -f "${ZIP_FILE}" ]; then
    unzip -o "${ZIP_FILE}" -d ${APP_DIR}
else
    echo "Zip file not found: ${ZIP_FILE}"
    exit 1
fi

cd ${APP_DIR}

if [ -f "pom.xml" ]; then
    mvn clean package spring-boot:repackage -DskipTests
else
    echo "No pom.xml found — please check extracted structure."
    exit 1
fi

TARGET_JAR=$(find target -type f -name "*.jar" | head -n 1)
if [ -z "${TARGET_JAR}" ]; then
    echo "No jar found after build."
    exit 1
fi
echo "Build complete: ${TARGET_JAR}"

chown -R clouduser:csye6225 ${APP_DIR}
chmod -R 750 ${APP_DIR}

echo "Launching Spring Boot..."
sudo -u csye6225 nohup "/usr/bin/java" -jar ${TARGET_JAR} > /var/log/csye6225.log 2>&1 &

sleep 5
echo "Application started. Logs: tail -f /var/log/csye6225.log"


echo "Setup complete"