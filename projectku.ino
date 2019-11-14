#include <FirebaseArduino.h>
#include <Servo.h>
#include <ESP8266WiFi.h>
#include <time.h>
#include <MD5Builder.h>

#define FIREBASE_HOST "smart-keran.firebaseio.com"
#define FIREBASE_AUTH "XbJKbquLTivFLDCrqxvNIvPRVahj3TpIFYoMlqYt"
//#define WIFI_SSID "Fernanda 2"
//#define WIFI_PASSWORD "lancarjaya"
#define WIFI_SSID "Bon Cabe"
#define WIFI_PASSWORD "12345678"

//init pin GPIO
#define servoPin 12 //D6
const int trigPin = 2;  //D4
const int echoPin = 0;  //D3

MD5Builder _md5;
String md5(String str) {
  _md5.begin();
  _md5.add(String(str));
  _md5.calculate();
  return _md5.toString();
}

//deklarasi waktu
const char* ntpServer      = "us.pool.ntp.org";
const long  gmtOffset_sec   = 3600*7;//GMT+7
const int   daylightOffset_sec  = 0;
char pukul[69], tanggal[69];


// defines variables
long duration;
int distance;

int volumeAirDb = 0;


const int otomatis = 0;
const int manual =1;
int jarak = 0; 

Servo servo; //instans

void setup() {
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT);
  Serial.begin(115200);
   WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    while (WiFi.status() != WL_CONNECTED) {

      Serial.print("not connected");
      
      delay(500);
      
      }

    //setup waktu
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  unsigned timeout = 5000;
  unsigned start = millis();
  while (!time(nullptr))
  {
    Serial.print(".");
    delay(1000);
  }

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  servo.attach(servoPin); //D
  servo.write(0);
  delay(2000);
}
void loop() {
//  set waktu
  time_t rawtime;
  struct tm * timeinfo;
  time (&rawtime);
  timeinfo = localtime (&rawtime);
  strftime (pukul, 80, "%H", timeinfo);
  strftime (tanggal, 80, "%A, %d %B %Y", timeinfo);
  
  Serial.println(WiFi.localIP());
  int status = Firebase.getInt("/keran_status");
  int servoStatus = servo.read();
  int modeControl = Firebase.getInt("/mode_control");

  String idHari = md5(tanggal);
  String idJam = pukul;
  int ketinggianAir = 200;

  digitalWrite(trigPin, LOW);
      delayMicroseconds(2);
      
      // Sets the trigPin on HIGH state for 10 micro seconds
      digitalWrite(trigPin, HIGH);
      delayMicroseconds(10);
      digitalWrite(trigPin, LOW);
      
      // Reads the echoPin, returns the sound wave travel time in microseconds
      duration = pulseIn(echoPin, HIGH);
      
      // Calculating the distance
      distance= (duration/2) / 29;
      // Prints the distance on the Serial Monitor
      Serial.print("Distance: ");
      Serial.println(distance);
      Serial.println("cm ");

  if (modeControl == manual){
       if( status == 1){
        Serial.println(servoStatus);
        if (servoStatus == 90){
          jarak  = distance;
          Serial.println("posisi nyala mode manual statusnya:");
          Serial.print(jarak); 
          }else{
            jarak  = distance;
            Serial.println("menyalakan...");
            Serial.print(jarak); 
            servo.write(90);
         
          }
        }
       if(status == 0){
        Serial.println(servoStatus);
        if (servoStatus == 0){
          jarak  = distance;
          Serial.println("posisi mati mode manual statusnya:");
          Serial.print(jarak); 
          }else{
            jarak  = distance;
            Serial.println("mematikan...");
            servo.write(0);
         
          }
        }
    
    }

  if (modeControl == otomatis){
      
      if(distance < 40 ){
          if (servoStatus == 0){
            jarak = distance;
            Serial.println("posisi mati mode otomatis statusnya:");
            Serial.print(status); 
            }else{
              jarak = distance;
              Serial.println("mematikan...");
              servo.write(0);
           
            }
        }
        if(distance > 150){
          
           if (servoStatus == 90){
              jarak = distance;
              Serial.println("posisi nyala mode otomatis statusnya:");
              Serial.print(distance); 
              }else{
                jarak = distance;
                Serial.println("menyalakan...");
            
                servo.write(90);
             
              }
          
          }

      
    }

    if(Firebase.failed()){
       Serial.println("gagal connect ke firebase.");
      
      }else{
          volumeAirDb = ketinggianAir - jarak;
          Firebase.setString("data/"+idHari+"/"+idJam+"/pukul", pukul);
          Firebase.setString("data/"+idHari+"/"+idJam+"/tanggal", tanggal);
          Firebase.setInt("data/"+idHari+"/"+idJam+"/volume_air", volumeAirDb);
          
      }
  
 
  
  delay(1000);
  
}
