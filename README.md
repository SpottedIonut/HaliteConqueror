
HaliteConqueror este un bot dezvoltat în Java pentru jocul Halite, care cucerește rapid și eficient
întreaga hartă printr-un bazat pe „fluxuri” (streams) și inspirat din algoritmul A*.

Algoritmul funcționează astfel:

 - Analiza celulelor:  Pentru fiecare celulă deținută, botul distinge două situații principale:
                       celulele de frontieră (vecinătate cu celule necucerite) și celulele complet
                       înconjurate de celulele proprii.
 - Calculul scorurilor: Fiecare celulă își evaluează vecinii (NORD, EST, SUD, VEST) prin calcularea
                        unui scor, care ține cont de producție, distanță și efortul necesar pentru
                        cucerirea celulelor adiacente.
 - Determinarea direcției: Celula își actualizează direcția de „flux” pe baza celui mai mic scor găsit,
                           ceea ce determină direcționarea optimă a forței de producție spre zonele
                           strategice.
 - Colaborarea între celule: Celulele din interior, care sunt deja controlate de bot, își coordonează
                             mișcările, sprijinindu-se reciproc pentru a concentra eficient resursele
                             și a crește rapid producția în zonele cu potențial ridicat.
   
Această strategie, inspirată din logica algoritmului A*, permite botului să se adapteze dinamic la starea hărții și să maximizeze expansiunea prin decizii tactice de nivel local, care se reflectă la scară globală.
