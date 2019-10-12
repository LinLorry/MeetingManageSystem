FROM java:8

COPY target/MeetingManageSystem-1.0-SNAPSHOT.jar /MeetingManageSystem.jar

EXPOSE 8080

CMD ["java", "-jar", "MeetingManageSystem.jar"]