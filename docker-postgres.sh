docker run -d \
--name dev-postgres \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-v ${HOME}/.postgres-data/:/var/lib/postgresql/data \
-p 5432:5432 \
postgres:latest
