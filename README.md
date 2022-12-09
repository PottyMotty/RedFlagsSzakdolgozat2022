# Szakdolgozat-RedFlags
Szűcs Ádám a Budapesti Műszaki Egyetem hallgatójának szakdolgozata.

## Android kliens telepítése:

A kliens telepíthető a Setup files mappában található .apk telepítő fájl használatával, vagy a forráshód készülékre való fordításával. A fordításhoz Java 11 szükséges, valamint a telepítendő készüléknek legalább SDK 24-es Android verziót kell futtatnia.
A kliens lokális teszteléséhez a bme.spoti.redflags.other.Constants fájlban található USE_LOCALHOST változó értékét kell átírni a megfelelő értékre. Ezután a HTTP_BASE_URL_LOCALHOST és WS_BASE_URL_LOCALHOST értékeit kell beállítani a következők szerint:
- Amenyiben emulátoron keresztül szeretnénk tesztelni az [IP_ADDRESS] karaktersorozat helyett 10.0.2.2. címet kell megadni.
- Fizikai készüléken való tesztelés esetén [IP_ADDRESS] értékét a szerver programot futtató gép ip címére kell állítani.

## Szerver oldal telepítése:

A szerver oldal könnyű telítéséhez egy Docker konfiguráció szolgál amely amellett, hogy a szervert feltelepíti és elindítja, létrehozza a konténeren belül a szükséges adatbázist is és azt feltölti a teszteléshez szükséges adatokkal. A futtatásának a menete a következő:

- Docker program telepítése a futató gépre
- A szerver gyökér mappájába nyissunk egy új terminált
- Generáljuk le a .jar fájlt a következő parancsal: ./gradlew buildFatJar
- Majd adjuk ki a következő parancsot: docker compose up

A Dokker konfigurációért felelős docker-compose.yml fájlban található DEV környezeti változó átírásával változtathatjuk, hogy az éles vagy a lokális adatbázist szeretnénk használni.

A könnyű tesztelhetőség érdekében mind az adatbázis mind a szerver oldal hosztolva van. A szerver az alábbi URL-en érhető el: https://bmevik-redflags.herokuapp.com/ 
A kliens alapértelmezetten ezt használja.

