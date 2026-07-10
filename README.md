# CallBlocker
Estä ulkomaanpuhelut sekä häirikkösoittajat


<img width="331" height="698" alt="image" src="https://github.com/user-attachments/assets/8c8619a5-a13a-47d2-9d66-1e60ee08e0c0" />


# 📞 CallBlocker (2026) — Androidin virallinen puhelunestopalvelu

CallBlocker on kevyt, turvallinen ja akkuystävällinen Android-sovellus ei-toivottujen puheluiden automaattiseen suodattamiseen. Sovellus käyttää Androidin virallista `CallScreeningService`-rajapintaa, joka tutkii saapuvat puhelut taustalla ja katkaisee ne ennen kuin puhelin ehtii edes hälyttää.

Asetusten määrittämisen jälkeen sovellusta ei tarvitse pitää auki — Android herättää palvelun automaattisesti aina, kun puhelu saapuu.

---

## 🔧 Ominaisuudet

* **Estä ulkomaiset puhelut:** Katkaisee kaikki ulkomailta tulevat puhelut yhdellä kytkimellä. Suomalaiset numerot (+358) sallitaan aina.
* **Sallitut maatunnukset (Poikkeukset):** Voit määrittää maakohtaisia poikkeuksia (esim. jos odotat puhelua kaverilta Ruotsista, voit sallia tunnuksen `+46`).
* **Kielletyt numerot:** Erillinen musta lista numeroille, jotka haluat estää aina.
* **Sallitut numerot:** Valkoinen lista numeroille, joiden puhelut pääsevät aina läpi riippumatta muista estoehdoista.
* **Häirikkösoittajien esto:** Valmius Androidin oman "Mahdollinen häirikkösoittaja" (Spam/Caller ID) -tunnistuksen hyödyntämiseen.
* **Selkeä estoloki:** Sovellus tallentaa reaaliaikaisen, selkeän ja ihmisluettavan lokin kaikista estetyistä puheluista aikaleimoineen ja syineen.
* **Kaksirivinen Spam-raportti:** Erillinen, siisti näkymä tunnistetuille häiriösoittajille.

---

## 🛠 Toimintalogiikka (CallScreeningService)

Sovellus pyytää Android-järjestelmältä `ROLE_CALL_SCREENING` -roolia. Kun rooli on myönnetty, sovellus toimii laitteen virallisena puhelunseulojana.

### Estojärjestys puhelun saapuessa:
1. **Sallitut numerot:** Jos numero on valkoisella listalla $\rightarrow$ **Sallitaan** välittömästi.
2. **Kielletyt numerot:** Jos numero on mustalla listalla $\rightarrow$ **Estetään**.
3. **Ulkomaiset puhelut:** Jos esto on päällä, eikä numero ole suomalainen tai poikkeuslistalla $\rightarrow$ **Estetään**.
4. **Spam-suodatus:** Jos puhelu tunnistetaan häiriköksi $\rightarrow$ **Estetään**.

> 💡 **Huomautus Androidin tietoturvasta:** Android suojelee oletuksena puhelimesi omia yhteystietoja (Contacts). Jos saapuva puhelu tulee numerosta, joka on tallennettu puhelimesi osoitekirjaan, Android luokittelee sen automaattisesti turvalliseksi eikä välitä sitä sovelluksen seulottavaksi. Sovellus suodattaa vain tuntemattomia numeroita.

---

## 📱 Käyttöohje

1.  **Palvelun aktivointi:**
    * Avaa sovellus ja käännä **Puhelunestopalvelu käytössä** -kytkin päälle.
    * Android avaa järjestelmäikkunan, jossa kysytään lupaa asettaa CallBlocker oletusarvoiseksi puhelunestosovellukseksi. Hyväksy pyyntö.
2.  **Estojen määritys:**
    * Kytke haluamasi estot (Ulkomaiset / Spam) päälle.
    * Lisää tarvittavat numerot listoille.
    * **Muista painaa "Tallenna asetukset" -painiketta**, jotta asetukset kirjoitetaan laitteen muistiin!
