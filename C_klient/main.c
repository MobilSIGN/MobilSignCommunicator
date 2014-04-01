#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <inttypes.h>
#include <unistd.h>

#define DEBUG 0
#define BUFFER 4096


/**
 * funkcia spracuje prijatu spravu
 * @param sprava
 */
void spracujSpravu(char sprava[]) {
    
}



int main(int argc, const char** argv) {

    char* ukoncovaciaSprava = "KONIEC"; //sprava, ktora ukonci program    
    int maxZnakov = 256; //maximalny pocet znakov, ktory je mozne poslat v sprave
    char sprava[maxZnakov];

    do {      

        sleep(1); //pockam 5 sekund
        
        //poslem spravu JAVE
        printf("Posielam spravu do JAVY\n");
        fflush(stdout);
        
        //precitam si odpoved
        int pocetZnakovSpravy = dajSpravu(&sprava, maxZnakov);
        spracujSpravu(sprava);
        

    } while (strcmp(sprava, ukoncovaciaSprava) != 0); //koncim, ked pride ukoncovacia sprava

    return 1;
}


/* Read one line from standard input, */
/* copying it to line array (but no more than max chars). */
/* Does not place terminating \n in line array. */

/* Returns line length, or 0 for empty line, or EOF for end-of-file. */

int dajSpravu(char line[], int max) {
    int nch = 0;
    int c;
    max = max - 1; /* leave room for '\0' */

    while ((c = getchar()) != EOF) {
        if (c == '\n')
            break;

        if (nch < max) {
            line[nch] = c;
            nch = nch + 1;
        }
    }

    if (c == EOF && nch == 0)
        return EOF;

    line[nch] = '\0';
    return nch;
}


