# CallBlocker
Estä ulkomaanpuhelut sekä häirikkösoittajat

<img width="331" height="698" alt="image" src="https://github.com/user-attachments/assets/8c8619a5-a13a-47d2-9d66-1e60ee08e0c0" />

# 📞 CallBlocker (2026) — Androidin virallinen puhelunestopalvelu

CallBlocker on kevyt, turvallinen ja akkuystävällinen Android-sovellus ei-toivottujen puheluiden automaattiseen suodattamiseen. Sovellus käyttää Androidin virallista `CallScreeningService`-rajapintaa, joka tutkii saapuvat puhelut taustalla ja katkaisee ne ennen kuin puhelin ehtii edes hälyttää.

Asetusten määrittämisen jälkeen sovellusta ei tarvitse pitää auki — Android herättää palvelun automaattisesti aina, kun puhelu saapuu.

---

## 🔧 Ominaisuudet

* **Estä ulkomaiset puhelut:** Katkaisee kaikki ulkomailta tulevat puhelut yhdellä kytkimellä. Suomalaiset numerot (+358) sallitaan aina.
* **Sallitut maatunnukset (Poikkeukset):** Voit määrittää maakohtaisia poikkeuksia (esim. akoissa puhelua Ruotsista, voit sallia tunnuksen `+46`).
* **Kielletyt numerot & Sarjat:** Erillinen musta lista yksittäisille numeroille tai kokonaisille numerosarjoille (esim. puhelinmyyjien numeroblokit).
* **Sallitut numerot:** Valkoinen lista numeroille, joiden puhelut pääsevät aina läpi riippumatta muista estoehdoista.
* **Häirikkösoittajien esto:** Kytkin integroituu suoraan Androidin ja OnePlussan omaan globaaliin "Mahdollinen häirikkösoittaja" (Spam/Caller ID) -tunnistukseen hyödyntäen bittitason $0x00020000$ (`PROPERTY_ASSUMED_SPAM`) -maskia.
* **Automaattinen lennossa tallennus:** Sovelluksessa ei ole erillistä tallennuspainiketta. Kytkimet ja listat kirjoitetaan laitteen muistiin heti muutoksen hetkellä.
* **Älykäs kosketusohjaus listoilla:** Kaikissa alivalikoissa on kosketustuki – valitse numero listasta napauttamalla, jolloin se siirtyy tekstikenttään ja on helposti poistettavissa yhdellä klikkauksella.
* **Interaktiivinen estoloki:** Sovellus tallentaa reaaliaikaisen lokin aikaleimoineen ja syineen. Voit **napauttaa mitä tahansa lokiriviä** lisätäksesi numeron salamannopeasti joko sallittujen tai kiellettyihin listalle ilman käsin kirjoittamista.

---

## 🛠 Toimintalogiikka (CallScreeningService)

Sovellus pyytää Android-järjestelmältä `ROLE_CALL_SCREENING` -roolia. Kun rooli on myönnetty, sovellus toimii laitteen virallisena puhelunseulojana.

### Estojärjestys puhelun saapuessa:
1. **Sallitut numerot:** Jos numero on valkoisella listalla $\rightarrow$ **Sallitaan** välittömästi.
2. **Kielletyt numerot & Sarjat:** Jos numero täsmää mustaan listaan tai estokuvioon $\rightarrow$ **Estetään**.
3. **Ulkomaiset puhelut:** Jos esto on päällä, eikä numero ole suomalainen tai poikkeuslistalla $\rightarrow$ **Estetään**.
4. **Spam-suodatus:** Jos kytkin on päällä ja järjestelmä tunnistaa puhelun häiriköksi $\rightarrow$ **Estetään**.

> 💡 **Huomautus Androidin tietoturvasta:** Android suojelee oletuksena puhelimesi omia yhteystietoja (Contacts). Jos saapuva puhelu tulee numerosta, joka on tallennettu puhelimesi osoitekirjaan, Android luokittelee sen automaattisesti turvalliseksi eikä välitä sitä sovelluksen seulottavaksi. Sovellus suodattaa vain tuntemattomia numeroita.

---

## 🔢 Ohje: Kielletyt/Sallitut numerot ja numerosarjat

Sovellus tukee älykästä alkuosa-estoa (prefix-suodatusta). Sinun ei tarvitse syöttää peräkkäisiä numeroita yksitellen, vaan voit estää tai sallia kokonaisen numerosarjan (esim. puhelinmyyjien numeroblokit) kahdella eri tavalla:

### Tapa 1: Alue-syöttö (Range viivalla)
Voit kirjoittaa syötekenttään aloitusnumeron ja loppupäätteen viivalla erotettuna. Sovellus muuntaa sen automaattisesti kysymysmerkeiksi tallennushetkellä suorituskyvyn maksimoimiseksi.
* **Esimerkki syötteestä:** `+358401234500-999`
* **Mitä sovellus tekee:** Tallentaa listaan rivin `+358401234???`. Tämä kattaa ja estää kaikki 1000 numeroa väliltä `5000`–`5999`.

### Tapa 2: Kysymysmerkit (`?`) — Joustava pituus
Voit käyttää kysymysmerkkiä jokerimerkkinä kuvaamaan puuttuvia numeroita. Taustakoodi katsoo aina vain tekstiä **ennen ensimmäistä kysymysmerkkiä**, joten merkkien määrällä ei ole väliä:
* **Esimerkki 1 (Kymmenen numeroa):** Syötät `+358401234?`  
  $\rightarrow$ Järjestelmä ottaa talteen alkuosan `+358401234` ja suodattaa kaikki kymmenen numeroa väliltä `0`–`9`.
