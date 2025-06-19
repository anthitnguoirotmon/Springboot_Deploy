#Stage 1 : build
#Lấy image trên docker hub chứa JDK21 và Maven
FROM maven:3.9.8-amazoncorretto-21 AS build
#và thêm cả 2 vào thư mục app mình tạo
WORKDIR /app
#Copy pom.xml ở thư mục cùng vs dockerfile (có dấu chấm)
COPY pom.xml .
#Copy src trong /src do nhỏ cấp hơn dockerfile nên không có dấu .
COPY src ./src
#Tiến hành build bỏ qua test trong java
RUN mvn package -DskipTests
#Stage 2: Create image
#Lấy 1 image chỉ có JDK21 (không có maven để mình chạy app không cần maven -> giảm tài nguyen)
FROM amazoncorretto:21.0.4

WORKDIR /app
#copy file .jar trong /app/target trong build và đổi tên thành app.jar và thêm vào thư mục app vừa tạo
COPY --from=build /app/target/*.jar app.jar
#Chỉ dẫn cho docker
#Tương đương với câu lênh java -jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]


