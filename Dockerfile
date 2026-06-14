FROM eclipse-temurin:21-jre
WORKDIR /app

LABEL authors="dev-dhama"

ENTRYPOINT ["top", "-b"]