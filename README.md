# SignYourApk

[logo](logo.svg)

## apksigner tool Android App

[Sign it](Screenshot.png)


Original source code :

https://deb.debian.org/debian/pool/main/a/android-platform-tools-apksig/android-platform-tools-apksig_31.0.2.orig.tar.gz


https://www.bouncycastle.org/download/bouncy-castle-java/


echo sudo apt install zip apksigner zipalign

cp app-release.apk original.zip
rm -rf ./app
unzip original.zip -d app
cp -r ./new/* ./app/

(cd app && zip --exclude resources.arsc -r ../app.zip * && zip -0 ../app.zip resources.arsc)
 
mv app.zip app_tmp.apk

if [ ! -f cert.jks ]; then
 keytool -genkey -keyalg RSA -alias cert -keystore cert.jks -storepass 12345678 -validity 360
fi

rm -f app.apk

zipalign -p 4 app_tmp.apk app.apk

echo PASSWORD is 12345678
apksigner sign --min-sdk-version 16 --ks cert.jks app.apk

apksigner verify app.apk



-----------------
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe"  -genkey -keyalg RSA -alias cert -keystore cert.bks -storepass 12345678 -validity 36000 -storetype BKS  -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath C:\Users\jml\Downloads\bcprov-jdk18on-1.78.1.jar
----------------
