FROM java:8

WORKDIR /
ADD build/libs/youtubebot-0.0.1-SNAPSHOT.jar youtubebot-0.0.1-SNAPSHOT.jar

CMD java -jar youtubebot-0.0.1-SNAPSHOT.jar