# Running Biobank on Docker

Follow these instructions to get Biobank running under Docker. These instructions are meant to be used on a computer running Linux.

## Biobank Docker Image

1. Download the *unversioned* files:

    ```sh
    curl https://biobank.cbsr.ualberta.ca/unversioned/biobank_unversioned_v3.10.5.zip -o biobank_unversioned_v3.10.5.zip
    ```

1. Create a file named `.env`, at the project's root folder,' with the following content:

    ```
    MODE=DEVELOPMENT
    COMPOSE_PROJECT_NAME=bb
    uid=1000
    gid=1000

    LOG_LEVEL=DEBUG

    DB_HOST=db
    #DB_HOST=localhost
    DB_PORT=3306
    DB_ROOT_USER=root
    DB_ROOT_PASSWORD=changeme
    DB_NAME=biobank
    DB_USER=biobank
    DB_PASSWORD=changeme
    ```

    Replace `changeme` with the values you want to use.

1. Build the Biobank web application:

    ```sh
    cd __project_root__
    ant -Doffline=1 deploy-jboss
    ```

    Where `__project_root__` is the root folder for the project.

1. Start the docker images using the  *compose* file:

    ```sh
    cd __project_root__
    docker compose --env-file .env -f docker/compose.yaml --project-directory docker up
    ```

    Look for the following line in the output:

    ```
    b-jboss-1  | 13:48:47,207 INFO  [STDOUT] 13:48:47,207 INFO  [DbMigrator] Current schema version: 1.6
bb-jboss-1  | 13:48:47,208 INFO  [STDOUT] 13:48:47,208 INFO  [DbMigrator] Schema is up to date. No migration necessary.
    ```

    If they show up, then the web application was built successfully.

1. Create the Docker image:

    ```sh
    cd __project_root__/docker
    docker build --platform linux/amd64 -t nloyola/biobank:0.1 .
    ```
1. Push the image to Docker Hub:

    ```sh
    docker push nloyola/biobank:0.1
    ```

    Replace `nloyola` with the name of the Docker Hub account you wish to use.

## Running Dockerized Biobank

1. Clone the project on the virtual machine or computer you wish to run Biobank on.

    ```sh
    cd __root_folder__
    git clone git@github.com:CBSR-Biobank/biobank.git
    cd biobank
    ```

    Repalce `__root_folder__` with the name of the folder you wish to have biobank installed at (usually `/opt/cbsr`).

1. Create a self signed certificate:

    ```sh
    cd __project_root__/docker
    ./nginx-selfsigned.sh
    ```

    You may enter blank settings for all prompts (just press the `Enter` key) except for the **Common Name**
    one. The common name should be the DNS name users will enter to connect to the server. For example, for CBSR's biobank server, enter:

    ```
    biobank.cbsr.ualberta.ca
    ```

1. Copy a working copy of the database:

    ```sh
    cd __project_root__
    cp __biobank_database__ database/db_initial.sql.gz
    ```
1. Start the containers

    ```sh
    cd __project_root__
    docker compose --env-file .env -f docker/compose.yaml --project-directory docker up
    ```

    The first time the database container runs, it will import the database. This may take a few minutes to finish.

    Once the database has been imported, restart the containers.

You can now test the connection to the new server using the thick client.
