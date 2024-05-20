# include <stdio.h>
# include <stdlib.h>
# include <string.h>
# include <ctype.h>


# define MAX_VARIABLES 100
# define MAX_NAME_LENGTH 50


struct Variable {
    char name[MAX_NAME_LENGTH];
    char type[MAX_NAME_LENGTH];
    int length;
    int address;
};



struct Variable symbolTable[MAX_VARIABLES];
int symbolCount = 0;



void addToSymbolTable(const char* name, const char *type, size_t length, int address, int index){
    if(index < MAX_VARIABLES){
        strcpy(symbolTable[index].name, name);
        strcpy(symbolTable[index].type, type);
        symbolTable[index].length = length;
        symbolTable[index].address = address;
        index++;
    }

}

void updateSymbolTable(const char *type, size_t length, int index){
    if(index < MAX_VARIABLES){
        strcpy(symbolTable[index].type, type);
        symbolTable[index].length = length;
        index++;
    }

}

int main(){
    FILE* file = fopen("example.c", "r");

    char line[100];
    int address = 1000;
    int index = 0;
    char* type = NULL;
    while(fgets(line, sizeof(line), file)){
        // printf("1");
        char *token = strtok(line, " ();,\n\t");
        // printf("%s \n", token);
        while(token != NULL){
            // printf("%s \n", token);
            if(strcmp(token, "int") == 0 || strcmp(token, "char") == 0 || strcmp(token, "float") == 0 || strcmp(token, "double") == 0){
                size_t size = -1;
                if(strcmp(token, "int") == 0){
                    size = sizeof(int);
                    type = "int";
                } else if(strcmp(token, "char") == 0){
                    size = sizeof(char);
                    type = "char";
                } else if(strcmp(token, "float") == 0){
                    size = sizeof(float);
                    type = "float";
                } else if(strcmp(token, "double") == 0){
                    size = sizeof(double);
                    type = "double";
                }
                token = strtok(NULL, " ();,\n\t");
                // printf("%s \n", token);
                
                while(token != NULL && token[0] != ';'){
                    // printf("%s \n", token);
                    //! Handling the case if the syntax used for writing the array is array [100];
                    //? Updating the symbol table if the syntax for writing the array is array [100]
                if(isalpha(token[0]) || token[0] == "_"){
                        if(strcmp(token, "main") != 0){
                            addToSymbolTable(token, strdup(type), size, address, index);
                            address += size;
                            index ++;
                        }
                    }
                    token = strtok(NULL, " ();,\n\t");
                }

            } else {
                char* temp = strdup(token);
                token = strtok(NULL, " ();,\n\t");
                printf("%s \n", token);



            }
            token = strtok(NULL, " ();,\n\t");
        }

    }

    fclose(file);

    printf("Symbol Table:\n");
    printf("-------------------------------------------------\n");
    printf("| %-10s | %-10s | %-10s | %-10s |\n", "Symbol", "Type", "Length", "Address");
    printf("-------------------------------------------------\n");
    for (int i = 0; i < index; i++) {
        printf("| %-10s | %-10s | %-10d | %-10d |\n", symbolTable[i].name, symbolTable[i].type, symbolTable[i].length, symbolTable[i].address);
    }
    printf("-------------------------------------------------\n");



}