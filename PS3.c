#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE_LENGTH 100
#define MAX_WORD_LENGTH 50

typedef struct {
    char mnemonic[MAX_WORD_LENGTH];
    char label[MAX_WORD_LENGTH];
    char operand[MAX_WORD_LENGTH];
    char loop_counter[MAX_WORD_LENGTH];
    char opcode[MAX_WORD_LENGTH];
} LiteralEntry;

typedef struct {
    char key[MAX_WORD_LENGTH];
    char value[2][MAX_WORD_LENGTH];
} MOTEntry;

typedef struct {
    char key[MAX_WORD_LENGTH];
    int value;
} POTEntry;

typedef struct {
    char key[MAX_WORD_LENGTH];
    char value[MAX_WORD_LENGTH];
} SymbolEntry;

MOTEntry motTable[] = {
    {"ADD", {"1", "1"}},
    {"SUB", {"2", "1"}},
    {"MULT", {"3", "1"}},
    {"JMP", {"4", "1"}},
    {"JNEG", {"5", "1"}},
    {"JPOS", {"6", "1"}},
    {"JZ", {"7", "1"}},
    {"LOAD", {"8", "1"}},
    {"STORE", {"9", "1"}},
    {"READ", {"10", "1"}},
    {"WRITE", {"11", "1"}},
    {"STOP", {"12", "1"}}
};
int motTableSize = sizeof(motTable) / sizeof(motTable[0]);

POTEntry pseudoOps[] = {
    {"DB", 1},
    {"DW", 1},
    {"ORG", 1},
    {"ENDP", 0},
    {"CONST", 1},
    {"END", 0}
};
int pseudoOpsSize = sizeof(pseudoOps) / sizeof(pseudoOps[0]);

LiteralEntry literalTable[100];
int literalTableSize = 0;

SymbolEntry symbolTable[100];
int symbolTableSize = 0;

void printMOT() {
    printf("Machine Operation Table (MOT):\n");
    printf("Mnemonic\tOpcode\tNumber of Operands\n");
    for (int i = 0; i < motTableSize; i++) {
        printf("%s\t\t%s\t%s\n", motTable[i].key, motTable[i].value[0], motTable[i].value[1]);
    }
    printf("\n");
}

void printPOT() {
    printf("Pseudo Operation Table (POT):\n");
    printf("Pseudo Opcode\tNumber of Operands\n");
    for (int i = 0; i < pseudoOpsSize; i++) {
        printf("%s\t\t\t%d\n", pseudoOps[i].key, pseudoOps[i].value);
    }
    printf("\n");
}

void processLine(char *line, int *locationCounter, int *stopEncountered) {
    char words[3][MAX_WORD_LENGTH] = {{0}};
    int wordCount = 0;
    
    char *token = strtok(line, " \t");
    while (token != NULL && wordCount < 3) {
        strcpy(words[wordCount], token);
        wordCount++;
        token = strtok(NULL, " \t");
    }

    LiteralEntry entry;
    strcpy(entry.mnemonic, wordCount >= 2 ? words[1] : words[0]);
    strcpy(entry.label, wordCount == 3 ? words[0] : "");
    strcpy(entry.operand, wordCount == 3 ? words[2] : (wordCount == 2 ? words[1] : ""));
    sprintf(entry.loop_counter, "%d", *locationCounter);

    int isMOT = 0;
    for (int i = 0; i < motTableSize; i++) {
        if (strcmp(motTable[i].key, entry.mnemonic) == 0) {
            strcpy(entry.opcode, motTable[i].value[0]);
            isMOT = 1;
            break;
        }
    }
    if (!isMOT) {
        strcpy(entry.opcode, "*");
    }

    if (strcmp(entry.mnemonic, "ENDP") == 0) {
        // do nothing
    } else if (!isMOT) {
        (*locationCounter)++;
    } else if (strcmp(entry.mnemonic, "STOP") == 0) {
        (*locationCounter)++;
        *stopEncountered = 1;
    } else {
        *locationCounter += *stopEncountered ? 1 : 2;
    }

    literalTable[literalTableSize++] = entry;

    if (wordCount == 3) {
        int found = 0;
        for (int i = 0; i < symbolTableSize; i++) {
            if (strcmp(symbolTable[i].key, words[0]) == 0) {
                found = 1;
                break;
            }
        }
        if (!found) {
            SymbolEntry symbol;
            strcpy(symbol.key, words[0]);
            strcpy(symbol.value, entry.loop_counter);
            symbolTable[symbolTableSize++] = symbol;
        }
    }
}

void printLiteralTable() {
    printf("\nLiteral Table (LT):\n");
    printf("Mnemonic\tLabel\tOperand\tLoop Counter\tOpcode\tAddress\n");
    for (int i = 0; i < literalTableSize; i++) {
        LiteralEntry entry = literalTable[i];
        int length = strlen(entry.mnemonic) + strlen(entry.operand);
        printf("%s\t\t%s\t%s\t%s\t\t%s\t", entry.mnemonic, entry.label, entry.operand, entry.loop_counter, entry.opcode);
        int addressFound = 0;
        for (int j = 0; j < symbolTableSize; j++) {
            if (strcmp(symbolTable[j].key, entry.operand) == 0) {
                printf("%s", symbolTable[j].value);
                addressFound = 1;
                break;
            }
        }
        if (!addressFound) {
            printf("");
        }
        printf("\n");
    }
}

void printSymbolTable() {
    printf("\nSymbol Table:\n");
    printf("Mnemonic\tAddress\n");
    for (int i = 0; i < symbolTableSize; i++) {
        printf("%s\t\t%s\n", symbolTable[i].key, symbolTable[i].value);
    }
}

int main() {
    printMOT();
    printPOT();

    FILE *file = fopen("input8.txt", "r");
    if (file == NULL) {
        perror("Failed to open file");
        return EXIT_FAILURE;
    }

    char line[MAX_LINE_LENGTH];
    int locationCounter = 0;
    int stopEncountered = 0;

    while (fgets(line, sizeof(line), file)) {
        // Remove newline character
        line[strcspn(line, "\n")] = 0;
        processLine(line, &locationCounter, &stopEncountered);
    }

    fclose(file);

    printLiteralTable();
    printSymbolTable();

    return 0;
}
