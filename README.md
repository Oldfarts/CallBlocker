# CallBlocker
Estä ulkomaanpuhelut sekä häirikkösoittajat

<img width="395" height="862" alt="image" src="https://github.com/user-attachments/assets/7f647bbd-8f04-4a01-8e1e-ac977e4f08da" />


# 📞 CallBlocker — Androidin puhelunestopalvelu

CallBlocker on Androidin **Call Screening Service** ‑pohjainen sovellus, joka estää
ei‑toivotut puhelut automaattisesti taustalla. Sovellus ei tarvitse olla auki —
Android käynnistää sen aina kun puhelu saapuu.

## 🔧 Ominaisuudet

### ✔ Estä ulkomaiset puhelut
Voit estää kaikki ulkomaiset puhelut yhdellä kytkimellä.  
Suomi (+358) sallitaan aina automaattisesti.

### ✔ Sallitut maatunnukset (poikkeukset)
Voit sallia yksittäisiä ulkomaisia maatunnuksia, kuten:
- +44 (Iso‑Britannia)
- +1 (USA)
- +420 (Tšekki)

### ✔ Sallitut numerot (poikkeukset)
Voit sallia yksittäisiä numeroita, jotka ohittavat kaikki estot.

### ✔ Kielletyt numerot
Voit estää yksittäisiä numeroita täsmällisesti.

### ✔ Estä häirikkösoittajat
Androidin oma Caller ID tunnistaa “Mahdollinen häirikkösoittaja” ‑puhelut.  
Kun tämä kytkin on päällä → puhelu estetään automaattisesti.

### ✔ Selkeä loki
Jokaisesta estetystä puhelusta tallennetaan:
- aikaleima
- numero
- syy miksi puhelu estettiin

Esimerkki:
2026-07-09 08:15 — Numero: +447123456789 — Syy: Ulkomainen numero — maatunnus ei sallittu

Koodi

### ✔ Spam‑raportti
Kaikki häirikkösoittajat tallennetaan erilliseen spam‑raporttiin aikaleiman kanssa.

---

## 🛠 Käyttöohje

### 1. Ota puhelunestopalvelu käyttöön
Avaa sovellus → laita kytkin **Puhelunestopalvelu käytössä** päälle.  
Android pyytää lupaa toimia puhelunestopalveluna.

### 2. Aseta estot
- Estä ulkomaiset puhelut  
- Estä häirikkösoittajat  
- Lisää sallitut maatunnukset  
- Lisää sallitut numerot  
- Lisää kielletyt numerot

### 3. Sovellus toimii taustalla
Sovellusta ei tarvitse pitää auki.  
Android käynnistää CallBlockerServicen automaattisesti jokaisen puhelun yhteydessä.

---

## 🧪 Testaus

### Testaa ulkomaisten puheluiden esto
1. Laita **Estä ulkomaiset puhelut** päälle  
2. Soita ulkomaisesta numerosta  
3. Puhelu ei soi → lokiin tulee merkintä

### Testaa sallittu maatunnus
1. Lisää esim. **+44** sallittuihin maatunnuksiin  
2. Soita +44‑numerosta  
3. Puhelu tulee läpi

### Testaa häirikkösoittajan esto
1. Laita **Estä häirikkösoittajat** päälle  
2. Soita numerosta, jonka Android merkitsee “Mahdollinen häirikkösoittaja”  
3. Puhelu estyy → spam‑raporttiin tulee merkintä

---

## 📂 Projektin rakenne

<img width="657" height="443" alt="image" src="https://github.com/user-attachments/assets/b7176ded-0976-4812-afb7-2381d1adece2" />


Koodi

---

## 🔒 Huomioita

- Sovellus ei kerää eikä lähetä tietoja internetiin.
- Kaikki lokit tallennetaan vain laitteen omaan muistiin.
- Sovellus toimii vain Androidissa, joka tukee Call Screening Serviceä (Android 10+).

---

## 🔒 Lisenssi — GNU General Public License v3.0

Tämä projekti on lisensoitu **GNU GPLv3** ‑lisenssillä.

Lyhyesti:
- Voit käyttää, muokata ja jakaa ohjelmaa vapaasti  
- Muokatut versiot on julkaistava samalla lisenssillä  
- Lähdekoodi on pidettävä avoimena, jos ohjelmaa jaetaan  
- Lisenssi takaa käyttäjien vapaudet ja avoimuuden

Lisenssin koko teksti löytyy täältä:  
https://www.gnu.org/licenses/gpl-3.0.en.html
