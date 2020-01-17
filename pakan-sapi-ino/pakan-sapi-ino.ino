#include <FirebaseArduino.h>
#include <Servo.h>
#include <ESP8266WiFi.h>
#include <time.h>
#include <MD5Builder.h>

#define FIREBASE_HOST "pakan-sapi.firebaseio.com"
#define FIREBASE_AUTH "MSsrAfBLQtktxhdBATfwXE30GoVPFo0O4EndcKWa"
//#define WIFI_SSID "Fernanda 2"
//#define WIFI_PASSWORD "lancarjaya"
#define WIFI_SSID "WIFI.HOME"
#define WIFI_PASSWORD "esjerukseger"
#define kode_kandang "K01" //konfigurasi id pakan
//init pin GPIO
#define relayPin 12 //D6

int menitMesin = 1; //setting nyala mesin dalam satuan menit


MD5Builder _md5;
String md5(String str) {
  _md5.begin();
  _md5.add(String(str));
  _md5.calculate();
  return _md5.toString();
}

//deklarasi waktu
const char* ntpServer      = "id.pool.ntp.org";
const long  gmtOffset_sec   = 3600*7;//GMT+7
const int   daylightOffset_sec  = 0;
char pukul[69], tanggal[69];


long startTime = 0;



void setup() {
  pinMode(relayPin, OUTPUT); // Sets the relay as an Output

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
  mesinOff();

  delay(2000);
}
void loop() {
//  set waktu
  long currentTime = millis();
  time_t rawtime;
  struct tm * timeinfo;
  time (&rawtime);
  timeinfo = localtime (&rawtime);
  strftime (pukul, 80, "%H", timeinfo);
  strftime (tanggal, 80, "%A, %d %B %Y", timeinfo);
  int jam_mati = Firebase.getInt("/jam_mati");
  int mesinBase =  Firebase.getInt("/"+String(kode_kandang)+"/setting/nyala_mesin");
  long nyalaMesin = mesinBase * 60 * 1000 ;
  Serial.println(tanggal);

  int waktu = atol(pukul);

  String idHari = md5(tanggal);
  String idJam = pukul;
  bool isAuto = Firebase.getBool("/"+String(kode_kandang)+"/is_auto");
  int curJam = atol(pukul);
  int jam = Firebase.getInt("/"+String(kode_kandang)+"/jam_makan/"+String(curJam)+"/jam");
  
  if (isAuto){
    Serial.println("auto");
     if (startTime < 1){
     if (jam == curJam){
      startTime = millis();
      mesinOn();
      addMonitoring(pukul, tanggal, true);
      Serial.print("waktu jika time sama: ");
      Serial.println(startTime);      
    }
   }

   long stopTime = currentTime - startTime;
   if(stopTime > nyalaMesin){
      Serial.println("auto mode off");
        
        mesinOff();
      }

    if (jam != curJam){
     startTime = 0;
      //addMonitoring(pukul, tanggal, false);
      mesinOff();
      Serial.print("mengembalikan waktu ");
      Serial.println(startTime);
     } 

    
  }else{
    if (getMesin()){
      Serial.println("manual mode on");
      mesinOn();
      addMonitoring(pukul, tanggal, true);
      }else{
        Serial.println("manual mode off");
        
        mesinOff();
        }
    
  }

  

  
  delay(5000);
  
}

void addMonitoring(char* pukul, String tanggal,bool stts){
          String kd = kode_kandang;
          String idHari = md5(tanggal);
          String idJam = String(pukul);
          int waktu = atol(pukul);
          Firebase.setInt("/"+ kd +"/status_makan/"+idHari+"/"+idJam+"/jam", waktu);
          Firebase.setBool("/"+ kd +"/status_makan/"+idHari+"/"+idJam+"/status", stts);
          Firebase.setString("/"+ kd +"/status_makan/"+idHari+"/"+idJam+"/tanggal", tanggal);          
  }

void setMesin (bool stts){
    String kd = kode_kandang;
    Firebase.setBool("/"+ kd +"/is_active", stts);
  }

 bool getMesin (){
    String kd = kode_kandang;
    bool msn = Firebase.getBool("/"+ kd +"/is_active");
    return msn;
  }

 void mesinOn(){
  digitalWrite(relayPin, 1);
  setMesin(true);
  }
 void mesinOff(){
  digitalWrite(relayPin, 0);
  setMesin(false);
  }
