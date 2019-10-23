FROM java:8

EXPOSE 8080

COPY target/MeetingManageSystem-1.0-SNAPSHOT.jar /MeetingManageSystem.jar

CMD ["java", "-jar", "MeetingManageSystem.jar"]