# CallBlocker
Estä ulkomaanpuhelut sekä häirikkösoittajat

<img width="331" height="698" alt="image" src="https://github.com/user-attachments/assets/8c8619a5-a13a-47d2-9d66-1e60ee08e0c0" />

# 📞 CallBlocker (2026) — Androidin virallinen puhelunestopalvelu

CallBlocker on kevyt, turvallinen ja akkuystävällinen Android-sovellus ei-toivottujen puheluiden automaattiseen suodattamiseen. Sovellus käyttää Androidin virallista `CallScreeningService`-rajapintaa, joka tutkii saapuvat puhelut taustalla ja katkaisee ne ennen kuin puhelin ehtii edes hälyttää.

Asetusten määrittämisen jälkeen sovellusta ei tarvitse pitää auki — Android herättää palvelun automaattisesti aina, kun puhelu saapuu.

---

## 🔧 Ominaisuudet

* **Estä ulkomaiset puhelut:** Katkaisee kaikki ulkomailta tulevat puhelut yhdellä kytkimellä. Suomalaiset numerot (+358) sallitaan aina.
* **Sallitut maatunnukset (Poikkeukset):** Voit määrittää maakohtaisia poikkeuksia (esim. jos odotat puhelua Ruotsista, voit sallia tunnuksen `+46`).
* **Kielletyt numerot & Sarjat:** Erillinen musta lista yksittäisille numeroille tai kokonaisille numerosarjoille (esim. puhelinmyyjien numeroblokit).
* **Sallitut numerot:** Valkoinen lista numeroille, joiden puhelut pääsevät aina läpi riippumatta muista estoehdoista.
* **Häirikkösoittajien esto:** Valmius Androidin oman "Mahdollinen häirikkösoittaja" (Spam/Caller ID) -tunnistuksen hyödyntämiseen.
* **Älykäs kosketusohjaus listoilla:** Kaikissa alivalikoissa on kosketustuki – valitse numero listasta napauttamalla, jolloin se siirtyy tekstikenttään ja on helposti poistettavissa yhdellä klikkauksella.
* **Interaktiivinen estoloki:** Sovellus tallentaa reaaliaikaisen lokin aikaleimoineen ja syineen. Voit **napauttaa mitä tahansa lokiriviä** lisätäksesi numeron salamannopeasti joko sallittujen tai kiellettyihin listalle ilman käsin kirjoittamista.

---

## 🛠 Toimintalogiikka (CallScreeningService)

Sovellus pyytää Android-järjestelmältä `ROLE_CALL_SCREENING` -roolia. Kun rooli on myönnetty, sovellus toimii laitteen virallisena puhelunseulojana.

### Estojärjestys puhelun saapuessa:
1. **Sallitut numerot:** Jos numero on valkoisella listalla $\rightarrow$ **Sallitaan** välittömästi.
2. **Kielletyt numerot & Sarjat:** Jos numero täsmää mustaan listaan tai estokuvioon $\rightarrow$ **Estetään**.
3. **Ulkomaiset puhelut:** Jos esto on päällä, eikä numero ole suomalainen tai poikkeuslistalla $\rightarrow$ **Estetään**.
4. **Spam-suodatus:** Jos puhelu tunnistetaan häiriköksi $\rightarrow$ **Estetään**.

> 💡 **Huomautus Androidin tietoturvasta:** Android suojelee oletuksena puhelimesi omia yhteystietoja (Contacts). Jos saapuva puhelu tulee numerosta, joka on tallennettu puhelimesi osoitekirjaan, Android luokittelee sen automaattisesti turvalliseksi eikä välitä sitä sovelluksen seulottavaksi. Sovellus suodattaa vain tuntemattomia numeroita.

---

## 🔢 Ohje: Kielletyt/Sallitut numerot ja numerosarjat

Sovellus tukee älykästä alkuosa-estoa (prefix-suodatusta). Sinun ei tarvitse syöttää peräkkäisiä numeroita yksitellen, vaan voit estää tai sallia kokonaisen numerosarjan (esim. 10, 100 tai 1000 peräkkäistä puhelinmyyntinumeroa) erittäin joustavasti kahdella eri tavalla:

### Tapa 1: Alue-syöttö (Range viivalla)
Voit kirjoittaa syötekenttään aloitusnumeron ja loppupäätteen viivalla erotettuna. Sovellus muuntaa sen automaattisesti kysymysmerkeiksi tallennushetkellä suorituskyvyn maksimoimiseksi.
* **Esimerkki syötteestä:** `+358401234500-999`
* **Mitä sovellus tekee:** Tallentaa listaan rivin `+358401234???`. Tämä kattaa ja estää kaikki 1000 numeroa väliltä `5000`–`5999`.

### Tapa 2: Kysymysmerkit (`?`) — Joustava pituus
Voit käyttää kysymysmerkkiä jokerimerkkinä kuvaamaan puuttuvia numeroita. Taustakoodi katsoo aina vain tekstiä **ennen ensimmäistä kysymysmerkkiä**, joten merkkien määrällä ei ole väliä:
* **Esimerkki 1 (Kymmenen numeroa):** Syötät `+358401234?`  
  $\rightarrow$ Järjestelmä ottaa talteen alkuosan `+358401234` ja suodattaa kaikki kymmenen numeroa väliltä `0`–`9` (esim. `...340`, `...341`, `...349`).