* **Esimerkki 2 (Sata/Tuhat numeroa):** Syötät `+358401234???`  
  $\rightarrow$ Järjestelmä suodattaa kaikki puhelut, jotka alkavat samalla `+358401234`-rungolla, olivatpa loput numerot mitä tahansa.

---

## 📋 Ohje: Interaktiivisen lokilistan käyttö

Jos huomaat estolokissa numeron, jonka eston haluat purkaa (tai haluat varmistaa numeron eston jatkossa), sinun ei tarvitse kirjoittaa numeroa käsin mihinkään.

1. Avaa **Näytä estoloki** sovelluksesta.
2. **Napauta** haluamaasi lokiriviä.
3. Ruudulle aukeaa valintaikkuna (*AlertDialog*), jossa on vaihtoehdot:
   * **Salli numero:** Lisää numeron automaattisesti *Sallitut numerot* -valkoiselle listalle.
   * **Estä numero:** Lisää numeron automaattisesti *Kielletyt numerot* -mustalle listalle.
   * **Peruuta:** Sulkee valikon tekemättä muutoksia.

---

## 🔒 Tietoturva ja Tekniikka

* **100 % Paikallinen:** Sovellus ei pyydä internet-oikeuksia (`INTERNET`), eikä se lähetä mitään tietoja laitteen ulkopuolelle. Kaikki lokit ja listat tallennetaan vain laitteen omaan sisäiseen muistiin.
* **Ei tietokantaraskautta:** Tiedot tallennetaan suorituskykyisesti Androidin `SharedPreferences`-välimuistiin käyttäen tiukat kriteerit täyttävää muistinallintaa. Tallennuksissa luodaan aina uudet `HashSet`-oliot, mikä pakottaa Androidin kirjoittamaan muutokset levylle lennossa ilman synkronointiongelmia.
* **Yhteensopivuus:** Yhteensopiva Android 10+ (API 29) laitteiden kanssa. Optimoitu toimimaan 100 % varmasti OnePlus Nord CE 3 Liten kanssa, kun oletussovelluksen rooli on aktivoitu.

---

## 📜 Lisenssi

Lisensoitu **GNU GPLv3** -lisenssillä. Lisenssi takaa käyttäjien vapaudet, koodin avoimuuden ja suojan. Katso tarkemmat ehdot: https://www.gnu.org/licenses/gpl-3.0.en.html

---

## 📱 Käyttöohje

### 1. Palvelun aktivointi
* Avaa sovellus ja käännä **Puhelunestopalvelu käytössä** -kytkin päälle.
* Android avaa järjestelmäikkunan, jossa pyydetään lupaa asettaa CallBlocker oletusarvoiseksi puhelunestosovellukseksi. Hyväksy pyyntö. Kytkin muuttuu **vihreäksi**.

### 🔋 TÄRKEÄÄ: Akun optimoinnin ohittaminen (OnePlus Nord CE 3 Lite)

OnePlus-laitteiden OxygenOS-käyttöjärjestelmä sisältää erittäin aggressiivisen virranhallinnan, joka sulkee taustalla odottavia sovelluksia säästääkseen akkua. Jos Android pääsee nukuttamaan CallBlockerin taustaprosessin, se ei ehdi reagoimaan puheluun sekunnin murto-osassa, jolloin häirikköpuhelu hälyttää virheellisesti läpi.

**Tee nämä määritykset asennuksen jälkeen suoraan puhelimestasi:**
1. Etsi **CallBlocker**-sovelluksen kuvake puhelimesi kotinäytöltä tai sovellusvalikosta.
2. Paina kuvaketta pitkään pohjassa ja valitse aukeavasta pikavalikosta **Sovelluksen tiedot** (App info / i-kuvake).
3. Etsi ja valitse kohta **Akun käyttö** (Battery usage) tai **Akku**.
4. Muuta oletusasetus *Optimoi automaattisesti* (Auto-optimize) tilaan **Älä optimoi** (Don't optimize) tai kytke päälle **Salli taustatoiminta** (Allow background activity).

### 2. Estojen määritys
* Kytke haluamasi estot (Ulkomaiset / Häirikkösoittajat) päälle. Kytkimet tallentuvat sekunnissa taustalle ja muuttuvat tilan mukaan **vihreiksi** tai **punaisiksi**.

### 3. Palvelun poistaminen
Android-tietoturvan vuoksi oletussovelluksen roolia ei voi sulkea suoraan koodilla sovelluksen sisältä. Kun käännät kytkimen pois päältä, sovellus opastaa sinua siirtymään Androidin asetuksiin:
1. Avaa puhelimen **Asetukset** $\rightarrow$ **Sovellukset** $\rightarrow$ **Oletussovellukset**.
2. Etsi listalta kohta **Soittajan tunnus ja häiriök.** (OxygenOS-versiossa nimellä *Puhelunestosovellus* tai *Caller ID & spam app*).
3. Klikkaa sitä, ja vaihda valinta *CallBlocker* $\rightarrow$ **Puhelin** (järjestelmän oletus, sininen luuri-ikoni).

---

## 🧪 Testaus emulaattorilla

Koska emulaattoreiden perussyötteet eivät sisällä Googlen spamtunnisteita, voit testata kytkimen ja järjestelmän välistä kommunikaatiota Android Studion **Terminalissa** (PowerShell) pakottamalla puhelulähteeksi järjestelmän häirikköstatuksen:

Käynnistä emulaattori (esim. API 34) ja aja komento:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start-activity -a android.intent.action.CALL -d tel:0401234567 --ei android.telecom.extra.CALL_SOURCE 2

---


