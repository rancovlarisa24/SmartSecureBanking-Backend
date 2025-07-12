# [RO] Smart Secure Banking - Ghid de Configurare

Acest fișier descrie pașii de instalare și configurare ai aplicației **Smart Secure Banking (SSB)**, alcătuită dintr-un front-end mobil **React Native** și un back-end **Spring Boot**, plus module externe (MySQL, Twilio, PayPal, OpenAI, Blockchain).

---

## 1. Front-end și Back-end

| Componentă        | Cerință minimă | Observații                                   |
|-------------------|---------------|-----------------------------------------------|
| Node.js           | 20 LTS        | necesar pentru React Native                   |
| React Native CLI  | ≥ 0.74        | instalare globală: `npm i -g react-native-cli`|
| Android SDK       | API 33 +      | emulator sau dispozitiv fizic                 |
| JDK               | 21            | compatibil cu Spring Boot                     |
| Maven / Gradle    | Maven ≥ 3.9, Gradle ≥ 8    |      oricare dintre ele         |                                             

Pași de rulare locală: 

**Front-end**

- `cd proiect/SSBfrontend`
- `npm install`
- `npx react-native run-android` pornește aplicația pe emulator / telefon


**Back-end**

- `cd proiect/SSBbackend`
- `mvn spring-boot:run`  ori `./gradlew bootRun`

Serverul rulează pe http://localhost:8080 .

Front-end-ul comunică prin REST cu back-end-ul pe rutele http://localhost:8080/api/... .


## 2. Bază de date(MySQL)

În implementarea de referință a fost utilizat Amazon RDS, însă configurația funcționează și cu o instalare locală.
1. Crearea unei baze de date.
2. Definirea parametrilor în SSBbackend/src/main/resources/application.properties:
- `spring.datasource.url=jdbc:mysql://<HOST>:3306/ssb?createDatabaseIfNotExist=true&serverTimezone=UTC`
- `spring.datasource.username=<DB_USER>`
- `spring.datasource.password=<DB_PASS>`
- `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`
3. Asigurarea accesului la portul 3306 și a drepturilor de creare / modificare pentru utilizatorul definit.


## 3. Twilio(SMS) și Spring Boot Mail(SMTP)

### 3.1 Twilio

1. Crearea unui cont pe Twilio și obținerea valorilor Account SID și Auth Token.
2. Alegerea sau achiziționarea unui număr Twilio.
3. Configurarea proprietăților în TwilioService:
- `twilio.account-sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- `twilio.auth-token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- `twilio.phone-number=+12025550123`
### 3.2 SMTP Gmail

1. Activarea „2-Step Verification” pe contul Gmail.
2. Generarea unei parole de aplicație de 16 caractere.
3. Adăugarea proprietăților SMTP în SSBbackend/src/main/resources/application.properties:
- `spring.mail.host=smtp.gmail.com`
- `spring.mail.port=587`
- `spring.mail.username=utilizator@gmail.com`
- `spring.mail.password=parola-aplicatie-16car`
- `spring.mail.properties.mail.smtp.auth=true`
- `spring.mail.properties.mail.smtp.starttls.enable=true`


## 4. PayPal(Sandbox)

1. Crearea unei aplicații sandbox în developer.paypal.com → My Apps & Credentials.
2. Copierea valorilor Client ID și Secret.
3. Definirea cheilor în SSBbackend/src/main/resources/application.properties:
- `paypal.mode=sandbox`
- `paypal.clientId=AYabc...123`
- `paypal.clientSecret=EOxyz...987`


## 5. OpenAI

1. Generarea unei chei API în dashboard-ul OpenAI → API Keys.
2. Setarea proprietăților în SSBbackend/src/main/resources/application.properties: 
- `openai.api.key=sk-abc123456789`
- `openai.api.url=https://api.openai.com/v1/chat/completions`

Cheia API rămâne confidențială; costurile depind de volumul solicitărilor.


## 6. Blockchain

1. Instalarea și pornirea Ganache pe http://127.0.0.1:7545: `ganache --port 7545`
2. Actualizarea proiect/blockchain/hardhat.config.js:
- `networks: { local: { url: "http://127.0.0.1:7545", accounts: [ "0x<PRIVATE_KEY_1>", "0x<PRIVATE_KEY_2>"]}}`
3. Compilarea și implementarea contractului:
- `cd proiect/blockchain`
- `npm install`
- `npx hardhat compile`
- `npx hardhat run scripts/deploy.js --network local`
4. Introducerea adresei contractului în back-end în BlockchainService: `contractAddress=0x1234...abcd`