* **Esimerkki 2 (Sata/Tuhat numeroa):** Syötät `+358401234???`  
  $\rightarrow$ Järjestelmä suodattaa kaikki puhelut, jotka alkavat samalla `+358401234`-rungolla, olivatpa loput numerot mitä tahansa.

---

## 📋 Ohje: Interaktiivisen lokilistan käyttö

Jos huomaat estolokissa numeron, jonka eston haluat purkaa (tai haluat varmistaa numeron eston jatkossa), sinun ei tarvitse kirjoittaa numeroa käsin mihinkään.

1. Avaa **Estoloki** sovelluksesta.
2. **Napauta** haluamaasi lokiriviä (esim. `[10.07.2026 12:45:00] +358401234567 estetty...`).
3. Ruudulle aukeaa valintaikkuna (*AlertDialog*), jossa on vaihtoehdot:
   * **Salli numero:** Lisää numeron automaattisesti *Sallitut numerot* -valkoiselle listalle.
   * **Estä numero:** Lisää numeron automaattisesti *Kielletyt numerot* -mustalle listalle.
   * **Peruuta:** Sulkee valikon tekemättä muutoksia.

---

## 📱 Käyttöohje

### 1. Palvelun aktivointi
* Avaa sovellus ja käännä **Puhelunestopalvelu käytössä** -kytkin päälle.
* Android avaa järjestelmäikkunan, jossa kysytään lupaa asettaa CallBlocker oletusarvoiseksi puhelunestosovellukseksi. Hyväksy pyyntö. Kytkin muuttuu **vihreäksi**.

### 2. Estojen määritys
* Kytke haluamasi estot (Ulkomaiset / Spam) päälle.
* Lisää tarvittavat numerot tai kysymysmerkkisarjat listoille.
* **Muista painaa "Tallenna asetukset" -painiketta**, jotta asetukset kirjoitetaan laitteen muistiin!

### 3. Palvelun poistaminen (OnePlus Nord CE 3 Lite)
Android-tietoturvan vuoksi oletussovelluksen roolia ei voi sulkea suoraan koodilla sovelluksen sisältä. Kun käännät kytkimen pois päältä, sovellus ohjaa sinut Androidin asetuksiin.

**Vaiheittainen ohje OnePlus-laitteille:**
1. Avaa puhelimen **Asetukset** (ratasikoni).
2. Skrollaa alaspäin ja valitse **Sovellukset** (Apps).
3. Valitse heti ylhäältä **Oletussovellukset** (Default apps).
4. Etsi listalta kohta **Puhelunestosovellus** (OxygenOS-versiosta riippuen nimellä *Häiriöpuhelut ja roska-asetukset* tai *Caller ID & spam app*).
5. Klikkaa sitä, ja vaihda valinta *CallBlocker* $\rightarrow$ **Puhelin** (järjestelmän oletus, sininen luuri-ikoni).
6. Kun palaat sovellukseen, pääkytkin on sammunut ja muuttunut **punaiseksi**.

---

## 🧪 Testaus emulaattorilla

Koska sovellus ei tietoturvasyistä koske puhelimen muistiin tallennettuihin tuttuihin kontakteihin, testaus kannattaa tehdä Android Studion emulaattorilla:

1. Varmista Logcatista, että sovellus sai roolin (`onResume: Role held = true`).
2. Avaa emulaattorin sivupaneelista kolme pistettä (`...`) $\rightarrow$ **Phone**.
3. Syötä *From number* -kenttään jokin tuntematon numero (esim. ulkomainen numero `+447123456` tai estämääsi sarjaan kuuluva numero `+358401234555`).
4. Paina **Call Device**.
5. Seuraa Logcatia: sinne pitäisi ilmestyä `CallScreeningService STARTED` ja `CALL BLOCKED`, ja puhelu katkeaa automaattisesti. Lokasivulle ilmestyy siisti pvm/klo-merkintä estosta.

---

## 🔒 Tietoturva ja Tekniikka

* **100 % Paikallinen:** Sovellus ei pyydä internet-oikeuksia (`INTERNET`), eikä se lähetä mitään tietoja laitteen ulkopuolelle. Kaikki lokit ja listat tallennetaan vain laitteen omaan sisäiseen muistiin.
* **Ei tietokantaraskautta:** Tiedot tallennetaan suorituskykyisesti Androidin `SharedPreferences`-välimuistiin käyttäen tiukat kriteerit täyttävää muistinallintaa. Tallennuksissa luodaan aina uudet `HashSet`-oliot, mikä pakottaa Androidin kirjoittamaan muutokset levylle lennossa ilman synkronointiongelmia.
* **Yhteensopivuus:** Yhteensopiva Android 10+ (API 29) laitteiden kanssa. Optimoitu toimimaan 100 % varmasti OnePlus Nord CE 3 Liten kanssa, kun oletussovelluksen rooli on aktivoitu.

---

## 📜 Lisenssi

Lisensoitu **GNU GPLv3** -lisenssillä. Lisenssi takaa käyttäjien vapaudet, koodin avoimuuden ja suojan. Katso tarkemmat ehdot: https://www.gnu.org/licenses/gpl-3.0.en.html
Lisensoitu **GNU GPLv3** -lisenssillä. Lisenssi takaa käyttäjien vapaudet, koodin avoimuuden ja suojan. Katso tarkemmat ehdot: https://www.gnu.org/licenses/gpl-3.0.en.html
