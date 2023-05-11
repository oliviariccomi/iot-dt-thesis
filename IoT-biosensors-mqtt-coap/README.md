# IoT-biosensors-mqtt-coap
The aim of this project is to simulate a board which detects different types of biosignals such as ECG, EMG, EDA and EEG, creating then packets with all of data to publish through an MQTT mosquitto broker and CoAP server too.
The data right now are read from a csv file, then collected into a single Smart Object and thus sent to a Client which receives the data.
You might decide to use CoAP protocol or MQTT protocol and use the respective protocol.

## Prerequisites
* **Any hardware** that could detect biosignals. I am using BITalino (r)evolution Plugged Kit but also other boards such as Arduino-based e-Health Sensor Platform are fine;
* **Eclipse-Californium** library to use CoaP;
* **Docker** with mosquitto broker (only if you want to use MQTT). Necessary to set the connection. It can be downloaded through https://hub.docker.com/_/eclipse-mosquitto;
* **Eclipse-paho** library to use MQTT protocol;


## MQTT 
I'm working with local MQTT Broker through Docker. Both server and client agents will communicate through the centralized docker server.

### Dependencies
 ```
 <dependency>
   <groupId>org.eclipse.paho</groupId>
   <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
   <version>1.2.5</version>
</dependency>
```

### Topics
The MQTT Publisher sends data to the following topics. In order to receive the desired data, the MQTT Clients has to subscribe to one of those topics.

`/iot/biosensors/boardId/#` related to all data from all sensors.   
`/iot/biosensors/boardId/sensor` related to the specific sensor data.


## CoAP 
**WORK IN PROGRESS**

### Dependencies
 ```
<dependency>
   <groupId>org.eclipse.californium</groupId>
   <artifactId>californium-core</artifactId>
   <version>2.4.1</version>
</dependency>
```


## Message formats
* Json
 ```
 {macAddress='88:6B:0F:F1:94:16', type='ECG', seqId=4, dig1=1, dig2=1, dig3=0, dig4=0, frequency=10, value=496.0, timeS=1607444749513}
 ```
* SenML 
  
 ```
 [
   {"bn":"88:6B:0F:F1:94:16", "n":"ECG", "s":1, "f":100, "u":"Hz", "v":-0,023637, "t":1564737},
   {"n":"EDA", "s":1, "f":100, "u":"Hz", "v":2,023637, "t":1634521},
   {"n":"EMG", "s":1, "f":100, "u":"Hz", "v":3,023637, "t":1534274},
]
 ``` 

## Repository directory structure
* Client: contains CoaP Clients
  * *CoapAutomaticDataFetcher*: this clients subscribes to the topic `/.well-known/core` 
  * *CoapGetClientProcess*: this clients subscribes to the specific resource topic and received data to the GET in json format;
  * *CoapGetSenmlClientProcess*: this clients subscribes to the specific resource topic and received data to the GET in SenML format; 
* Resources
  * Raw: sensors that produce biosegnals data;
  * CoAP resources
* Process
  * CoAP process & MQTT process where the connection is established;
* Message: I create a package with data that has to be sent
* Device
  * MQTTSmartObject: collects the sensors in an HashMap and update the values
* Descriptors
  * Sensors descriptors to collect the data from a .csv file
* Consumer
  * Simple Test Consumer (receiver that subscribes to topics and collect data)


# Application overview
![Schermata 2021-05-20 alle 22 48 16](https://user-images.githubusercontent.com/71649032/119047247-f082e600-b9bd-11eb-99e9-29be944e7f82.jpg)

