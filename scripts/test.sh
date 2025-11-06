#!bin/bash

echo "Updating packages"
sudo apt update

sudo "Upgrading packages"
sudo apt upgrade

echo "Installing MySQL server"
sudo apt install -y mysql-server

echo "Ensuring it opens on boot"
sudo systemctl enable mysql

echo "Creating db and user"
sudo mysql -e "CREATE DATABASE trial;"
sudo mysql -e "CREATE USER 'root'@'localhost' IDENTIFIED BY 'Siddarth46;'
sudo mysql -e 'GRANT ALL PRIVILEGES ON trial.* TO 'root'@'localhost';'
sudo mysql -e 'FLUSH PRIVILEGES;'


echo "Creating a group"
sudo groupadd csye6225