3.  **Palvelun poistaminen:**
    * Android-tietoturvan vuoksi oletussovelluksen roolia ei voi sulkea suoraan koodilla.
    * Kun käännät kytkimen pois päältä, sovellus ohjaa sinut suoraan Androidin **Oletussovellukset**-asetuksiin, jossa voit vaihtaa puhelunestosovellukseksi takaisin laitteen oman järjestelmäpuhelimen.

OnePlus Nord CE 3 Litessä (OxygenOS-käyttöjärjestelmässä) oletussovellusten valikko on piilotettu hieman eri tavalla kuin puhtaassa Androidissa. Koska Androidin tietoturva estää sovellusta poistamasta omaa oletusrooliaan, sinun täytyy käydä klikkaamassa se pois OnePlussan asetuksista.

## Tässä on tarkat, vaiheittaiset ohjeet palvelun poistamiseen juuri OnePlus CE lite 3 puhelinmallillasi:

Vaiheittainen ohje OnePlus Nord CE 3 Litelle:
Avaa puhelimen Asetukset (ratasikoni).

Skrollaa alaspäin ja valitse Sovellukset (Apps).

Valitse heti ylhäältä Oletussovellukset (Default apps).

Etsi listalta kohta Puhelunestosovellus (joissain OxygenOS-versioissa nimellä Häiriöpuhelut ja roska-asetukset tai Caller ID & spam app).

Klikkaa sitä, ja vaihda valinta CallBlocker -> Puhelin (järjestelmän oletus, jossa on Googlen sininen luuri-ikoni).
---

## 🧪 Testaus emulaattorilla

Koska sovellus ei tietoturvasyistä koske puhelimen muistiin tallennettuihin tuttuihin kontakteihin, testaus kannattaa tehdä Android Studion emulaattorilla:

1.  Varmista Logcatista, että sovellus sai roolin (`onResume: Role held = true`).
2.  Avaa emulaattorin sivupaneelista kolme pistettä (`...`) $\rightarrow$ **Phone**.
3.  Syötä *From number* -kenttään jokin tuntematon numero (esim. ulkomainen numero `+447123456`).
4.  Paina **Call Device**.
5.  Seuraa Logcatia: sinne pitäisi ilmestyä `CallScreeningService STARTED` ja `CALL BLOCKED`, ja puhelu katkeaa automaattisesti. Lokasivulle ilmestyy siisti pvm/klo-merkintä estosta.

---

## 🔒 Tietoturva ja Tekniikka

* **100 % Paikallinen:** Sovellus ei pyydä internet-oikeuksia (`INTERNET`), eikä se lähetä mitään tietoja laitteen ulkopuolelle.
* **Ei tietokantaraskautta:** Lokit ja listat tallennetaan suorituskykyisesti Androidin `SharedPreferences`-välimuistiin, ja raskaat I/O-tallennukset on eriytetty taustasäikeisiin, jotta puhelun katkaisu tapahtuu millisekunneissa ilman laitteen hidastumista.
* Yhteensopiva Android 10+ (API 29) laitteiden kanssa.

---

## 📜 Lisenssi

Lisensoitu **GNU GPLv3** -lisenssillä. Katso tarkemmat ehdot: https://www.gnu.org/licenses/gpl-3.0.en.html


---

## 🔒 Tietoturva

- Sovellus ei kerää eikä lähetä tietoja internetiin  
- Lokit tallennetaan vain laitteen muistiin  
- Sovellus toimii kaikissa Android 10+ laitteissa  
- OnePlus CE Lite 3 toimii nyt 100 % varmasti  
- Call Screening ‑roolia ei enää käytetä  

---

## 📜 Lisenssi — GNU GPLv3

Sovellus on lisensoitu **GNU GPLv3** ‑lisenssillä.  
Lisenssi takaa käyttäjien vapaudet ja avoimuuden.

Lisenssin koko teksti:  
https://www.gnu.org/licenses/gpl-3.0.en.html
