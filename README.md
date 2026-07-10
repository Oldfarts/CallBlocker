# CallBlocker
Estä ulkomaanpuhelut sekä häirikkösoittajat


<img width="331" height="698" alt="image" src="https://github.com/user-attachments/assets/8c8619a5-a13a-47d2-9d66-1e60ee08e0c0" />


README.md — CallBlocker (2026, OnePlus‑yhteensopiva)
markdown
# 📞 CallBlocker — Androidin puhelunestopalvelu (2026)

CallBlocker on Androidin **AccessibilityService‑pohjainen puhelunestopalvelu**, joka toimii
luotettavasti myös OnePlus‑puhelimissa (mm. CE Lite 3), joissa Google Dialer estää
perinteisen Call Screening ‑roolin.

Sovellus estää ei‑toivotut puhelut automaattisesti taustalla.  
Sovellusta ei tarvitse pitää auki — Android käynnistää palvelun aina kun puhelu saapuu.

---

## 🔧 Ominaisuudet

### ✔ Estä ulkomaiset puhelut
Estää kaikki ulkomaiset puhelut yhdellä kytkimellä.  
Suomi (+358) sallitaan automaattisesti.

### ✔ Sallitut maatunnukset
Voit sallia yksittäisiä ulkomaisia maatunnuksia, kuten:
- +44 (Iso‑Britannia)
- +1 (USA)
- +420 (Tšekki)

### ✔ Sallitut numerot
Voit sallia yksittäisiä numeroita, jotka ohittavat kaikki estot.

### ✔ Kielletyt numerot
Voit estää yksittäisiä numeroita täsmällisesti.

### ✔ Häirikkösoittajien esto
Androidin Caller ID tunnistaa “Mahdollinen häirikkösoittaja” ‑puhelut.  
Kun esto on päällä → puhelu katkaistaan automaattisesti.

### ✔ Selkeä loki
Jokaisesta estetystä puhelusta tallennetaan:
- aikaleima  
- numero  
- syy estolle  

Esimerkki:
2026‑07‑09 08:15 — Numero: +447123456789 — Syy: Ulkomainen numero — maatunnus ei sallittu

Koodi

### ✔ Spam‑raportti
Kaikki häirikkösoittajat tallennetaan erilliseen raporttiin.

---

## 🛠 Toimintalogiikka (2026, OnePlus‑yhteensopiva)

### 🔥 1. Sovellus ei käytä Call Screening ‑roolia
OnePlus CE Lite 3 ja monet muut mallit **estävät Call Screening ‑roolin**.  
Siksi sovellus käyttää **AccessibilityService‑pohjaista puhelunestoa**, joka toimii kaikissa malleissa.

### 🔥 2. Accessibility‑palvelu havaitsee saapuvan puhelun
Kun puhelu saapuu:
- AccessibilityService saa eventin  
- Palvelu yrittää lukea numeron  
- Jos numero ei näy (OnePlus piilottaa UI:n) → käytetään varmaa katkaisua

### 🔥 3. OnePlus‑varma puhelunesto
Jos numeroa ei voi lukea, palvelu katkaisee puhelun:

GLOBAL_ACTION_BACK
GLOBAL_ACTION_HOME

Koodi

Tämä toimii **kaikissa OnePlus‑malleissa**, myös CE Lite 3:ssa.

### 🔥 4. Estologiikka

| Ehto | Toiminta |
|------|----------|
| Numero sallittujen listalla | Puhelu päästetään läpi |
| Numero kiellettyjen listalla | Puhelu katkaistaan |
| Ulkomaiset estetty & numero ei ole suomalainen | Puhelu katkaistaan |
| Spam‑esto päällä & numero ulkomainen | Puhelu katkaistaan |
| Numero ei näy (OnePlus) | Puhelu katkaistaan varmistetulla menetelmällä |

---

## 📱 Käyttöohje

### 1. Ota puhelunesto käyttöön
Avaa:

**Asetukset → Helppokäyttöisyys → Lataamasi palvelut → CallBlocker**

Ota palvelu käyttöön.

### 2. Aseta estot sovelluksessa
- Estä ulkomaiset puhelut  
- Estä häirikkösoittajat  
- Lisää sallitut maatunnukset  
- Lisää sallitut numerot  
- Lisää kielletyt numerot  

### 3. Sovellus toimii taustalla
Sovellusta ei tarvitse pitää auki.  
Android käynnistää palvelun automaattisesti puhelun yhteydessä.

---

## 🧪 Testaus

### Ulkomaiset puhelut
1. Laita **Estä ulkomaiset puhelut** päälle  
2. Soita ulkomaisesta numerosta  
3. Puhelu katkeaa → lokiin tulee merkintä

### Sallittu maatunnus
1. Lisää esim. **+44** sallittuihin  
2. Soita +44‑numerosta  
3. Puhelu tulee läpi

### Häirikkösoittaja
1. Laita **Estä häirikkösoittajat** päälle  
2. Soita numerosta, jonka Android merkitsee “Mahdollinen häirikkösoittaja”  
3. Puhelu katkeaa → spam‑raporttiin tulee merkintä

### OnePlus‑varma katkaisu
1. Soita numerosta, jota ei näytetä ruudulla  
2. Puhelu katkeaa silti  
3. Lokissa näkyy:  
   **“OnePlus‑varma katkaisu”**

---

## 📂 Projektin rakenne

<img width="648" height="447" alt="image" src="https://github.com/user-attachments/assets/a2d32499-5c47-4b80-97ae-d061c988f7d6" />


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