# [EN] Smart Secure Banking - Configuration Guide

This document outlines the installation and configuration steps for the **Smart Secure Banking (SSB)** application, which consists of a mobile front-end built with **React Native** and a back-end built with **Spring Boot**, along with auxiliary modules (MySQL, Twilio, PayPal, OpenAI, Blockchain).

---

## 1. Front-end și Back-end

| Component        | Minimum version | Notes                                                |
|------------------|-----------------|------------------------------------------------------|
| Node.js          | 20 LTS          | required by React Native                             |
| React Native CLI | ≥ 0.74          | global install: `npm i -g react-native-cli`          |
| Android SDK      | API 33 +        | physical device or emulator                          |
| JDK              | 21              | compatible with Spring Boot                          |
| Maven / Gradle   | Maven ≥ 3.9, Gradle ≥ 8     | either build tool may be used                        |
                                                            

Local run:

**Front-end**

- `cd proiect/SSBfrontend`
- `npm install`
- `npx react-native run-android`     launches the mobile app

**Back-end**

- `cd proiect/SSBbackend`
- `mvn spring-boot:run`             or `./gradlew bootRun`

The server listens on http://localhost:8080 .

The front-end communicates with the back-end over REST at http://localhost:8080/api/... .


## 2. Database(MySQL)

The reference implementation targets Amazon RDS, but an identical configuration works with a local MySQL instance.
1. Create a database.
2. Define the parameters in SSBbackend/src/main/resources/application.properties:
- `spring.datasource.url=jdbc:mysql://<HOST>:3306/ssb?createDatabaseIfNotExist=true&serverTimezone=UTC`
- `spring.datasource.username=<DB_USER>`
- `spring.datasource.password=<DB_PASS>`
- `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`
3. Ensure port 3306 is reachable and the specified account has create/modify rights.


## 3. Twilio(SMS) and Spring Boot Mail(SMTP)

### 3.1 Twilio

1. Create a Twilio account and obtain the Account SID and Auth Token.
2. Choose or purchase a Twilio phone number.
3. Configure the properties in TwilioService:
- `twilio.account-sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- `twilio.auth-token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- `twilio.phone-number=+12025550123`

### 3.2 SMTP Gmail

1. Enable 2-Step Verification for the Gmail account.
2. Generate a 16-character App Password.
3. Add the SMTP properties in SSBbackend/src/main/resources/application.properties:
- `spring.mail.host=smtp.gmail.com`
- `spring.mail.port=587`
- `spring.mail.username=user@gmail.com`
- `spring.mail.password=app-password-16chars`
- `spring.mail.properties.mail.smtp.auth=true`
- `spring.mail.properties.mail.smtp.starttls.enable=true`


## 4. PayPal(Sandbox)

1. Create a sandbox application at developer.paypal.com → My Apps & Credentials.
2. Copy the Client ID and Secret.
3. Define the keys in SSBbackend/src/main/resources/application.properties:
- `paypal.mode=sandbox`
- `paypal.clientId=AYabc...123`
- `paypal.clientSecret=EOxyz...987`


## 5. OpenAI

1. Generate an API key in the OpenAI dashboard → API Keys.
2. Set the properties in SSBbackend/src/main/resources/application.properties:
- `openai.api.key=sk-abc123456789`
- `openai.api.url=https://api.openai.com/v1/chat/completions`

The API key remains confidential; usage costs depend on request volume.


## 6. Blockchain

1. Install and start Ganache on http://127.0.0.1:7545: `ganache --port 7545`
2. Update proiect/blockchain/hardhat.config.js:
- `networks: { local: { url: "http://127.0.0.1:7545", accounts: [ "0x<PRIVATE_KEY_1>", "0x<PRIVATE_KEY_2>"]}}`
3. Compile and deploy the smart contract:
- `cd proiect/blockchain`
- `npm install`
- `npx hardhat compile`
- `npx hardhat run scripts/deploy.js --network local`
4. Add the deployed contract address in the back-end (BlockchainService): `contractAddress=0x1234...abcd`



